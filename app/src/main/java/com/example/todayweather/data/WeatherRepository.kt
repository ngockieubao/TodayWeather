package com.example.todayweather.data

import android.app.Application
import com.example.todayweather.data.model.WeatherGetApi
import com.example.todayweather.database.WeatherDao
import com.example.todayweather.database.WeatherDatabase
import com.example.todayweather.network.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository(application: Application) {
    private val weatherDao: WeatherDao

    init {
        val weatherDatabase: WeatherDatabase = WeatherDatabase.getDatabase(application)
        weatherDao = weatherDatabase.weatherDao
    }

    private suspend fun insertWeather(weatherGetApi: WeatherGetApi) {
        weatherDao.insert(weatherGetApi)
    }

    suspend fun load(lat: Double, lon: Double) {
        withContext(Dispatchers.IO) {
            val data = WeatherApi.retrofitService.getProperties(lat, lon)
            insertWeather(data)
        }
    }

    suspend fun getWeatherApi(): WeatherGetApi = weatherDao.getWeatherData()
}
