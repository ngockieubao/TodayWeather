package com.example.todayweather.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.todayweather.R
import com.example.todayweather.data.model.City
import com.example.todayweather.data.model.Daily
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

object Utils {
    fun formatCurrentTime(context: Context, time: Date): String {
        val timeFormat = SimpleDateFormat(context.getString(R.string.fm_time), Locale.getDefault())
        return timeFormat.format(time)
    }

    fun formatTime(context: Context, time: Long): String {
        val getTime = Date(time.times(1000))
        val timeFormat = SimpleDateFormat(context.getString(R.string.fm_time), Locale.getDefault())
        return timeFormat.format(getTime)
    }

    fun formatDate(context: Context, date: Long): String {
        val dateFormat = Date(date.times(1000))
        val dayOfWeek = SimpleDateFormat(context.getString(R.string.fm_day_of_week), Locale(Constants.LOCALE_LANG))
        val dayOfWeekFormat = dayOfWeek.format(dateFormat)
        val dayMonth = SimpleDateFormat(context.getString(R.string.fm_date), Locale(Constants.LOCALE_LANG))
        val dayMonthFormat = dayMonth.format(dateFormat)
        return String.format(context.getString(R.string.fm_day_date), dayOfWeekFormat, dayMonthFormat)
    }

    fun formatPop(context: Context, pop: Double): String {
        val popFormat = String.format(context.getString(R.string.fm_pop), pop.times(100))
        return if (pop != 0.0)
            popFormat
        else
            String.format(context.getString(R.string.string_empty))
    }

    fun formatTempFeelsLike(context: Context, temp: Double, tempFeelsLike: Double): String {
        val tempFormat = String.format(context.getString(R.string.fm_temp), temp)
        val tempFeelsLikeFormat = String.format(context.getString(R.string.fm_temp), tempFeelsLike)
        return String.format(context.getString(R.string.fm_temp_feels_like), tempFormat, tempFeelsLikeFormat)
    }

    fun formatTempFeelsLikeFah(context: Context, temp: Double, tempFeelsLike: Double): String {
        val tempFormat = String.format(context.getString(R.string.fm_temp), convertCelsiusToFahrenheit(temp))
        val tempFeelsLikeFormat = String.format(context.getString(R.string.fm_temp), convertCelsiusToFahrenheit(tempFeelsLike))
        return String.format(context.getString(R.string.fm_temp_feels_like), tempFormat, tempFeelsLikeFormat)
    }

    fun formatTempMaxMin(context: Context, tempMax: Double, tempMin: Double): String {
        val tempMaxFormat = String.format(context.getString(R.string.fm_temp), tempMax)
        val tempMinFormat = String.format(context.getString(R.string.fm_temp), tempMin)
        return String.format(context.getString(R.string.fm_temp_max_min), tempMaxFormat, tempMinFormat)
    }

    fun formatTempMaxMinFah(context: Context, tempMax: Double, tempMin: Double): String {
        val tempMaxFormat = String.format(context.getString(R.string.fm_temp), convertCelsiusToFahrenheit(tempMax))
        val tempMinFormat = String.format(context.getString(R.string.fm_temp), convertCelsiusToFahrenheit(tempMin))
        return String.format(context.getString(R.string.fm_temp_max_min), tempMaxFormat, tempMinFormat)
    }

    fun formatTemp(context: Context, temp: Double): String {
        return String.format(context.getString(R.string.fm_temp_celsius), temp)
    }

    fun formatTempFah(context: Context, temp: Double): String {
        return String.format(context.getString(R.string.fm_temp_fah), convertCelsiusToFahrenheit(temp))
    }

    fun formatWindSpeed(context: Context, windSpeed: Double): String {
        return String.format(context.getString(R.string.fm_wind_speed), windSpeed.times(3600).div(1000))
    }

    fun formatWindSpeedMile(context: Context, windSpeed: Double): String {
        return String.format(
            context.getString(R.string.fm_wind_speed_mile),
            convertKilometerToMile(windSpeed.times(3600).div(1000))
        )
    }

    fun formatWind(context: Context, wind: Double, windDeg: Int): String {
        return String.format(context.getString(R.string.fm_wind_status), wind, formatWindDeg(context, windDeg))
    }

    fun formatWindMile(context: Context, wind: Double, windDeg: Int): String {
        return String.format(context.getString(R.string.fm_wind_status_mile), wind, formatWindDeg(context, windDeg))
    }

    fun formatWindDeg(context: Context, deg: Int): String {
        val getIndex = deg.div(22.5).plus(1).roundToInt()
        val listWindDeg = context.resources.getStringArray(R.array.wind_deg)
        return listWindDeg[getIndex.minus(1)]
    }

    fun formatHomeStatusAbove(context: Context, daily: Daily?): String {
        return String.format(context.getString(R.string.fm_string), upCaseFirstLetter(daily!!.weather[0].description))
    }

    fun formatHomeStatusBelow(context: Context, daily: Daily?): String {
        return String.format(
            context.getString(R.string.fm_status_home),
            upCaseFirstLetter(daily!!.weather[0].description),
            daily.temp.max,
            daily.temp.min,
            formatWindDeg(context, daily.wind_deg),
            daily.wind_speed
        )
    }

    fun formatHomeStatusBelowFah(context: Context, daily: Daily?): String {
        return String.format(
            context.getString(R.string.fm_status_home_fah),
            upCaseFirstLetter(daily!!.weather[0].description),
            convertCelsiusToFahrenheit(daily.temp.max),
            convertCelsiusToFahrenheit(daily.temp.min),
            formatWindDeg(context, daily.wind_deg),
            daily.wind_speed,
            formatPop(context, daily.pop)
        )
    }

    fun formatDailyNavStatus(context: Context, daily: Daily?): String {
        return String.format(
            context.getString(R.string.fm_status_daily_nav),
            upCaseFirstLetter(daily!!.weather[0].description),
            daily.temp.max,
            daily.temp.min,
            formatWindDeg(context, daily.wind_deg),
            daily.wind_speed,
            formatPop(context, daily.pop)
        )
    }

    fun formatDailyNavStatusFah(context: Context, daily: Daily?): String {
        return String.format(
            context.getString(R.string.fm_status_daily_nav_fah),
            upCaseFirstLetter(daily!!.weather[0].description),
            convertCelsiusToFahrenheit(daily.temp.max),
            convertCelsiusToFahrenheit(daily.temp.min),
            formatWindDeg(context, daily.wind_deg),
            convertKilometerToMile(daily.wind_speed),
            formatPop(context, daily.pop)
        )
    }

    fun formatLocation(context: Context, location: String): String {
        val listLocation: List<String> = location.split(",").map { it -> it.trim() }
        return String.format(context.getString(R.string.fm_show_location), listLocation[1], listLocation[2])
    }

    fun upCaseFirstLetter(letter: String): String {
        return letter.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    fun convertToBitMap(context: Context, id: Int): Bitmap {
        return BitmapFactory.decodeResource(context.resources, id)
    }

    fun readJSONFromAsset(context: Context): String? {
        var json: String? = null
        try {
            val inputStream: InputStream = context.assets.open(Constants.READ_JSON_FROM_ASSETS)
            json = inputStream.bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    fun String.fromJsonToLocation(): ArrayList<City> {
        val type = object : TypeToken<ArrayList<City>>() {}.type
        return Gson().fromJson(this, type)
    }

    fun convertCelsiusToFahrenheit(input: Double): Double {
        return input.times(1.8).plus(32)
    }

    fun convertKilometerToMile(input: Double): Double {
        return input.times(1.609344)
    }
}

