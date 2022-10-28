package com.example.todayweather.util

import androidx.room.TypeConverter
import com.example.todayweather.data.model.Current
import com.example.todayweather.data.model.Daily
import com.example.todayweather.data.model.Hourly
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    // Convert data from json object to display
    @TypeConverter
    fun fromJsonToCurrent(input: String): Current {
        return Gson().fromJson(input, Current::class.java)
    }

    @TypeConverter
    fun fromJsonToDaily(input: String): List<Daily> {
        val type = object : TypeToken<List<Daily>>() {}.type
        return Gson().fromJson(input, type)
    }

    @TypeConverter
    fun fromJsonToHourly(input: String): List<Hourly> {
        val type = object : TypeToken<List<Hourly>>() {}.type
        return Gson().fromJson(input, type)
    }

    // Convert data from API(json string) to json object
    @TypeConverter
    fun fromCurrentToJson(input: Current): String {
        return Gson().toJson(input)
    }

    @TypeConverter
    fun fromListDailyToJson(input: List<Daily>): String {
        return Gson().toJson(input)
    }

    @TypeConverter
    fun fromListHourlyToJson(input: List<Hourly>): String {
        return Gson().toJson(input)
    }
}