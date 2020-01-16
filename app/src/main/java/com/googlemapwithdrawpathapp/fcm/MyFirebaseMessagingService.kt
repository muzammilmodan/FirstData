package com.googlemapwithdrawpathapp.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.googlemapwithdrawpathapp.R
import com.googlemapwithdrawpathapp.activity.LocationToFromActivity
import com.googlemapwithdrawpathapp.fcm.Config
import com.googlemapwithdrawpathapp.fcm.Config.NOTIFICATION_ID
import com.googlemapwithdrawpathapp.fcm.NotificationUtils
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = MyFirebaseMessagingService::class.java.simpleName
    private var notificationUtils: NotificationUtils? = null
    var context: Context = this
    var keyNotification: String=""
    var keyValues:String=""
    var notifyTitle:String?=""
    var notifyMessage:String? =""
    var packageMessage: String=""
    var packageTitle:String=""
    var notifiCount:String=""
    //var msg_type:String=""

    var job_id:String=""
    var notification_type: String=""
    var sub_category_name:String=""
    var category_name:String=""
    var categoryHoverImg:String=""
    var property_address:String=""

    override fun onMessageReceived(p0: RemoteMessage) {
        Log.e(TAG, "From: " + p0!!.from!!)

        /*if (remoteMessage == null)
            return*/


        try {
            for ((key, value) in p0.data) {

                Log.d("1----=----", "key, $key value $value")
                if (key == "job_id") {
                    job_id = value
                    Log.e("Notification===>","JOBIDSSS222::::" +job_id)
                    Log.e("Notification===>","job_id Notification..........."+job_id)
                }
                if (key == "sub_category_name") {
                    sub_category_name = value
                    Log.e("Notification===>","job_id Notification..........."+sub_category_name)
                }
                if (key == "notification_type") {
                    notification_type = value
                    Log.e("Notification===>","job_id Notification..........."+notification_type)
                }
                if (key == "category_name") {
                    category_name = value
                    Log.e("Notification===>","job_id Notification..........."+category_name)
                }
                if (key == "categoryHoverImg") {
                    categoryHoverImg = value
                    Log.e("Notification===>","job_id Notification..........."+categoryHoverImg)
                }
                if (key == "property_address") {
                    property_address = value
                    Log.e("Notification===>","job_id Notification..........."+property_address)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (p0.notification != null) {
            try {
                Log.e(TAG, "Notification Body: " + p0.notification!!.body!!)
                notifyTitle = p0.notification!!.title
                notifyMessage = p0.notification!!.body
                Log.e("notifyTitle", "=== $notifyTitle---notCount------$notifiCount")
                Log.e("Notification===>","JOBIDSSS111::::" +job_id)
                handleDataMessage(notifyTitle!!, notifyMessage!!,notification_type,job_id,category_name,sub_category_name,categoryHoverImg,property_address)
            } catch (e: Exception) {
                Log.e(TAG, "Exception: " + e.message)
            }

        }

        // Check if message contains a data payload.
        if (p0.data.size > 0) {
            Log.e("Notification===>","====>")
            Log.e(TAG, "Data Payload: " + p0.data.toString())
            try {
                for ((key, value) in p0.data) {
                    keyNotification = key
                    keyValues = value

                    Log.e("Notification===>","-------------------$keyNotification")
                    Log.e("Notification===>","-------------------$keyValues")

                    if (keyNotification == "gcm.notification.bedge") {
                        notifiCount = keyValues
                        Log.e("Notification===>","notCount-3---------====$notifiCount")
                    }
                    if (keyNotification == "gcm.notification.title") {
                        packageTitle = keyValues
                    }
                    if (keyNotification == "gcm.notification.body") {
                        packageMessage = keyValues
                    }
                    if (keyNotification == "job_id") {
                        job_id = keyValues
                        Log.e("Notification===>","Get job_id..........."+job_id)
                        Log.e("Notification===>","JOBIDSSS333::::" +job_id)
                    }
                    if (keyNotification == "sub_category_name") {
                        sub_category_name = keyValues
                        Log.e("Notification===>","Getjob_id ..........."+sub_category_name)
                    }
                    if (keyNotification == "notification_type") {
                        notification_type = keyValues
                        Log.e("Notification===>","job_id Notification..........."+notification_type)
                    }
                    if (keyNotification == "category_name") {
                        category_name = keyValues
                        Log.e("Notification===>","job_id Notification..........."+category_name)
                    }
                    if (keyNotification == "categoryHoverImg") {
                        categoryHoverImg = keyValues
                        Log.e("Notification===>","job_id Notification..........."+categoryHoverImg)
                    }
                    if (keyNotification == "property_address") {
                        property_address = keyValues
                        Log.e("Notification===>","job_id Notification..........."+property_address)
                    }

                }
                if (!packageTitle.isEmpty() && !packageMessage.isEmpty())
                handleDataMessage(packageTitle, packageMessage,notification_type,job_id,category_name,sub_category_name,categoryHoverImg,property_address)
            } catch (e: Exception) {
                Log.e(TAG, "Exception: " + e.message)
            }

        }


        Log.d("msg", "onMessageReceived: " + p0.data["message"])

    }


    var notification: Notification? = null
    var builder: NotificationCompat.Builder? = null
    var bmIcon: Bitmap? = null
    var notificatioId: Long = 0
    var manager: NotificationManager? = null


    private fun handleDataMessage(title: String, message: String, notification_type: String?,
                                  job_id: String?,category_name: String?,sub_category_name: String?,categoryHoverImg: String?,property_address: String?) {
        try {
            Log.e("Notification===>","JOBIDSSS444::::" +job_id)
            Log.e("Notification===>","notCount-4---------====$title   $message   $notification_type")

            if (!NotificationUtils.isAppIsInBackground(applicationContext)) {

//Not app is background
                val pushNotification = Intent(Config.PUSH_NOTIFICATION)
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)

                val notificationUtils = NotificationUtils(applicationContext)
                notificationUtils.playNotificationSound()

                /*val resultIntent = Intent(applicationContext, JobDetailsActivity::class.java)
                resultIntent.putExtra("job_id", job_id)
                startActivity(resultIntent)*/

              /*  if (msg_type != null && msg_type == "video_call_request") {
                    if (SessionManager.getRole(context).equals(DOCTOR_ROLE)) {
                        val resultIntent = Intent(applicationContext, Doctor_CallArrivalActivity::class.java)
                        resultIntent.putExtra("doctor_id", doctor_id)
                        resultIntent.putExtra("patient_id", patient_id)
                        resultIntent.putExtra("room_number", room_number)
                        startActivity(resultIntent)
                    } else {
                        val resultIntent = Intent(applicationContext, Patient_VideoCallingActivity::class.java)
                        resultIntent.putExtra("doctor_id", doctor_id)
                        resultIntent.putExtra("patient_id", patient_id)
                        resultIntent.putExtra("room_number", room_number)
                        startActivity(resultIntent)
                    }
                } else {
                    if (SessionManager.getRole(context).equals(DOCTOR_ROLE)) {
                        val resultIntent = Intent(applicationContext, DoctorDashboardActivity::class.java)
                        resultIntent.putExtra("message", message)
                        startActivity(resultIntent)
                    } else {
                        val resultIntent = Intent(applicationContext, Patient_DashboardActivity::class.java)
                        resultIntent.putExtra("message", message)
                        startActivity(resultIntent)
                    }
                }
                */

                sendNotification(title,message,notification_type,job_id,category_name,sub_category_name,categoryHoverImg,property_address)

            } else {
                val pushNotification = Intent(Config.PUSH_NOTIFICATION)
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)

                val notificationUtils = NotificationUtils(applicationContext)
                notificationUtils.playNotificationSound()

                sendNotification(title,message,notification_type,job_id,category_name,sub_category_name,categoryHoverImg,property_address)

              /*  val resultIntent = Intent(applicationContext, JobDetailsActivity::class.java)
                resultIntent.putExtra("job_id", job_id)
                startActivity(resultIntent)*/

// app is background

               /* if (msg_type != null && msg_type == "video_call_request") {
                    if (SessionManager.getRole(context).equals(DOCTOR_ROLE)) {
                        val resultIntent = Intent(applicationContext, Doctor_CallArrivalActivity::class.java)
                        resultIntent.putExtra("doctor_id", doctor_id)
                        resultIntent.putExtra("patient_id", patient_id)
                        resultIntent.putExtra("room_number", room_number)
                        startActivity(resultIntent)
                    } else {
                        val resultIntent = Intent(applicationContext, Patient_VideoCallingActivity::class.java)
                        resultIntent.putExtra("message", message)
                        resultIntent.putExtra("doctor_id", doctor_id)
                        resultIntent.putExtra("patient_id", patient_id)
                        resultIntent.putExtra("room_number", room_number)
                        startActivity(resultIntent)
                    }
                } else {
                    if (SessionManager.getRole(context).equals(DOCTOR_ROLE)) {
                        val resultIntent = Intent(applicationContext, DoctorDashboardActivity::class.java)
                        resultIntent.putExtra("message", message)
                        startActivity(resultIntent)
                    } else {
                        val resultIntent = Intent(applicationContext, Patient_DashboardActivity::class.java)
                        resultIntent.putExtra("message", message)
                        startActivity(resultIntent)
                    }
                }*/


            }
        } catch (e: Exception) {
            Log.e(TAG, "Json Exception: " + e.message)
        }

    }


    /**
     * Showing notification with text only
     */
    private fun showNotificationMessage(context: Context, title: String, message: String, intent: Intent) {
        notificationUtils = NotificationUtils(context)
        intent.putExtras(intent.extras!!)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        notificationUtils!!.showNotificationMessage(title, message, intent)

        /*notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, intent);*/
    }

    /**
     * Showing notification with text and image
     */
    private fun showNotificationMessageWithBigImage(
        context: Context, title: String, message: String,
        imageUrl: String, intent: Intent
    ) {
        notificationUtils = NotificationUtils(context)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        notificationUtils!!.showNotificationMessage(title, message, intent, imageUrl)
    }


    fun getBitmapFromURL(strURL: String): Bitmap? {
        try {
            val url = URL(strURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input = connection.getInputStream()
            return BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }

    var notificationBuilder: NotificationCompat.Builder? = null
    var notificationManager: NotificationManager? = null
    val channelId = "QuickHelp"

    private fun sendNotification(title: String?, body: String?,notification_type:String?,
                                 job_id: String?,category_name: String?,sub_category_name: String?,categoryHoverImg: String?,property_address: String?) {

        try {
            if (notification_type.equals("job_details")) {
                var intent = Intent(this, LocationToFromActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                var pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT
                )

                val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                val mNotificationManager = getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager
                mNotificationManager.cancel(NOTIFICATION_ID)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    val nameChannel = getString(R.string.app_name)
                    val descChannel = getString(R.string.app_name)
                    val importance = NotificationManager.IMPORTANCE_DEFAULT

                    val channel = NotificationChannel(getString(R.string.app_name), nameChannel, importance)
                    channel.description = descChannel
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    assert(mNotificationManager != null)
                    mNotificationManager.createNotificationChannel(channel)

                } else {
                    notificationBuilder = NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setVibrate(longArrayOf(100, 250))
                        .setDefaults(Notification.DEFAULT_SOUND).setAutoCancel(true)

                    notificationBuilder!!.setContentIntent(pendingIntent)

                }


                notificationBuilder = NotificationCompat.Builder(this, "Notification")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(notificationSound)
                    .setContentIntent(pendingIntent)

                notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager!!.notify(0, notificationBuilder!!.build())
            } else {
                var intent = Intent(this, LocationToFromActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("Notification", body)

                var pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT
                )

                val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                val mNotificationManager = getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager
                mNotificationManager.cancel(NOTIFICATION_ID)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    val nameChannel = getString(R.string.app_name)
                    val descChannel = getString(R.string.app_name)
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val channel = NotificationChannel(getString(R.string.app_name), nameChannel, importance)
                    channel.description = descChannel
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    assert(mNotificationManager != null)
                    mNotificationManager.createNotificationChannel(channel)

                } else {
                    notificationBuilder = NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setVibrate(longArrayOf(100, 250))
                        .setDefaults(Notification.DEFAULT_SOUND).setAutoCancel(true)

                    notificationBuilder!!.setContentIntent(pendingIntent)

                }


                notificationBuilder = NotificationCompat.Builder(this, "Notification")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(notificationSound)
                    .setContentIntent(pendingIntent)

                notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager!!.notify(0, notificationBuilder!!.build())
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


}