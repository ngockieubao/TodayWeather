package com.example.todayweather.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.todayweather.R
import com.example.todayweather.network.WeatherApi
import com.example.todayweather.ui.home.model.WeatherGetApi
import com.example.todayweather.util.Constants
import com.example.todayweather.util.Utils
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import java.io.IOException

@Suppress("DEPRECATION")
class PushNotificationService : Service() {
    private var getPosition: String = ""
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var job: Job = Job()
    private val coroutineContext = CoroutineScope(Dispatchers.Main + job)

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        initLocationRequest()
        initLocationCallback()
    }

    override fun onDestroy() {
        job.cancel() // Cancel the Job
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.name_notification_channel)
            val descriptionText = getString(R.string.description_notification_channel)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(
                getString(R.string.id_channel_push_notification),
                name,
                importance
            )
            mChannel.description = descriptionText
            val notificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
        startPushNotification(startId)

        /**
         * Todo: call weather API with Database' latitude and longitude
         * then push notification with weather information
         */
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this@PushNotificationService)
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    coroutineContext.launch {
                        getInfoAndPushNotifications(location, startId)
                    }
                }
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        }
        return START_STICKY
    }

    private suspend fun getInfoAndPushNotifications(location: Location?, startId: Int) {
        val lat: Double
        val lon: Double
        if (location != null) {
            lat = location.latitude
            lon = location.longitude
            val weatherData = WeatherApi.retrofitService.getProperties(lat, lon)
            // Get location
            getLocation(lat, lon)

            // Push Weather Notification
            startPushNotificationShowInfo(weatherData, getPosition)
            delay(Constants.DELAY)
            stopSelf(startId)
        } else {
            startLocationUpdates()
        }
    }

    private fun startPushNotification(startId: Int) {
        // Create the notification to be shown
        val mBuilder: Notification =
            NotificationCompat.Builder(
                this@PushNotificationService,
                getString(R.string.id_channel_push_notification)
            )
                .setSmallIcon(R.mipmap.ic_todayweather_removebg)
                .setLargeIcon(
                    Utils.convertToBitMap(
                        this@PushNotificationService,
                        R.mipmap.ic_weather_news
                    )
                )
                .setAutoCancel(true)
                .setContentText(getString(R.string.notification_waiting_get_weather_information))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
        startForeground(startId, mBuilder)
    }

    @SuppressLint("StringFormatMatches")
    private fun startPushNotificationShowInfo(weatherGetApi: WeatherGetApi, location: String) {
        // Create the notification to be shown
        val mBuilder = NotificationCompat.Builder(
            this@PushNotificationService,
            getString(R.string.id_channel_push_notification)
        )
            .setSmallIcon(R.mipmap.ic_todayweather_removebg)
            .setLargeIcon(
                Utils.convertToBitMap(
                    this@PushNotificationService,
                    R.mipmap.ic_weather_news
                )
            )
            .setAutoCancel(true)
            .setContentTitle(Utils.formatLocation(this@PushNotificationService, location))
            .setContentText(Utils.upCaseFirstLetter(weatherGetApi.current.weather[0].description))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(
                NotificationCompat
                    .BigTextStyle()
                    .bigText(
                        resources.getString(
                            R.string.fm_status_daily_nav,
                            Utils.upCaseFirstLetter(weatherGetApi.current.weather[0].description),
                            weatherGetApi.daily[0].temp.max,
                            weatherGetApi.daily[0].temp.min,
                            Utils.formatWindDeg(
                                this@PushNotificationService,
                                weatherGetApi.daily[0].wind_deg
                            ),
                            weatherGetApi.daily[0].wind_speed,
                            Utils.formatPop(
                                this@PushNotificationService,
                                weatherGetApi.daily[0].pop
                            )
                        )
                    )
            )

        val am = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        am.notify(Constants.ONGOING_NOTIFICATION_ID, mBuilder.build())
    }

    // Init locationCallback
    private fun initLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = Constants.INTERVAL
            fastestInterval = Constants.FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

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

    // Get location
    private fun getLocation(latUpdate: Double, lonUpdate: Double) {
        try {
            val geocoder = Geocoder(this@PushNotificationService)
            val position = geocoder.getFromLocation(latUpdate, lonUpdate, 1)

            // Display location
            getPosition = position[0].getAddressLine(0)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    // Update location
    private fun startLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        }
    }
}


