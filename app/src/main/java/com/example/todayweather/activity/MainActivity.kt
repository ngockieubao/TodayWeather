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
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.todayweather.R
import com.example.todayweather.base.WeatherViewModelFactory
import com.example.todayweather.databinding.ActivityMainBinding
import com.example.todayweather.receiver.LocationImpl
import com.example.todayweather.receiver.LocationReceiver
import com.example.todayweather.receiver.NetworkReceiver
import com.example.todayweather.receiver.NotificationReceiver
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

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // fullscreen, no status bar, no title bar :))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // NavHost Activity
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.actNavHost) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)

        // Hide header & bottom navigation when navigate to conversation
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    binding.bottomNav.visibility = View.VISIBLE
                }
                R.id.searchFragment -> {
                    binding.bottomNav.visibility = View.GONE
                }
                R.id.settingFragment -> {
                    binding.bottomNav.visibility = View.GONE
                }
                else -> {
                    binding.bottomNav.visibility = View.VISIBLE
                }
            }
        }

        val options = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.from_right)
            .setExitAnim(R.anim.to_left)
            .setPopEnterAnim(R.anim.from_left)
            .setPopExitAnim(R.anim.to_right)
            .build()

        val optionsBack = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.from_left)
            .setExitAnim(R.anim.to_right)
            .setPopEnterAnim(R.anim.from_right)
            .setPopExitAnim(R.anim.to_left)
            .build()

        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment, null, optionsBack)
                }
                R.id.dailyFragment -> {
                    navController.navigate(R.id.dailyFragment, null, options)
                }
                R.id.hourlyFragment -> {
                    navController.navigate(R.id.hourlyFragment, null, options)
                }
            }
            true
        }

//        binding.progressCircular.indeterminateDrawable = WanderingCubes()

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

        weatherViewModel.hasLocationChange.observe(this) {
            if (it == null) return@observe
            else
                if (it == false) {
                    val snackbar = createSnackbar(
                        this.getString(R.string.string_location_off),
                        Constants.TIME_IMMORTAL
                    )
                    snackbar.view.setBackgroundResource(R.color.red)
                    snackbar.setActionTextColor(R.color.black).setAction("Bật") {
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(intent)
                    }.show()
                }
        }
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
//            startPushNotifications()
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
                Snackbar.make(binding.root, "Permissions granted.", Snackbar.LENGTH_SHORT).show()
                weatherViewModel.getLastLocation()
//                startPushNotifications()
            } else {
                Snackbar.make(binding.root, "Permissions denied.", Snackbar.LENGTH_SHORT).show()
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
        weatherViewModel.stopLocationUpdates()
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

    @SuppressLint("ResourceAsColor")
    override fun onLocationChange(status: String) {
        when (status) {
            "off" -> {
                val snackbar = createSnackbar(
                    this.getString(R.string.string_location_off),
                    Constants.TIME_IMMORTAL
                )
                snackbar.view.setBackgroundResource(R.color.red)
                snackbar.setActionTextColor(R.color.black).setAction("Bật") {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }.show()
            }
            "on" -> {
                weatherViewModel.locationChange()
                val snackbar = createSnackbar(
                    this.getString(R.string.string_location_on),
                    Constants.TIME_SHORT
                )
                snackbar.view.setBackgroundResource(R.color.green)
                snackbar.show()
            }
            else -> Log.d(TAG, "onLocationChange: status is null")
        }
    }

    private fun createSnackbar(message: String, TIME: Int): Snackbar {
        val snackbar = Snackbar.make(binding.root, message, TIME)
        snackbar.setText(message)
        return snackbar
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}