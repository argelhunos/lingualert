package com.example.lingualert

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar

/*
* schedule alarms set by the user or cancel them. stores into database to keep inside
*
* BECAUSE U KEEP FORGOR:
* Intent - obj carrying intent, like message from one component to another.
*          can communicate messages among any of the three. what it intends to do
* PendingIntent - token given to foreign app (ex. alarm manager) to use application's perms to execute
*                 predefined piece of code
*
* */

class AndroidAlarmScheduler(
    private val context: Context,
    private val dao: AlarmDao
): AlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    private lateinit var alarmIntent: PendingIntent

    override suspend fun schedule(alarm: Alarm) {
        // create intent for alarm with specified time
        alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, alarm.requestCode, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        // set alarm with user specified time
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.min)
        }

        alarmManager?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            alarmIntent
        )
        Log.d("TAG", "alarm set")

        // save alarm information into db
        dao.upsertAlarm(alarm)
    }

    override suspend fun cancel(alarm: Alarm) {
        // must construct the same intent that was used earlier
        alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, alarm.requestCode, intent, PendingIntent.FLAG_IMMUTABLE)
        }
        alarmManager.cancel(alarmIntent)

        // delete alarm from db
        dao.deleteAlarm(alarm)
    }

}