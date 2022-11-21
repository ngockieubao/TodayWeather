package com.example.todayweather.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.example.todayweather.WeatherApplication

class SharedPrefs {

    private val mSharedPreferences: SharedPreferences = WeatherApplication.self()!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    @SuppressLint("CommitPrefEdits")
    fun putStringValue(key: String, data: String) {
        val editor = mSharedPreferences.edit()
        editor.putString(key, data)
        editor.apply()
    }

    fun getStringValue(key: String): String? {
        return mSharedPreferences.getString(key, "")
    }

    fun putBooleanValue(key: String, data: Boolean) {
        val editor = mSharedPreferences.edit()
        editor.putBoolean(key, data)
        editor.apply()
    }

    fun getBooleanValue(key: String, data: Boolean): Boolean {
        return mSharedPreferences.getBoolean(key, data)
    }

    companion object {
        private const val PREF_NAME = "shared_prefs"
        private var mInstance: SharedPrefs? = null
        val instance: SharedPrefs
            get() {
                if (mInstance == null) {
                    mInstance = SharedPrefs()
                }
                return mInstance as SharedPrefs
            }
    }
}