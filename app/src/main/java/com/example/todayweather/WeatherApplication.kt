package com.example.todayweather

import android.app.Application

class WeatherApplication : Application() {

//    var gSon: Gson? = null

    override fun onCreate() {
        super.onCreate()
        mSelf = this
//        gSon = Gson()
    }

    companion object {
        private var mSelf: WeatherApplication? = null
        fun self(): WeatherApplication? {
            return mSelf
        }
    }
}