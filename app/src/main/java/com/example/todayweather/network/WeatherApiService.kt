package com.example.todayweather.network

import com.example.todayweather.ui.home.model.WeatherGetApi
import com.example.todayweather.util.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET(Constants.URL_PATH)
    suspend fun getProperties(
        @Query(Constants.URL_LAT) lat: Double = Constants.URL_LAT_VALUE,
        @Query(Constants.URL_LON) lon: Double = Constants.URL_LON_VALUE,
        @Query(Constants.URL_EXCLUDE) exclude: String = Constants.URL_EXCLUDE_VALUE,
        @Query(Constants.URL_APPID) appid: String = Constants.URL_APPID_VALUE,
        @Query(Constants.URL_LANG) lang: String = Constants.URL_LANG_VALUE,
        @Query(Constants.URL_UNITS) units: String = Constants.URL_UNITS_VALUE
    ): WeatherGetApi
}