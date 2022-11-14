package com.example.todayweather.ui.home

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.todayweather.R
import com.example.todayweather.receiver.LocationImpl

class LocationDialog(
    private val locationImpl: LocationImpl
    ) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder
                .setMessage(R.string.gps_network_not_enabled)
                .setPositiveButton(R.string.open_location_settings) { _, _ ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton(R.string.Cancel) { _, _ ->
                    locationImpl.onLocationChange("off")
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}