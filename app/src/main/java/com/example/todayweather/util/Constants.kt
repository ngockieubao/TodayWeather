package com.example.todayweather.util

object Constants {

    // Location
    const val INTERVAL = 2000L
    const val FASTEST_INTERVAL = 1000L
    const val DELAY = 2000L
    const val PACKAGE = "package"
    const val TIME_IMMORTAL = -2
    const val TIME_SHORT = -1

    // Broadcast Receiver
    const val BROADCAST_RECEIVER_PUSH_NOTIFICATIONS = "com.example.todayweather.AlarmManager"

    // Bundle keys
    const val KEY_BUNDLE_SELECT_CITY = "myKey"

    // Service push notifications
    const val ONGOING_NOTIFICATION_ID = 2

    // Database key
    const val DATABASE = "weather_database"

    // Webservice API
    const val BASE_URL = "https://api.openweathermap.org/"
    const val URL_PATH = "data/2.5/onecall"
    const val URL_LAT = "lat"
    const val URL_LON = "lon"
    const val URL_EXCLUDE = "exclude"
    const val URL_APPID = "appid"
    const val URL_LANG = "lang"
    const val URL_UNITS = "units"
    const val URL_EXCLUDE_VALUE = "minutely,alert"
    const val URL_APPID_VALUE = "53fbf527d52d4d773e828243b90c1f8e"
    const val URL_LANG_VALUE = "vi"
    const val URL_UNITS_VALUE_DEFAULT = "metric"
    const val URL_UNITS_VALUE_IMPERIAL = "imperial"
    const val URL_ICON_PREFIX = "https://openweathermap.org/img/wn/"
    const val URL_ICON_SUFFIX = "@2x.png"

    // Utils format
    const val LOCALE_LANG = "vi"
    const val READ_JSON_FROM_ASSETS = "Cities.json"

    // Unique
    const val REQUEST_PERMISSION_CODE = 10

    // Delay second
    const val ONE_SECOND = 1000L
    const val ONE_POINT_FIVE_SECONDS = 1500L

    // SharedPrefs
    const val SHARED_PREFS = "shared_prefs"
    const val SHARED_PREFS_FIRST_RUN = "shared_prefs_first_run"
    const val SHARED_PREFS_LAT = "lat"
    const val SHARED_PREFS_LON = "lon"
    const val CELCIUS = "celcius"
    const val FAHRENHEIT = "fah"
}