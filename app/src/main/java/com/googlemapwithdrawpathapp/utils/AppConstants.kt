package com.QuickHelpVendor.AppUtils

object AppConstants {

    public val SPLASH_TIMEOUT: Long = 4000
    public  var IS_PLATFORM: Int = 1

    public  var BEARER_TOKEN: String = "Bearer "
    public  var BEARER_APPLICATION: String = "application/json"
    var GOOGLE_KEY = ""
    public  var LOGIN_ROLE: String = "provider"

    //Country
    val COUNTRY_USA = "USA"
    val COUNTRY_AUSTRALIA = "AU"
    val COUNTRY_INDIA = "IN"
    public val IS_ANDROID: Int=1
    val SIGN_UP = 1
    val SOCIAL_MEDIA_FB = 0

    val MODE = "https://maps.googleapis.com/maps/api/distancematrix/json?"//units=imperial&
    val ORIGINS = "origin="
    val DESTINATION = "&destination="
    val KEY = "&key=AIzaSyB9UskfvpWqkdbHVn8afnkdeoSoOdyosio"

}