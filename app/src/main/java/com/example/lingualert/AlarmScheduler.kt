package com.example.lingualert

interface AlarmScheduler {
    suspend fun schedule(alarm: Alarm)
    suspend fun cancel(alarm: Alarm)
}