package com.example.lingualert

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.lingualert.data.InitialSettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(application: Application): AndroidViewModel(application) {
    // username entered (DO NOT USE MUTABLESTATEFLOW FOR THIS, ANTI PATTERN? WHAT IS ANTIPATTERN?)
    var username by mutableStateOf("")
    var currentStep by mutableStateOf(0)

    // move to the next screen
    fun advanceScreen() {
        currentStep++
    }

    // update username (not async, will make state out of sync)
    fun updateUsername(input: String) {
        username = input
    }

    // set the desired duolingo username
    fun saveUsername(duolingoUser: String) {
        // TODO: either use datastore or sharedprefs to store username
    }
}