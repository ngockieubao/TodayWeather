package com.example.todayweather.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.todayweather.data.model.WeatherGetApi

@Dao
interface WeatherDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(weatherGetApi: WeatherGetApi)

    @Query("SELECT * FROM weather_get_api_table")
    suspend fun getWeatherData(): WeatherGetApi

    @Query("DELETE FROM weather_get_api_table")
    suspend fun clear()
}