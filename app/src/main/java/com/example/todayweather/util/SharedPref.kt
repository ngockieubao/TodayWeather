package com.example.todayweather.util

import android.content.Context

class SharedPref(val context: Context) {
    val sharedPref = context.getSharedPreferences(Constants.CELSIUS, Context.MODE_PRIVATE)
}