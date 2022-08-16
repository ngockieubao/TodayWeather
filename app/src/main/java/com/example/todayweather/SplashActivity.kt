package com.example.todayweather

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Start MainActivity
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        // Close splash activity
        finish()
    }
}