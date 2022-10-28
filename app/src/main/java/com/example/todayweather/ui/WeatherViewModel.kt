package com.example.todayweather.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.todayweather.R
import com.example.todayweather.data.WeatherRepository
import com.example.todayweather.ui.home.model.Daily
import com.example.todayweather.ui.home.model.Hourly
import com.example.todayweather.ui.home.model.WeatherGetApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException

class WeatherViewModel(
//    private val database: WeatherDao,
//    private val weatherApiService: WeatherApiService,
    application: Application
) : ViewModel() {

    private val weatherRepository: WeatherRepository = WeatherRepository(application)

    // The external LiveData interface to the property is immutable, so only this class can modify
    // list current
    private val _listCurrent = MutableLiveData<Daily>()
    val listCurrent: LiveData<Daily>
        get() = _listCurrent

    // list data detail
    var listDataDetail = MutableLiveData<MutableList<HomeModel>>()

    // daily nav
    private val _listDailyNav = MutableLiveData<MutableList<Daily>>()
    val listDataDaily: LiveData<MutableList<Daily>>
        get() = _listDailyNav

    // hourly nav
    private val _listHourlyNav = MutableLiveData<MutableList<Hourly>>()
    val listDataHourly: LiveData<MutableList<Hourly>>
        get() = _listHourlyNav

    // var using for set data to list data detail
    private val res = application

    // init var get location to show in MainActivity
    val showLocation = MutableLiveData<String>()

    private fun loadWeather(): List<WeatherGetApi> = weatherRepository.getWeatherApi()

    fun refreshData(lat: Double, lon: Double) {
        viewModelScope.launch {
            weatherRepository.load(lat, lon)
            getDB()
        }
    }

    private suspend fun getDB() {
        withContext(Dispatchers.IO) {
            val data = loadWeather()
            populateData(data)
        }
    }

    private fun populateData(weathers: List<WeatherGetApi>) {
        _listCurrent.postValue(weathers[0].daily.lastOrNull())
        _listDailyNav.postValue(weathers[0].daily)
        _listHourlyNav.postValue(weathers[0].hourly)
//        addDataDetail(weathers[0])
    }

//    fun getWeatherProperties(lat: Double, lon: Double) {
//        viewModelScope.launch {
//            try {
//                // get data from API
//                val weatherData = weatherApiService.getProperties(lat, lon)
//                // create and save db after get from API
//                database.insert(weatherData)
//                getDataFromDatabase()
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//        }
//    }
//
//    private fun getDataFromDatabase() {
//        val weatherData = loadDataFromDB().lastOrNull() ?: return
//        populateDailyHourlyData(weatherData)
//    }

    private fun populateDailyHourlyData(weatherData: WeatherGetApi) {
        // display detail home fragment
//        addDataDetail(weatherData)

        // get data Daily & set to ListDaily
        val listDaily = weatherData.daily
        _listDailyNav.value = listDaily

        // get first element of daily List
        _listCurrent.value = listDaily.first()

        // get data Hourly & set to ListHourly
        val listHourly = weatherData.hourly
        _listHourlyNav.value = listHourly
    }

    // set data to list data detail
//    private fun addDataDetail(weatherData: WeatherGetApi) {
//        val listDetail = mutableListOf<HomeModel>()
//
//        val listCurrent = weatherData.current
//
//        val index1 = HomeModel(
//            1,
//            res.getString(R.string.feels_like_string),
//            res.getString(R.string.fm_temp_celsius, listCurrent.temp)
//        )
//        listDetail.add(index1)
//        val index2 = HomeModel(
//            2,
//            res.getString(R.string.humidity_string),
//            res.getString(R.string.humidity, listCurrent.humidity)
//        )
//        listDetail.add(index2)
//        val index3 = HomeModel(
//            3,
//            res.getString(R.string.uvi_string),
//            res.getString(R.string.uvi, listCurrent.uvi)
//        )
//        listDetail.add(index3)
//        val index4 = HomeModel(
//            4,
//            res.getString(R.string.visibility_string),
//            res.getString(R.string.visibility, (listCurrent.visibility.div(1000)))
//        )
//        listDetail.add(index4)
//        val index5 = HomeModel(
//            5,
//            res.getString(R.string.dew_point_string),
//            res.getString(R.string.dew_point, listCurrent.dew_point)
//        )
//        listDetail.add(index5)
//        val index6 = HomeModel(
//            6,
//            res.getString(R.string.pressure_string),
//            res.getString(R.string.pressure, listCurrent.pressure)
//        )
//        listDetail.add(index6)
//
//        // set data to observe
//        listDataDetail.value = listDetail
//    }

    fun showLocation(location: String) {
        showLocation.value = location
    }

//    private fun loadDataFromDB() = database.loadAPI()

    class WeatherViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return WeatherViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}