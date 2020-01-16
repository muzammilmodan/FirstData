package com.googlemapwithdrawpathapp.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.widget.Toast
import android.location.LocationManager
import android.util.Log
import com.googlemapwithdrawpathapp.R
import android.content.IntentSender
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback


class GpsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gps)

        // Todo Location Already on  ... start
        val manager = this@GpsActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
       /* if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this@GpsActivity)) {
            Toast.makeText(this@GpsActivity, "Gps already enabled", Toast.LENGTH_SHORT).show()
            finish()
        }
        // Todo Location Already on  ... end

        if (!hasGPSDevice(this@GpsActivity)) {
            Toast.makeText(this@GpsActivity, "Gps not Supported", Toast.LENGTH_SHORT).show()
        }*/

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this@GpsActivity)) {
            Log.e("keshav", "Gps already enabled")
            Toast.makeText(this@GpsActivity, "Gps not enabled", Toast.LENGTH_SHORT).show()
            enableLoc()
        } else {
            Log.e("keshav", "Gps already enabled")
            Toast.makeText(this@GpsActivity, "Gps already enabled", Toast.LENGTH_SHORT).show()
        }

    }

    private fun hasGPSDevice(context: Context): Boolean {
        val mgr = context
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager ?: return false
        val providers = mgr.allProviders ?: return false
        return providers.contains(LocationManager.GPS_PROVIDER)
    }

    var googleApiClient: GoogleApiClient? = null
    val REQUEST_LOCATION = 1520

    private fun enableLoc() {
        try {

            if (googleApiClient == null) {
                googleApiClient = GoogleApiClient.Builder(this@GpsActivity)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                        override fun onConnected(bundle: Bundle?) {
                        }

                        override fun onConnectionSuspended(i: Int) {
                            googleApiClient!!.connect()
                        }
                    })
                    .addOnConnectionFailedListener { connectionResult ->
                        Log.d(
                            "Location error",
                            "Location error " + connectionResult.errorCode
                        )
                    }
                    .build()
                googleApiClient!!.connect()

                val locationRequest = LocationRequest.create()
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                locationRequest.interval = (30 * 1000).toLong()
                locationRequest.fastestInterval = (5 * 1000).toLong()
                val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
                builder.setAlwaysShow(true)

                val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
                result.setResultCallback(object : ResultCallback<LocationSettingsResult> {
                    override fun onResult(result: LocationSettingsResult) {
                        val status = result.status
                        when (status.statusCode) {
                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                                status.startResolutionForResult(
                                    this@GpsActivity,
                                    REQUEST_LOCATION
                                )

                                finish()
                            } catch (e: IntentSender.SendIntentException) {
                                // Ignore the error.
                            }

                        }
                    }
                })
            }
        } catch (e: Exception) {
        }


    }

}
