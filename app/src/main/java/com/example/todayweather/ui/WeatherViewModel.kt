package com.example.todayweather.ui

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.todayweather.R
import com.example.todayweather.data.WeatherRepository
import com.example.todayweather.data.model.Daily
import com.example.todayweather.data.model.DetailHomeModel
import com.example.todayweather.data.model.Hourly
import com.example.todayweather.data.model.WeatherGetApi
import kotlinx.coroutines.launch
import java.io.IOException

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

    private val _listDailyNav = MutableLiveData<MutableList<Daily>>()
    val listDataDaily: LiveData<MutableList<Daily>>
        get() = _listDailyNav

    private val _listHourlyNav = MutableLiveData<MutableList<Hourly>>()
    val listDataHourly: LiveData<MutableList<Hourly>>
        get() = _listHourlyNav

    private val _showLocation = MutableLiveData<String>()
    val showLocation = _showLocation

    fun loadAPI(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                weatherRepository.load(lat, lon)
                getWeatherDatabase()
            } catch (ex: IOException) {
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
        _listDailyNav.value = listDaily

        // get first element of daily List
        _listCurrent.value = listDaily.first()

        // display data HourlyFragment
        val listHourly = weatherData.hourly
        _listHourlyNav.value = listHourly
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

        listDataDetail.value = listDetail
    }

    fun showLocation(location: String) {
        _showLocation.value = location
    }

    class WeatherViewModelFactory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return WeatherViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    companion object {
        private const val TAG = "WeatherViewModel"
    }
}