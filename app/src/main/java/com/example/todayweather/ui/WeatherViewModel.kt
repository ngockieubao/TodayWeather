package com.example.todayweather.ui

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.todayweather.R
import com.example.todayweather.data.WeatherRepository
import com.example.todayweather.data.model.Daily
import com.example.todayweather.data.model.DetailHomeModel
import com.example.todayweather.data.model.Hourly
import com.example.todayweather.data.model.WeatherGetApi
import com.example.todayweather.util.Constants
import com.example.todayweather.util.Utils
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class WeatherViewModel(
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val weatherRepository: WeatherRepository = WeatherRepository(application)

    private val _listCurrent = MutableLiveData<Daily>()
    val listCurrent: LiveData<Daily>
        get() = _listCurrent

    var listDataDetail = MutableLiveData<MutableList<DetailHomeModel>>()

    private val _listDailyNav = MutableLiveData<MutableList<Daily>?>()
    val listDataDaily: MutableLiveData<MutableList<Daily>?>
        get() = _listDailyNav

    private val _listHourlyNav = MutableLiveData<MutableList<Hourly>?>()
    val listDataHourly: MutableLiveData<MutableList<Hourly>?>
        get() = _listHourlyNav

    private val _showLocation = MutableLiveData<String?>()
    val showLocation = _showLocation

    private val _networkError = MutableLiveData<Boolean>()
    val networkError: LiveData<Boolean> = _networkError

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var getPosition: String = ""
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private val _mCurrentTime = MutableLiveData<String?>()
    val mCurrentTime = _mCurrentTime

    fun loadApi(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                _networkError.value = false
                weatherRepository.load(lat, lon)
                getWeatherDatabase()
            } catch (ex: IOException) {
                _networkError.value = true
                getWeatherDatabase()
                Log.d(TAG, "loadAPI: network err - $ex")
            }
        }
    }

    // Read data from database
    private suspend fun getWeatherDatabase() {
        val weatherData = weatherRepository.getWeatherApi()
        populateDailyHourlyData(weatherData)
    }

    private fun populateDailyHourlyData(weatherData: WeatherGetApi) {
        // display data detail HomeFragment
        addDataDetail(weatherData)

        // display data DailyFragment
        val listDaily = weatherData.daily
        _listDailyNav.postValue(listDaily)

        // get first element of daily List
        _listCurrent.postValue(listDaily.first())

        // display data HourlyFragment
        val listHourly = weatherData.hourly
        _listHourlyNav.postValue(listHourly)
    }

    private fun addDataDetail(weatherData: WeatherGetApi) {
        val listDetail = mutableListOf<DetailHomeModel>()
        val listCurrent = weatherData.current

        val index1 = DetailHomeModel(
            1,
            context.getString(R.string.feels_like_string),
            context.getString(R.string.fm_temp_celsius, listCurrent.temp)
        )
        listDetail.add(index1)

        val index2 = DetailHomeModel(
            2,
            context.getString(R.string.humidity_string),
            context.getString(R.string.humidity, listCurrent.humidity)
        )
        listDetail.add(index2)

        val index3 = DetailHomeModel(
            3,
            context.getString(R.string.uvi_string),
            context.getString(R.string.uvi, listCurrent.uvi)
        )
        listDetail.add(index3)

        val index4 = DetailHomeModel(
            4,
            context.getString(R.string.visibility_string),
            context.getString(R.string.visibility, (listCurrent.visibility.div(1000)))
        )
        listDetail.add(index4)

        val index5 = DetailHomeModel(
            5,
            context.getString(R.string.dew_point_string),
            context.getString(R.string.dew_point, listCurrent.dew_point)
        )
        listDetail.add(index5)

        val index6 = DetailHomeModel(
            6,
            context.getString(R.string.pressure_string),
            context.getString(R.string.pressure, listCurrent.pressure)
        )
        listDetail.add(index6)

        listDataDetail.postValue(listDetail)
    }

    fun getLastLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude
                    getLocation(lat, lon, context)
                } else {
                    startLocationUpdates()
                }
            }
            return
        } catch (ex: SecurityException) {
            Log.d(TAG, "getLastLocation: $ex")
            Toast.makeText(context, "Permissions denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getLocation(lat: Double, lon: Double, context: Context) {
        try {
            val geocoder = Geocoder(context)
            val position = geocoder.getFromLocation(lat, lon, 1)

            loadApi(lat, lon)
            getPosition = position[0].getAddressLine(0)
            showLocation(Utils.formatLocation(context, getPosition))
        } catch (e: Exception) {
            Log.d(TAG, "getLastLocation: failed - $e")
        }
    }

    fun showLocation(location: String) {
        _showLocation.value = location
    }

    fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = Constants.INTERVAL
            fastestInterval = Constants.FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    // Update UI with location data
                    // Get lat-lon
                    val latUpdate = location.latitude
                    val lonUpdate = location.longitude
                    getLocation(latUpdate, lonUpdate, context)
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

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun pushNotifications(context: Context, intent: Intent, alarmManager: AlarmManager) {
        val pendingIntentRequestCode = 0
        val pendingIntent = PendingIntent.getBroadcast(
            context,
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

    fun locationChange() {
        createLocationRequest()
        createLocationCallback()
        startLocationUpdates()
    }

    suspend fun getCurrentTime() {
        withContext(Dispatchers.Main) {
            while (true) {
                val currentTime = Calendar.getInstance().time
                _mCurrentTime.postValue(Utils.formatCurrentTime(context, currentTime))
                Log.d(TAG, "getCurrentTime: ${_mCurrentTime.value}")
                delay(1000)
            }
        }
    }

    companion object {
        private const val TAG = "WeatherViewModel"
    }
}