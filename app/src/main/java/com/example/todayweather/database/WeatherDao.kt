package com.example.todayweather.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.todayweather.ui.home.model.WeatherGetApi

@Dao
interface WeatherDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(weatherGetApi: WeatherGetApi)

    @Query("SELECT * FROM get_api_table")
    suspend fun loadAPI(): List<WeatherGetApi>

    @Query("DELETE FROM get_api_table")
    suspend fun clear()
}