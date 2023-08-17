package com.example.lingualert

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: find the difference between withContext and viewmodel scope again

class AlarmViewModel(application: Application): AndroidViewModel(application) {
    private val _readAllData: LiveData<List<Alarm>>
    val allAlarms: LiveData<List<Alarm>>
    private val repository: AlarmRepository
    private val alarmScheduler: AlarmScheduler

    init {
        val alarmDao = AlarmDatabase.getDatabase(application).dao()
        repository = AlarmRepository(alarmDao)
        _readAllData = repository.readAllData
        allAlarms = _readAllData
        alarmScheduler = AndroidAlarmScheduler(getApplication<Application>().applicationContext, alarmDao)
    }

    fun addAlarm(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            //repository.upsertAlarm(alarm)
            alarmScheduler.schedule(alarm)
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            alarmScheduler.cancel(alarm)
        }
    }

    suspend fun getCount(): Int {
        var count: Int
        withContext(Dispatchers.IO) {
             count = repository.getCount()
        }
        return count
    }
}