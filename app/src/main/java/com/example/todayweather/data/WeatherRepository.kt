package com.example.todayweather.data

import android.app.Application
import com.example.todayweather.data.model.WeatherGetApi
import com.example.todayweather.database.WeatherDao
import com.example.todayweather.database.WeatherDatabase

class WeatherRepository(application: Application) {

    private val weatherDao: WeatherDao

    init {
        val weatherDatabase: WeatherDatabase = WeatherDatabase.getDatabase(application)
        weatherDao = weatherDatabase.weatherDao
    }

    suspend fun insertWeather(weatherGetApi: WeatherGetApi) {
        weatherDao.insert(weatherGetApi)
    }

    suspend fun getWeatherApi(): WeatherGetApi =
        weatherDao.getWeatherData()


    companion object {
        private const val TAG = "WeatherRepository"
    }
}

