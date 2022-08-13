package com.example.todayweather.util

import android.app.Application
import com.example.todayweather.di.appModule
import com.example.todayweather.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WeatherApplication : Application(){
    companion object {
        private var mInstant: WeatherApplication? = null
        val instant
            get() = mInstant!!
    }

    override fun onCreate() {
        super.onCreate()
        mInstant = this
        startKoin {
            androidContext(this@WeatherApplication)
            modules(appModule, viewModelModule)
        }
    }
}