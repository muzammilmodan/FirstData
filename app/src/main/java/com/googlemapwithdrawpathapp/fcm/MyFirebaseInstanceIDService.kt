package com.googlemapwithdrawpathapp.fcm

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId

class MyFirebaseInstanceIDService {
    private var TAG = "MyFirebaseInstanceIDService"

    fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        var refreshedToken = FirebaseInstanceId.getInstance().token
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {

    }
}