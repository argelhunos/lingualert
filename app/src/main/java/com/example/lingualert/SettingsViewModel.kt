package com.example.lingualert

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingualert.preferencedatastore.DataStoreManager
import com.example.lingualert.preferencedatastore.UserDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    // keep track of error of username text
    var textFieldError by mutableStateOf("")

    // lateinit allows to avoid initializing a property when an object is constructed.
    lateinit var dataStoreManager: DataStoreManager


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
        if (textFieldError.isBlank()) {
            canShowWebView = true
        }
    }

    fun checkTextView() {
        textFieldError = if (username.isBlank()) {
            "Username cannot be blank."
        } else if (username.split(" ").count() != 1) {
            "Usernames do not contain any spaces."
        } else {
            ""
        }
    }

    // clear username when user indicates profile is wrong
    fun resetLogin() {
        username = ""
        canShowWebView = false
        webViewLoaded = false
    }

    // set the desired duolingo username
    fun saveUsername() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreManager.saveUsername(
                UserDetails(username = username)
            )
        }
    }
}