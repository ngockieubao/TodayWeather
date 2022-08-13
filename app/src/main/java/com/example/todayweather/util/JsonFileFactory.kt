package com.example.todayweather.util

import com.example.todayweather.ui.home.model.City
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.FileReader

class JsonFileFactory {
    fun readFileJson(path: String): MutableList<City> {
        var data: MutableList<City> = mutableListOf()
        try {
            val gson = Gson()
            val file = FileReader(path)
            data = gson.fromJson(file, object : TypeToken<MutableList<City>>() {}.type)
            file.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return data
    }
}