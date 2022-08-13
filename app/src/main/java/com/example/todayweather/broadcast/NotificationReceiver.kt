package com.example.todayweather.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.todayweather.service.PushNotificationService

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Start service
        startTestService(context)
    }

    private fun startTestService(context: Context) {
        val intent = Intent(context, PushNotificationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, intent)
        } else {
            context.startService(intent)
        }
    }
}