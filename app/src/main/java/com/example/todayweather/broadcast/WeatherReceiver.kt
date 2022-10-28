package com.example.todayweather.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.todayweather.R

@RequiresApi(Build.VERSION_CODES.M)
class WeatherReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        isOnline(context)
    }
    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Toast.makeText(
                    context,
                    context.getString(R.string.string_online_internet),
                    Toast.LENGTH_SHORT
                ).show()
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Toast.makeText(
                    context,
                    context.getString(R.string.string_online_wifi),
                    Toast.LENGTH_SHORT
                ).show()
                return true
            }
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.string_offline),
                Toast.LENGTH_SHORT
            ).show()
        }
        return false
    }
}