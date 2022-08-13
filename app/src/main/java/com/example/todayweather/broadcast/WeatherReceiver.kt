package com.example.todayweather.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.todayweather.R
import com.example.todayweather.util.Constants

class WeatherReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Constants.BROADCAST_RECEIVER_NETWORK_STATUS) {
            isOnline(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Toast.makeText(context, context.getString(R.string.online), Toast.LENGTH_SHORT).show()
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Toast.makeText(context, context.getString(R.string.online), Toast.LENGTH_SHORT).show()
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Toast.makeText(context, context.getString(R.string.online), Toast.LENGTH_SHORT).show()
                    return true
                }
            } else {
                Toast.makeText(context, context.getString(R.string.offline), Toast.LENGTH_SHORT).show()
            }
        }
        return false
    }
}
