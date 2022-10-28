package com.example.todayweather.ui.home

import com.example.todayweather.data.model.City

interface SelectCity {
    fun selectItem(city: City?)
}