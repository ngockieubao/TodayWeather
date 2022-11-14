package com.example.todayweather.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.widget.Toast

class LocationReceiver(
    private val locationImpl: LocationImpl
) : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
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
                locationImpl.onLocationChange("off")
            }
        } else {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (isGpsEnabled) {
                Toast.makeText(context, "Location is on", Toast.LENGTH_SHORT).show()
                locationImpl.onLocationChange("on")
            } else {
                Toast.makeText(context, "Location is off", Toast.LENGTH_SHORT).show()
                locationImpl.onLocationChange("off")
            }
        }
        return false
    }
}