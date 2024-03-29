package com.example.lingualert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService

/*
* unfinished: alarm receiver to eventually wake up the app and indicate to user to do language lesson
* */

class AlarmReceiver: BroadcastReceiver() {
    // values required for NotificationChannel
    private val CHANNEL_NAME = "lingualert"
    private val CHANNEL_ID = "lingualert31"
    private val NOTIFICATION_ID = 0

    override fun onReceive(context: Context, intent: Intent) {
        // create high-priority notification to tell user to open duolingo

//        val fullScreenIntent = Intent(context, AlarmActivity::class.java)
//        val fullScreenPendingIntent = PendingIntent.getActivity(context, 0, fullScreenIntent, FLAG_IMMUTABLE)
//
//        val notificiationBuilder =
//            NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.drawable.alarm)
//                .setContentTitle("Wake up!")
//                .setContentText("Time to do your lesson.")
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCategory(NotificationCompat.CATEGORY_ALARM)
//                .setOngoing(true)
//
//                // using full screen intent to launch alarm activity
//                .setFullScreenIntent(fullScreenPendingIntent, true)
//
//        val incomingAlarmNotification = notificiationBuilder.build()
//
//        // set alarm notification to include FLAG FOREGROUND SERVICE AND NO CLEAR FLAGS
//        incomingAlarmNotification.flags = incomingAlarmNotification.flags or NotificationCompat.FLAG_FOREGROUND_SERVICE or NotificationCompat.FLAG_NO_CLEAR
//
//        // create notification manager
//        val notificationManager = NotificationManagerCompat.from(context)
//
//        with(notificationManager) {
//            notify(NOTIFICATION_ID, incomingAlarmNotification)
//        }

        // check if the intent is the one that is from the alarm created
        val intentAction = intent.action

        if (intentAction == "com.example.lingualert.ALARM_RECEIVED") {
            // create an intent that will start the service
            val serviceIntent = Intent(context, AlarmService::class.java)

            // start service
            context.startService(serviceIntent)

//            Log.d("TAG", "YUH ALARM RECEIVED")
        }


    }

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH).apply {
                lightColor = Color.GREEN // set color for devices with notif light to green
                enableLights(true)
        }
        // manager is default of type any, it is general. need to do as NotificationManager
        val manager = getSystemService(context, NotificationManager::class.java) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}