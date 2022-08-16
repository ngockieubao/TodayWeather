package com.example.todayweather.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.todayweather.ui.home.model.WeatherGetApi
import com.example.todayweather.util.Converters

@Database(entities = [WeatherGetApi::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract val weatherDAO: WeatherDAO
}