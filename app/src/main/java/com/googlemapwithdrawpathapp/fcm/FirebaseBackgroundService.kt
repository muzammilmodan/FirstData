package com.googlemapwithdrawpathapp.fcm

import android.content.Context
import android.content.Intent
import com.facebook.FacebookSdk.getApplicationContext
import org.json.JSONObject
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.legacy.content.WakefulBroadcastReceiver
import com.googlemapwithdrawpathapp.fcm.NotificationUtils

class FirebaseBackgroundService : WakefulBroadcastReceiver() {

    private val TAG = "FirebaseService"
    var builder: NotificationCompat.Builder? = null
    var notification_flag: Boolean = false

    var noti_type: String? = null
    var doctor_id:String? = null
    var patient_id:String? = null
    var room_number:String? = null
    var title: String? = null
    var message:String? = null
    var bedge:String? = null
    private val resultIntent: Intent? = null

    override fun onReceive(context: Context, intent: Intent) {

        try {
            if (intent.extras != null) {

                for (key in intent.extras!!.keySet()) {
                    Log.e("key->", intent.extras!!.keySet().toString())
                    if (key.contains("data")) {
                        notification_flag = true
                    } else if (key == "gcm.notification.badge") {
                        bedge = intent.extras!!.getString("gcm.notification.bedge")
                    }
                }
                // gcm.notification.badge
                if (intent.extras != null) {

                    if (intent.extras!!.getString("gcm.notification.bedge") != null) {
                        bedge = intent.extras!!.getString("gcm.notification.bedge")
                    }
                    if (intent.extras!!.getString("gcm.notification.title") != null) {
                        title = intent.extras!!.getString("gcm.notification.title")
                    }

                    if (intent.extras!!.getString("gcm.notification.body") != null) {
                        message = intent.extras!!.getString("gcm.notification.body")
                    }

                    if (intent.extras!!.getString("data") != null) {


                        val value = intent.extras!!.getString("data")
                        val obj = JSONObject(value)

                        val notificationUtils = NotificationUtils(getApplicationContext())
                        notificationUtils.playNotificationSound()

                        noti_type = obj.get("notification_type").toString()
                        Log.e("noti_type", noti_type)


                      /*
                        if (noti_type.equals("video_call_request")) {
                            doctor_id = obj.get("doctor_id").toString()
                            patient_id = obj.get("patient_id").toString()
                            room_number = obj.get("call_room_number").toString()

                            if (SessionManager.getRole(context).equals(DOCTOR_ROLE)) {
                                val bundle = Bundle()
                                val backgroundIntent = Intent("video_call_request_doctor")
                                bundle.putString("doctor_id", doctor_id)
                                bundle.putString("patient_id", patient_id)
                                bundle.putString("room_number", room_number)
                                backgroundIntent.putExtras(bundle)
                                LocalBroadcastManager.getInstance(context).sendBroadcast(backgroundIntent)
                            } else {
                                val bundle = Bundle()
                                val backgroundIntent = Intent("video_call_request_patient")
                                bundle.putString("doctor_id", doctor_id)
                                bundle.putString("patient_id", patient_id)
                                bundle.putString("room_number", room_number)
                                backgroundIntent.putExtras(bundle)
                                LocalBroadcastManager.getInstance(context).sendBroadcast(backgroundIntent)
                            }
                        } else {
                            if (SessionManager.getRole(context).equals(DOCTOR_ROLE)) {
                                val bundle = Bundle()
                                val backgroundIntent = Intent("doctor_noti")
                                backgroundIntent.putExtras(bundle)
                                LocalBroadcastManager.getInstance(context).sendBroadcast(backgroundIntent)
                            } else {
                                val bundle = Bundle()
                                val backgroundIntent = Intent("patient_noti")
                                backgroundIntent.putExtras(bundle)
                                LocalBroadcastManager.getInstance(context).sendBroadcast(backgroundIntent)
                            }
                        }*/

                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}