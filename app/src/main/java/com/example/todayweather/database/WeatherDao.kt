package com.example.todayweather.database

import androidx.room.*
import com.example.todayweather.data.model.WeatherGetApi

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherGetApi: WeatherGetApi)

    @Query("SELECT * FROM weather_get_api_table")
    suspend fun getWeatherData(): WeatherGetApi

    @Delete
    suspend fun clear(weatherGetApi: WeatherGetApi)

    @Update
    suspend fun update(weatherGetApi: WeatherGetApi)
}