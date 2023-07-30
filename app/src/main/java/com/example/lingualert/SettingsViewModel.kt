package com.example.lingualert

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class SettingsViewModel(application: Application): AndroidViewModel(application) {
    // username entered (DO NOT USE MUTABLESTATEFLOW FOR THIS, ANTI PATTERN? WHAT IS ANTIPATTERN?)
    var username by mutableStateOf("")
    var currentStep by mutableStateOf(0)

    // keep track if dialog to remind user needs to be shown
    var showDialog by mutableStateOf(false)

    // if the request permission button has been pressed before
    var permissionRequested by mutableStateOf<Boolean?>(null)

    // keep track if need to display webview of duolingo profile
    var canShowWebView by mutableStateOf(false)

    // keep track of webview loading to avoid blank loading screen
    var webViewLoaded by mutableStateOf(false)

    fun toggleDialog() {
        showDialog = !showDialog
    }

    fun togglePermissionRequest() {
        permissionRequested = true
    }

    // move to the next screen
    fun advanceScreen() {
        currentStep++
        Log.d("TAG", "$currentStep")
    }

    // update username in textfield (not async, will make state out of sync)
    fun updateUsername(input: String) {
        username = input
    }

    // display webview only if user has inputted a username
    fun tryWebView() {
        if (username != "") {
            canShowWebView = true
        }
    }



    // set the desired duolingo username
    fun saveUsername(duolingoUser: String) {
        // TODO: either use datastore or sharedprefs to store username
    }
}