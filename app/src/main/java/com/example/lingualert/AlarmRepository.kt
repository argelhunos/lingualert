package com.example.lingualert

import androidx.lifecycle.LiveData

/*
* repository to access sources of db
* */

class AlarmRepository(private val alarmDao: AlarmDao) {

    val readAllData: LiveData<List<Alarm>> = alarmDao.getAlarms()

    suspend fun upsertAlarm(alarm: Alarm) {
        alarmDao.upsertAlarm(alarm)
    }

    suspend fun deleteAlarm(alarm: Alarm) {
        alarmDao.deleteAlarm(alarm)
    }

    fun getCount(): Int {
        return alarmDao.getCount()
    }
}