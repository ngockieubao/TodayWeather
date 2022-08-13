package com.example.todayweather.di

import android.app.Application
import androidx.room.Room
import com.example.todayweather.database.WeatherDAO
import com.example.todayweather.database.WeatherDatabase
import com.example.todayweather.network.WeatherApiService
import com.example.todayweather.util.Constants
import com.example.todayweather.util.WeatherApplication
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single { provideRetrofit(get()) }
    single { provideApiService(get()) }
    single { logRetrofit() }
    single { provideDatabase(WeatherApplication.instant) }
    single { provideWeatherDao(get()) }
}

private fun logRetrofit(): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }
    return OkHttpClient.Builder().addInterceptor(logging).build()
}

private fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(Constants.BASE_URL)
    .client(okHttpClient)
    .build()

private fun provideApiService(retrofit: Retrofit): WeatherApiService = retrofit.create(WeatherApiService::class.java)

private fun provideDatabase(application: Application): WeatherDatabase {
    return Room.databaseBuilder(application, WeatherDatabase::class.java, Constants.DATABASE).fallbackToDestructiveMigration().allowMainThreadQueries()
        .build()
}

private fun provideWeatherDao(database: WeatherDatabase): WeatherDAO {
    return database.weatherDAO
}