package com.example.lingualert

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert

/*
* methods used to access the databaase. where sqlite queries are written
* */

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAlarm(alarm: Alarm)

    @Delete
    suspend fun deleteAlarm(alarm: Alarm)

    @Query("SELECT * FROM alarm")
    fun getAlarms(): LiveData<List<Alarm>>

    @Query("SELECT COUNT(*) FROM alarm")
    fun getCount(): Int
}