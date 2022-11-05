package com.example.todayweather.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.todayweather.R
import com.example.todayweather.base.WeatherViewModelFactory
import com.example.todayweather.receiver.NotificationReceiver
import com.example.todayweather.receiver.WeatherReceiver
import com.example.todayweather.databinding.ActivityMainBinding
import com.example.todayweather.ui.WeatherViewModel
import com.example.todayweather.util.Constants

@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.Q)
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var mNetworkReceiver: BroadcastReceiver

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

        mNetworkReceiver = WeatherReceiver()
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
        }
    }

    private fun unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver)
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

    companion object {
        private const val TAG = "MainActivity"
}
}