package com.example.todayweather

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.todayweather.broadcast.NotificationReceiver
import com.example.todayweather.broadcast.WeatherReceiver
import com.example.todayweather.databinding.ActivityMainBinding
import com.example.todayweather.ui.WeatherViewModel
import com.example.todayweather.util.Constants
import com.example.todayweather.util.Utils
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val weatherViewModel: WeatherViewModel by lazy {
        ViewModelProvider(
            this,
            WeatherViewModel.WeatherViewModelFactory(this.application)
        )[WeatherViewModel::class.java]
    }

    // Init variable location
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var getPosition: String = ""
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var requestingLocationUpdates: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate: Main")

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Provides method to retrieve device location information
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        checkPermissionLocation()

        // Update location
        initLocationRequest()
        initLocationCallback()
    }

    private val localPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    )
    { permission ->
        if (permission == true) {
            getLastLocation()
            startBroadcast()
//            checkPermissionLocation()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            setupPermission()
        }
    }

    // Show dialog to request permission location
    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Handles conditions permission
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Run secondly show dialog with 3 choices
                Toast.makeText(this, "Permission accepted", Toast.LENGTH_SHORT).show()
                localPermissionRequest.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            } else {
                // Run firstly show dialog with 2 choices
                Toast.makeText(this, "Permission accepted", Toast.LENGTH_SHORT).show()
                localPermissionRequest.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        } else {
            TODO("VERSION.SDK_INT < M")
        }
    }

    // Navigation by nav host
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.actNavHost)
        return navController.navigateUp()
    }

    // Navigate to setup permission
    private fun setupPermission() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts(Constants.PACKAGE, packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    // Check permission location
    private fun checkPermissionLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLastLocation()
            startBroadcast()
        } else {
            requestLocationPermission()
        }
    }

    // Get location
    private fun getLocation(lat: Double, lon: Double) {
        try {
            val geocoder = Geocoder(this)
            val position = geocoder.getFromLocation(lat, lon, 1)

            // Display location
            getPosition = position[0].getAddressLine(0)
            weatherViewModel.showLocation(Utils.formatLocation(this, getPosition))

            // Pass lat-lon args after allow position
            weatherViewModel.refreshData(lat, lon)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    private fun getLastLocation() {
        // Get last location
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient!!.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        val lat = location.latitude
                        val lon = location.longitude
                        try {
//                            val geocoder = Geocoder(this)
//                            val position = geocoder.getFromLocation(lat, lon, 1)
//
//                            // Assign location
//                            getPosition = position[0].getAddressLine(0)
//                            // Pass lat-lon args after allow position
//                            weatherViewModel.refreshData(lat, lon)
//                            // Display location
//                            weatherViewModel.showLocation(Utils.formatLocation(this, getPosition))
                            getLocation(lat, lon)
//                            weatherViewModel.refreshData(lat, lon)
                        } catch (e: Exception) {
                            Log.w("bugLocation", e)
                        }
                    } else {
                        startLocationUpdates()
                    }
                }
                .addOnFailureListener {}
                .addOnCompleteListener {}
            return
        }
    }

    // Init locationRequest
    private fun initLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = Constants.INTERVAL
            fastestInterval = Constants.FASTEST_INTERVAL
            priority = PRIORITY_HIGH_ACCURACY
        }
    }

    // Init locationCallback
    private fun initLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Update UI with location data
                    // Get lat-lon
                    val latUpdate = location.latitude
                    val lonUpdate = location.longitude

                    // Get location
                    getLocation(latUpdate, lonUpdate)
                }
            }
        }
    }

    // Update location
    private fun startLocationUpdates() {
        try {
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        }
    }

    // Stop update location
    private fun stopLocationUpdates() {
        fusedLocationClient!!.removeLocationUpdates(locationCallback)
    }

    private fun startBroadcast() {
        startBroadcastNetwork()
//        startBroadcastWeatherNotifications()
    }

    // Broadcast
    private fun startBroadcastNetwork() {
        val intent = Intent(this, WeatherReceiver::class.java).apply {
            action = Constants.BROADCAST_RECEIVER_NETWORK_STATUS
        }
        sendBroadcast(intent)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun startBroadcastWeatherNotifications() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java).apply {
            action = Constants.BROADCAST_RECEIVER_PUSH_NOTIFICATIONS
        }

        val pendingIntentRequestCode = 0
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            pendingIntentRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val isPermission = alarmManager.canScheduleExactAlarms()
            if (isPermission) {
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + 1_000,
                    TimeUnit.SECONDS.toMillis(10),
                    pendingIntent
                )
            } else {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + 60_000,
                    TimeUnit.SECONDS.toMillis(10),
                    pendingIntent
                )
            }
        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 60_000,
                TimeUnit.SECONDS.toMillis(10),
                pendingIntent
            )
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: Main")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Main")
//        if (requestingLocationUpdates) startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: Main")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: Main")
//        stopLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Main")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart: Main")
    }

    companion object {
        private const val TAG = "Home"
    }
}