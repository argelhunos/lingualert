package com.example.lingualert

/*
 * class to define route between mainactiviy and initial settings
 */

sealed class Screen (val route: String) {
    object MainActivity: Screen("main_screen")
    object InitialSettings: Screen("initial_settings")
}
