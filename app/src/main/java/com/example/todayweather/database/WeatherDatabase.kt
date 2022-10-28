package com.example.todayweather.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.todayweather.data.model.WeatherGetApi
import com.example.todayweather.util.Constants
import com.example.todayweather.util.Converters

@Database(entities = [WeatherGetApi::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract val weatherDao: WeatherDao

    companion object {
        @Volatile
        private var instance: WeatherDatabase? = null

        fun getDatabase(context: Context): WeatherDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context,
                    WeatherDatabase::class.java,
                    Constants.DATABASE
                ).build()
            }
            return instance!!
        }
    }
}