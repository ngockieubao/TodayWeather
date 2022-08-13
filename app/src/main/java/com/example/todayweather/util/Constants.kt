package com.example.todayweather.util

object Constants {
    // Location
    const val INTERVAL = 50000L
    const val FASTEST_INTERVAL = 10000L
    const val DELAY = 2000L
    const val PACKAGE = "package"

    // Broadcast Receiver
    const val BROADCAST_RECEIVER_NETWORK_STATUS = "com.example.todayweather.NetworkReceiver"
    const val BROADCAST_RECEIVER_PUSH_NOTIFICATIONS = "com.example.todayweather.AlarmManager"

    // Shared preference keys
    const val CELSIUS = "celsius"
    const val FAHRENHEIT = "fahrenheit"

    // Bundle keys
    const val KEY_BUNDLE_SELECT_CITY = "myKey"

    // Service push notifications
    const val ONGOING_NOTIFICATION_ID = 2

    // Database key
    const val DATABASE = "database"

    // Webservice API
    const val BASE_URL = "https://api.openweathermap.org/"
    const val URL_PATH = "data/2.5/onecall"

    // Danang_lat-lon
    const val URL_LAT = "lat"
    const val URL_LON = "lon"
    const val URL_EXCLUDE = "exclude"
    const val URL_APPID = "appid"
    const val URL_LANG = "lang"
    const val URL_UNITS = "units"
    const val URL_LAT_VALUE = 16.047079
    const val URL_LON_VALUE = 108.206230
    const val URL_EXCLUDE_VALUE = "minutely,alert"
    const val URL_APPID_VALUE = "53fbf527d52d4d773e828243b90c1f8e"
    const val URL_LANG_VALUE = "vi"
    const val URL_UNITS_VALUE = "metric"
    const val URL_ICON_PREFIX = "https://openweathermap.org/img/wn/"
    const val URL_ICON_SUFFIX = "@2x.png"

    // Utils format
    const val LOCALE_LANG = "vi"
    const val READ_JSON_FROM_ASSETS = "Cities.json"
}