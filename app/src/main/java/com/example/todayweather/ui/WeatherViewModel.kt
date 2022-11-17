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
import com.example.todayweather.data.model.*
import com.example.todayweather.network.WeatherApi
import com.example.todayweather.util.Constants
import com.example.todayweather.util.SharedPrefs
import com.example.todayweather.util.Utils
import com.google.android.gms.location.*
import kotlinx.coroutines.*
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

    private val _listCurrent = MutableLiveData<Current?>()
    val listCurrent: LiveData<Current?> get() = _listCurrent

    val listDataDetail = MutableLiveData<MutableList<DetailHomeModel>?>()

    private val _listDaily = MutableLiveData<Daily?>()
    val listDaily: LiveData<Daily?> get() = _listDaily

    private val _listDailyNav = MutableLiveData<MutableList<Daily>?>()
    val listDataDaily: LiveData<MutableList<Daily>?> get() = _listDailyNav

    private val _listHourlyNav = MutableLiveData<MutableList<Hourly>?>()
    val listDataHourly: LiveData<MutableList<Hourly>?> get() = _listHourlyNav

    private val _showLocation = MutableLiveData<String?>()
    val showLocation = _showLocation

    private val _networkError = MutableLiveData<Boolean?>()
    val networkError: LiveData<Boolean?> = _networkError

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var getPosition: String = ""
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private val _mCurrentTime = MutableLiveData<String?>()
    val mCurrentTime: LiveData<String?> get() = _mCurrentTime

    private lateinit var currentTime: Date
    private lateinit var data: WeatherGetApi

    var mStatus = MutableLiveData<String?>(Utils.status)
    var mFirstInstall = true

    init {
        createLocationRequest()
        createLocationCallback()
        SharedPrefs
    }

    fun loadApiFirst(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                _networkError.value = false
                data = callApi(lat = lat, lon = lon)
                weatherRepository.insertWeather(data)
                getWeatherDatabase()
            } catch (ex: IOException) {
                _networkError.value = true
                Toast.makeText(context, "$ex", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun loadApi(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                _networkError.value = false
                mStatus.value = SharedPrefs.instance.getString(Constants.SHARED_PREFS)

                if (mStatus.value == Constants.CELCIUS)
                    data = callApi(lat = lat, lon = lon)
                if (mStatus.value == Constants.FAHRENHEIT)
                    data = callApiImperial(lat = lat, lon = lon, units = Constants.URL_UNITS_VALUE_IMPERIAL)

                weatherRepository.insertWeather(data)
                getWeatherDatabase()
            } catch (ex: IOException) {
                _networkError.value = true
                getWeatherDatabase()
                Log.d(TAG, "loadAPI: network err - $ex")
                Toast.makeText(context, "$ex", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun callApi(lat: Double, lon: Double): WeatherGetApi {
        return WeatherApi.retrofitService.getProperties(lat = lat, lon = lon)
    }

    private suspend fun callApiImperial(lat: Double, lon: Double, units: String): WeatherGetApi {
        return WeatherApi.retrofitService.getProperties(lat = lat, lon = lon, units = units)
    }

    // Read data from database
    private suspend fun getWeatherDatabase() {
        val weatherData = weatherRepository.getWeatherApi()
        populateWeatherData(weatherData)
    }

    private fun populateWeatherData(weatherData: WeatherGetApi) {
        try {
            // display data detail HomeFragment
            populateDataDetail(weatherData)
            populateDataDaily(weatherData)
            populateDataHourly(weatherData)
        } catch (ex: Exception) {
            Log.d(TAG, "populateDailyHourlyData: failed - $ex")
        }
    }

    private fun populateDataHourly(weatherData: WeatherGetApi) {
        val listHourly = weatherData.hourly
        _listHourlyNav.postValue(listHourly)
    }

    private fun populateDataDaily(weatherData: WeatherGetApi) {
        val listDaily = weatherData.daily

        _listDailyNav.postValue(listDaily)
        // get first element of daily List
        _listDaily.postValue(listDaily.first())
    }

    private fun populateDataDetail(weatherData: WeatherGetApi) {
        val listDetail = mutableListOf<DetailHomeModel>()
        _listCurrent.value = weatherData.current

        if (mStatus.value == Constants.CELCIUS)
            addDataDetail(listDetail)
        if (mStatus.value == Constants.FAHRENHEIT)
            addDataDetailConvert(listDetail)
        listDataDetail.postValue(listDetail)
    }

    @SuppressLint("StringFormatMatches")
    private fun addDataDetail(listDetail: MutableList<DetailHomeModel>) {
        val index1 = DetailHomeModel(
            1,
            context.getString(R.string.feels_like_string),
            context.getString(R.string.fm_temp_celsius, _listCurrent.value?.feels_like)
        )
        listDetail.add(index1)

        val index2 = DetailHomeModel(
            2,
            context.getString(R.string.humidity_string),
            context.getString(R.string.humidity, _listCurrent.value?.humidity)
        )
        listDetail.add(index2)

        val index3 = DetailHomeModel(
            3,
            context.getString(R.string.uvi_string),
            context.getString(R.string.uvi, _listCurrent.value?.uvi)
        )
        listDetail.add(index3)

        val index4 = DetailHomeModel(
            4,
            context.getString(R.string.visibility_string),
            context.getString(R.string.visibility, _listCurrent.value?.visibility?.let { Utils.divThousand(it) })
        )
        listDetail.add(index4)

        val index5 = DetailHomeModel(
            5,
            context.getString(R.string.dew_point_string),
            context.getString(R.string.dew_point, _listCurrent.value?.dew_point)
        )
        listDetail.add(index5)

        val index6 = DetailHomeModel(
            6,
            context.getString(R.string.pressure_string),
            context.getString(R.string.pressure, _listCurrent.value?.pressure)
        )
        listDetail.add(index6)
    }

    private fun addDataDetailConvert(listDetail: MutableList<DetailHomeModel>) {
        val index1 = DetailHomeModel(
            1,
            context.getString(R.string.feels_like_string),
            context.getString(R.string.fm_temp_fah, _listCurrent.value?.feels_like)
        )
        listDetail.add(index1)

        val index2 = DetailHomeModel(
            2,
            context.getString(R.string.humidity_string),
            context.getString(R.string.humidity, _listCurrent.value?.humidity)
        )
        listDetail.add(index2)

        val index3 = DetailHomeModel(
            3,
            context.getString(R.string.uvi_string),
            context.getString(R.string.uvi, _listCurrent.value?.uvi?.let { Utils.formatUltraviolet(it) })
        )
        listDetail.add(index3)

        val index4 = DetailHomeModel(
            4,
            context.getString(R.string.visibility_string),
            context.getString(R.string.visibility_mile, _listCurrent.value?.visibility?.let { Utils.convertMeterToMile(it) })
        )
        listDetail.add(index4)

        val index5 = DetailHomeModel(
            5,
            context.getString(R.string.dew_point_string),
            context.getString(R.string.dew_point_fah, _listCurrent.value?.dew_point)
        )
        listDetail.add(index5)

        val index6 = DetailHomeModel(
            6,
            context.getString(R.string.pressure_string),
            context.getString(R.string.pressure, _listCurrent.value?.pressure)
        )
        listDetail.add(index6)
    }

    fun getLastLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val lat = location.latitude
                        val lon = location.longitude
                        getLocation(lat, lon, context)
                    } else {
                        Toast.makeText(context, "getLastLocation: Location is null", Toast.LENGTH_SHORT).show()
                        locationChange()
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "getLastLocation: $it")
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

            if (mFirstInstall) {
                loadApiFirst(lat, lon)
                mFirstInstall = false
                mStatus.value = Constants.CELCIUS
            } else {
                loadApi(lat, lon)
            }
            getPosition = position[0].getAddressLine(0)
            showLocation(Utils.formatLocation(context, getPosition))
        } catch (e: Exception) {
            Log.d(TAG, "getLastLocation: failed - $e")
        }
    }

    fun showLocation(location: String) {
        _showLocation.value = location
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
        viewModelScope.launch(Dispatchers.Main) {
            while (true) {
                currentTime = Calendar.getInstance().time
                _mCurrentTime.postValue(Utils.formatCurrentTime(context, currentTime))
                delay(1000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        _listCurrent.value = null
        _listDaily.value = null
        listDataDetail.value = null
        _listDailyNav.value = null
        _listHourlyNav.value = null
        _showLocation.value = null
        _networkError.value = null
        _mCurrentTime.value = null
    }

    companion object {
        private const val TAG = "WeatherViewModel"
    }
}