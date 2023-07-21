package com.example.lingualert.data

/*
 * Data class representing the current state of the initial settings
 */

data class InitialSettingsUiState(
    // current step user is in initial settings
    val currentStep: Int = 0,
    // user inputted duolingo username
    val username: String = ""
)