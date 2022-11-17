package com.example.todayweather.network

import com.example.todayweather.data.model.WeatherGetApi
import com.example.todayweather.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val retrofit =
    Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

interface WeatherApiService {
    @GET(Constants.URL_PATH)
    suspend fun getProperties(
        @Query(Constants.URL_LAT) lat: Double,
        @Query(Constants.URL_LON) lon: Double,
        @Query(Constants.URL_EXCLUDE) exclude: String = Constants.URL_EXCLUDE_VALUE,
        @Query(Constants.URL_APPID) appid: String = Constants.URL_APPID_VALUE,
        @Query(Constants.URL_LANG) lang: String = Constants.URL_LANG_VALUE,
        @Query(Constants.URL_UNITS) units: String = Constants.URL_UNITS_VALUE_DEFAULT
    ): WeatherGetApi
}

object WeatherApi {
    val retrofitService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}