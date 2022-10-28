package com.example.todayweather.database

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.example.todayweather.ui.home.model.WeatherGetApi

@Dao
interface WeatherDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(weatherGetApi: WeatherGetApi)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(weathers: List<WeatherGetApi>)

    @Query("SELECT * FROM get_api_table")
    fun loadAPI(): List<WeatherGetApi>

    @Delete
    suspend fun clear(weatherGetApi: WeatherGetApi)
}