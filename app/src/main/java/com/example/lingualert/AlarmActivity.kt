package com.example.lingualert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.lingualert.ui.theme.LingualertTheme

class AlarmActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LingualertTheme {

            }
        }
    }
}

@Composable
fun AlarmScreen() {

}