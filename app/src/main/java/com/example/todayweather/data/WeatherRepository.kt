package com.example.todayweather.data

import android.app.Application
import android.widget.Toast
import com.example.todayweather.data.model.WeatherGetApi
import com.example.todayweather.database.WeatherDao
import com.example.todayweather.database.WeatherDatabase
import com.example.todayweather.network.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherRepository(private val application: Application) {

    private val weatherDao: WeatherDao

    init {
        val weatherDatabase: WeatherDatabase = WeatherDatabase.getDatabase(application)
        weatherDao = weatherDatabase.weatherDao
    }

    suspend fun insertWeather(weatherGetApi: WeatherGetApi) {
        weatherDao.insert(weatherGetApi)
    }

    suspend fun getWeatherApi(): WeatherGetApi = weatherDao.getWeatherData()

    companion object {
        private const val TAG = "WeatherRepository"
    }
}
