package com.example.todayweather.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.todayweather.R
import com.example.todayweather.broadcast.WeatherReceiver
import com.example.todayweather.databinding.ActivityMainBinding
import com.example.todayweather.ui.WeatherViewModel
import com.example.todayweather.util.Constants
import com.example.todayweather.util.Utils
import com.google.android.gms.location.*

@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.Q)
class MainActivity : AppCompatActivity() {

    private val REQUEST_PERMISSON_CODE = 10
    private lateinit var binding: ActivityMainBinding
    private lateinit var mNetworkReceiver: BroadcastReceiver

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var getPosition: String = ""
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private val weatherViewModel: WeatherViewModel by lazy {
        ViewModelProvider(
            this,
            WeatherViewModel.WeatherViewModelFactory(this.application)
        )[WeatherViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkPermissions()
        createLocationRequest()
        createLocationCallback()

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
                REQUEST_PERMISSON_CODE
            )
        } else {
            getLastLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSON_CODE) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
                getLastLocation()
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
                openSettingPermissions()
                Toast.makeText(this, "Location must allow", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openSettingPermissions() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude
                    getLocation(lat, lon)
                } else {
                    Toast.makeText(this, "Location can be off", Toast.LENGTH_SHORT).show()
                    startLocationUpdates()
                }
            }
            return
        } else {
            Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getLocation(lat: Double, lon: Double) {
        try {
            val geocoder = Geocoder(this)
            val position = geocoder.getFromLocation(lat, lon, 1)

            weatherViewModel.loadAPI(lat, lon)
            getPosition = position[0].getAddressLine(0)
            weatherViewModel.showLocation(Utils.formatLocation(this, getPosition))

        } catch (e: Exception) {
            Log.d(TAG, "getLastLocation: failed - $e")
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = Constants.INTERVAL
            fastestInterval = Constants.FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Update UI with location data
                    // Get lat-lon
                    val latUpdate = location.latitude
                    val lonUpdate = location.longitude

                    getLocation(latUpdate, lonUpdate)
                }
            }
        }
    }

    private fun startLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e("SecurityException", "$e")
        }
    }

    override fun onResume() {
        super.onResume()
//        if (requestingLocationUpdates) startLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
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