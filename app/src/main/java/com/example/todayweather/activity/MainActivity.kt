package com.example.todayweather.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.todayweather.R
import com.example.todayweather.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.actNavHost)
        return navController.navigateUp()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}