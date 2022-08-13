package com.example.todayweather.util

import android.util.Log
import com.example.todayweather.BuildConfig

/* Use for debugging */
object LogUtils {
    fun logDebug(scrName:String, msg:String){
        if(BuildConfig.DEBUG)
            Log.d(scrName, msg)
    }

    fun logInfo(scrName:String, msg:String){
        if(BuildConfig.DEBUG)
            Log.i(scrName, msg)
    }
}