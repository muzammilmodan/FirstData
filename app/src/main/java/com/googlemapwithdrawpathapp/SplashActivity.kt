package com.googlemapwithdrawpathapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.multidex.MultiDex
import com.google.firebase.FirebaseApp
import com.googlemapwithdrawpathapp.activity.GpsActivity
import com.googlemapwithdrawpathapp.activity.LocationToFromActivity
import java.util.*

class SplashActivity : AppCompatActivity() {

    var context: Context? = null
    private var locationManager: LocationManager? = null
    public val TAG = "SplashScreen"
    val MY_PERMISSIONS_REQUEST_ACCOUNTS = 1

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)
        context = SplashActivity@ this

        splashTimeOut()
    }


    fun isGPSEnable(): Boolean {
        var isGPSEnable = false
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isGPSEnable = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return isGPSEnable
    }

    private fun splashTimeOut() {
        try {
            val timer = Timer()
            timer.schedule(object : TimerTask() {

                override fun run() {

                    // SessionManager.setRefererCode(this@SplashActivity,"")
                    val intent = Intent(context, LocationToFromActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)

                }
            }, 3000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.e(TAG, "onRequestPermissionsResult()")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCOUNTS -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            if (ContextCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.INTERNET
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                splashTimeOut()

                            }
                        }
                    }

                } else {
                    permissionsDenied()
                }
            }
        }
    }

    // App cannot work without the permissions
    private fun permissionsDenied() {
        Log.e(TAG, "permissionsDenied()")
        // TODO close app and warn user
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}
