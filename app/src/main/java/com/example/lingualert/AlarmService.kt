package com.example.lingualert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ContentInfoCompat.Flags

class AlarmService : Service() {
    // values required for NotificationChannel
    private val CHANNELNAME = "lingualert"
    private val CHANNELID = "lingualert31"
    private val NOTIFICATION_ID = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel(this)

        // create notification that will start alarm activity
        val notificiationBuilder =
            NotificationCompat.Builder(this, CHANNELID)
                .setSmallIcon(R.drawable.alarm)
                .setContentTitle("Wake up!")
                .setContentText("Time to do your lesson.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setOngoing(true)

        val incomingAlarmNotification = notificiationBuilder.build()

        // set alarm notification to include FLAG FOREGROUND SERVICE AND NO CLEAR FLAGS
        incomingAlarmNotification.flags = incomingAlarmNotification.flags or NotificationCompat.FLAG_FOREGROUND_SERVICE or NotificationCompat.FLAG_NO_CLEAR

        // create notification manager
        val notificationManager = NotificationManagerCompat.from(this)

        with(notificationManager) {
            notify(NOTIFICATION_ID, incomingAlarmNotification)
        }

        // must start the foreground service with the notification before the alarm activity
        startForeground(NOTIFICATION_ID, incomingAlarmNotification)

        // start the alarm activity
        val activityIntent = Intent(this, AlarmActivity::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK)
        startActivity(activityIntent)

        Log.d("TAG", "YUH ALARM RECEIVED")


        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        /* method where a client binds to the service,
        * the IBinder object returned is an interface that specifies
        * how a client can communicate with the service.
        *
        * no real use in alarm app, so return null
        * */
        return null
    }

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(CHANNELID, CHANNELNAME,
            NotificationManager.IMPORTANCE_HIGH).apply {
            lightColor = Color.GREEN // set color for devices with notif light to green
            enableLights(true)
        }
        // manager is default of type any, it is general. need to do as NotificationManager
        val manager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}