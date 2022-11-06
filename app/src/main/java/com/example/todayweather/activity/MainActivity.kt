package com.example.todayweather.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.todayweather.R
import com.example.todayweather.base.WeatherViewModelFactory
import com.example.todayweather.receiver.NotificationReceiver
import com.example.todayweather.receiver.NetworkReceiver
import com.example.todayweather.databinding.ActivityMainBinding
import com.example.todayweather.receiver.LocationImpl
import com.example.todayweather.receiver.LocationReceiver
import com.example.todayweather.ui.WeatherViewModel
import com.example.todayweather.util.Constants
import com.google.android.material.snackbar.Snackbar

@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.Q)
class MainActivity : AppCompatActivity(), LocationImpl {

    private lateinit var binding: ActivityMainBinding
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var mNetworkReceiver: BroadcastReceiver
    private lateinit var mLocationReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weatherViewModel = ViewModelProvider(
            this,
            WeatherViewModelFactory(this.application)
        )[WeatherViewModel::class.java]

        checkPermissions()
        weatherViewModel.createLocationRequest()
        weatherViewModel.createLocationCallback()

        mNetworkReceiver = NetworkReceiver()
        mLocationReceiver = LocationReceiver(this)
        registerNetworkBroadcastForNougat()
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                Constants.REQUEST_PERMISSION_CODE
            )
        } else {
            weatherViewModel.getLastLocation()
            startPushNotifications()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
                weatherViewModel.getLastLocation()
                startPushNotifications()
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
                openSettingPermissions()
                Toast.makeText(this, "Location must allow", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openSettingPermissions() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts(Constants.PACKAGE, packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
//        if (requestingLocationUpdates) startLocationUpdates()
    }

    private fun stopLocationUpdates() {
        weatherViewModel.fusedLocationClient.removeLocationUpdates(weatherViewModel.locationCallback)
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(
                mNetworkReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
            registerReceiver(
                mLocationReceiver,
                IntentFilter(LocationManager.MODE_CHANGED_ACTION)
            )
        }
    }

    private fun unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver)
            unregisterReceiver(mLocationReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun startPushNotifications() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java).apply {
            action = Constants.BROADCAST_RECEIVER_PUSH_NOTIFICATIONS
        }
        weatherViewModel.pushNotifications(this, intent, alarmManager)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.actNavHost)
        return navController.navigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkChanges()
    }

    override fun onLocationChange(status: String) {
        when (status) {
            "off" -> Snackbar.make(binding.root, "Định vị đang tắt. Vui lòng bật để sử dụng!", Snackbar.LENGTH_INDEFINITE).show()
            "on" -> Snackbar.make(binding.root, "Định vị đã bật.", Snackbar.LENGTH_SHORT).show()
            else -> Log.d(TAG, "onLocationChange: status is null")
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}