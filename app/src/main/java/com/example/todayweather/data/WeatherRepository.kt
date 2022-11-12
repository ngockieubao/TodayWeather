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
    private var data: WeatherGetApi? = null

    init {
        val weatherDatabase: WeatherDatabase = WeatherDatabase.getDatabase(application)
        weatherDao = weatherDatabase.weatherDao
    }

    private suspend fun insertWeather(weatherGetApi: WeatherGetApi) {
        weatherDao.insert(weatherGetApi)
    }

    suspend fun load(lat: Double, lon: Double) {
        withContext(Dispatchers.IO) {
            WeatherApi.retrofitService.getProperties(lat, lon).enqueue(object : Callback<WeatherGetApi> {
                override fun onResponse(call: Call<WeatherGetApi>, response: Response<WeatherGetApi>) {
                    // HTTP code success
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body == null) return
                        else data = body
                    }
                }

                override fun onFailure(call: Call<WeatherGetApi>, t: Throwable) {
                    Toast.makeText(application.applicationContext, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
            data?.let { insertWeather(it) }
        }
    }

    suspend fun getWeatherApi(): WeatherGetApi = weatherDao.getWeatherData()

    companion object {
        private const val TAG = "WeatherRepository"
    }
}
