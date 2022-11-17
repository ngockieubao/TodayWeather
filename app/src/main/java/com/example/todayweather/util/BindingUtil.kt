package com.example.todayweather.util

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.todayweather.data.model.Current
import com.example.todayweather.data.model.Daily
import com.example.todayweather.util.Utils.status

private val statusUnitConversion: String? = status

@BindingAdapter("setIcon")
fun ImageView.setIcon(url: String?) {
    val urlPrefix = Constants.URL_ICON_PREFIX
    val urlSuffix = Constants.URL_ICON_SUFFIX
    Glide.with(this.context)
        .load("$urlPrefix$url$urlSuffix")
        .into(this)
}

@BindingAdapter("setTemp")
fun TextView.setTemp(temp: Double) {
    if (statusUnitConversion == Constants.CELCIUS)
        this.text = Utils.formatTemp(context, temp)
    if (statusUnitConversion == Constants.FAHRENHEIT)
        this.text = Utils.formatTempFah(context, temp)
    else
        this.text = Utils.formatTemp(context, temp)
}

@BindingAdapter("setDewPoint")
fun TextView.setDewPoint(input: Double) {
    if (statusUnitConversion == Constants.CELCIUS)
        this.text = Utils.formatDewPoint(context, input)
    if (statusUnitConversion == Constants.FAHRENHEIT)
        this.text = Utils.formatDewPointFah(context, input)
    else
        this.text = Utils.formatDewPoint(context, input)
}

@BindingAdapter("setPop")
fun TextView.setPop(pop: Double) {
    this.text = Utils.formatPop(context, pop)
}

@BindingAdapter("setHumidity")
fun TextView.setHumidity(input: Double) {
    this.text = Utils.formatHumidity(context, input)
}

@BindingAdapter("setWindSpeed")
fun TextView.setWindSpeed(windSpeed: Double) {
    if (statusUnitConversion == Constants.CELCIUS)
        this.text = Utils.formatWindSpeed(context, windSpeed)
    if (statusUnitConversion == Constants.FAHRENHEIT)
        this.text = Utils.formatWindSpeedMile(context, windSpeed)
    else
        this.text = Utils.formatWindSpeed(context, windSpeed)
}

@SuppressLint("SimpleDateFormat")
@BindingAdapter("setTime")
fun TextView.setTime(dt: Long) {
    this.text = Utils.formatTime(context, dt)
}

@BindingAdapter("setDate")
fun TextView.setDate(dt: Long) {
    this.text = Utils.formatDateFull(context, dt)
}

@BindingAdapter(value = ["setTempMax", "setTempMin"])
fun TextView.setTempMaxMin(tempMax: Double, tempMin: Double) {
    if (statusUnitConversion == Constants.CELCIUS)
        this.text = Utils.formatTempMaxMin(context, tempMax, tempMin)
    if (statusUnitConversion == Constants.FAHRENHEIT)
        this.text = Utils.formatTempMaxMinFah(context, tempMax, tempMin)
    else
        this.text = Utils.formatTempMaxMin(context, tempMax, tempMin)
}

@BindingAdapter(value = ["setTempCurrent", "setFeelsLike"])
fun TextView.setTempFeelsLike(temp: Double, feelsLike: Double) {
    if (statusUnitConversion == Constants.CELCIUS)
        this.text = Utils.formatTempFeelsLike(context, temp, feelsLike)
    if (statusUnitConversion == Constants.FAHRENHEIT)
        this.text = Utils.formatTempFeelsLikeFah(context, temp, feelsLike)
    else
        this.text = Utils.formatTempFeelsLike(context, temp, feelsLike)
}

@BindingAdapter("setStatus")
fun TextView.setStatus(description: String) {
    this.text = Utils.upCaseFirstLetter(description)
}

@BindingAdapter(value = ["setWindStatusSpeed", "setWindStatusDescription"])
fun TextView.setWindStatus(windSpeed: Double, windDeg: Int) {
    if (statusUnitConversion == Constants.CELCIUS)
        this.text = Utils.formatWind(context, windSpeed, windDeg)
    if (statusUnitConversion == Constants.FAHRENHEIT)
        this.text = Utils.formatWindMile(context, windSpeed, windDeg)
    else
        this.text = Utils.formatWind(context, windSpeed, windDeg)
}

@BindingAdapter("setHomeStatusAbove")
fun TextView.setHomeStatusAbove(daily: Daily?) {
    try {
        this.text = Utils.formatHomeStatusAbove(
            context, daily!!
        )
    } catch (ex: NullPointerException) {
        LogUtils.logDebug("null", ex.toString())
    }
}

@BindingAdapter(value = ["setHomeStatusBelow", "setHomeStatusBelowFah"])
fun TextView.setHomeStatusBelow(current: Current?, daily: Daily?) {
    try {
        if (statusUnitConversion == Constants.CELCIUS)
            this.text = Utils.formatHomeStatusBelow(context, current!!, daily!!)
        if (statusUnitConversion == Constants.FAHRENHEIT)
            this.text = Utils.formatHomeStatusBelowFah(context, current!!, daily!!)
        else
            this.text = Utils.formatHomeStatusBelow(context, current!!, daily!!)
    } catch (ex: NullPointerException) {
        LogUtils.logDebug("null", ex.toString())
    }
}

@BindingAdapter("setDailyNavStatus")
fun TextView.setDailyNavStatus(daily: Daily?) {
    try {
        if (statusUnitConversion == Constants.CELCIUS)
            this.text = Utils.formatDailyNavStatus(context, daily!!)
        if (statusUnitConversion == Constants.FAHRENHEIT)
            this.text = Utils.formatDailyNavStatusFah(context, daily!!)
        else
            this.text = Utils.formatDailyNavStatus(context, daily!!)
    } catch (ex: NullPointerException) {
        LogUtils.logDebug("null", ex.toString())
    }
}
