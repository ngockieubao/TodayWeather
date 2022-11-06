package com.example.todayweather.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.provider.Settings

@Suppress("DEPRECATION")
class LocationReceiver(
    private val locationImpl: LocationImpl
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        isLocationEnabled(context)
    }

    private fun isLocationEnabled(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This is a new method provided in API 28
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (lm.isLocationEnabled) {
                locationImpl.onLocationChange("on")
            } else {
                // This was deprecated in API 28
                locationImpl.onLocationChange("off")
                val mode: Int = Settings.Secure.getInt(
                    context.contentResolver, Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF
                )
                mode != Settings.Secure.LOCATION_MODE_OFF
            }
        }
        return false
    }
}