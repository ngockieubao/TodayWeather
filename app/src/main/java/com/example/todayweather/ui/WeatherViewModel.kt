package com.example.todayweather.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todayweather.R
import com.example.todayweather.database.WeatherDao
import com.example.todayweather.ui.home.model.Daily
import com.example.todayweather.ui.home.model.Hourly
import com.example.todayweather.ui.home.model.WeatherGetApi
import com.example.todayweather.network.WeatherApiService
import com.example.todayweather.util.WeatherApplication
import kotlinx.coroutines.launch

class WeatherViewModel(
    // init var database
    private val database: WeatherDao,
    private val weatherApiService: WeatherApiService
) : ViewModel() {
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
    private val res = WeatherApplication.instant.resources

    // init var get location to show in MainActivity
    val showLocation = MutableLiveData<String>()

    fun getWeatherProperties(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                // get data from API
                val weatherData = weatherApiService.getProperties(lat, lon)
                // create and save db after get from API
                database.insert(weatherData)
                getDataFromDatabase()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private suspend fun getDataFromDatabase() {
        val weatherData = loadDataFromDB().lastOrNull() ?: return
        populateDailyHourlyData(weatherData)
    }

    private fun populateDailyHourlyData(weatherData: WeatherGetApi) {
        // display detail home fragment
        addDataDetail(weatherData)

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
    private fun addDataDetail(weatherData: WeatherGetApi) {
        val listDetail = mutableListOf<HomeModel>()

        val listCurrent = weatherData.current

        val index1 = HomeModel(1, res.getString(R.string.feels_like_string), res.getString(R.string.fm_temp_celsius, listCurrent.temp))
        listDetail.add(index1)
        val index2 = HomeModel(2, res.getString(R.string.humidity_string), res.getString(R.string.humidity, listCurrent.humidity))
        listDetail.add(index2)
        val index3 = HomeModel(3, res.getString(R.string.uvi_string), res.getString(R.string.uvi, listCurrent.uvi))
        listDetail.add(index3)
        val index4 = HomeModel(
            4, res.getString(R.string.visibility_string), res.getString(R.string.visibility, (listCurrent.visibility.div(1000)))
        )
        listDetail.add(index4)
        val index5 = HomeModel(5, res.getString(R.string.dew_point_string), res.getString(R.string.dew_point, listCurrent.dew_point))
        listDetail.add(index5)
        val index6 = HomeModel(6, res.getString(R.string.pressure_string), res.getString(R.string.pressure, listCurrent.pressure))
        listDetail.add(index6)

        // set data to observe
        listDataDetail.value = listDetail
    }

    fun showLocation(location: String) {
        showLocation.value = location
    }

    private suspend fun loadDataFromDB() = database.loadAPI()
}