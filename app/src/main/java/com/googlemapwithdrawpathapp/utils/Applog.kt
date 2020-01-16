package com.QuickHelpVendor.Utils

import android.util.Log

object Applog {

    fun E(msg: String) {

        Log.e("Google Map App", msg)

    }

    fun e(tag: String, msg: String) {

        Log.e(tag, msg)

    }

    fun d(tag: String, msg: String) {

        Log.d(tag, msg)

    }

    fun v(tag: String, msg: String) {

        Log.v(tag, msg)

    }

    fun i(tag: String, msg: String) {

        Log.i(tag, msg)

    }
}