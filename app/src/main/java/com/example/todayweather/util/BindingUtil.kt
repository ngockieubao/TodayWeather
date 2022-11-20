package com.example.todayweather.util

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.todayweather.data.model.Daily

@BindingAdapter("setIcon")
fun ImageView.setIcon(url: String?) {
    val urlPrefix = Constants.URL_ICON_PREFIX
    val urlSuffix = Constants.URL_ICON_SUFFIX
    Glide.with(this.context)
        .load("$urlPrefix$url$urlSuffix")
        .into(this)
}

@BindingAdapter("setPop")
fun TextView.setPop(pop: Double) {
    this.text = Utils.formatPop(context, pop)
}

@BindingAdapter("setHumidity")
fun TextView.setHumidity(input: Double) {
    this.text = Utils.formatHumidity(context, input)
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

@BindingAdapter("setStatus")
fun TextView.setStatus(description: String) {
    this.text = Utils.upCaseFirstLetter(description)
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
