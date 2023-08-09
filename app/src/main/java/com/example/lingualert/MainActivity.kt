package com.example.lingualert

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lingualert.ui.theme.LingualertTheme
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    private val viewModel: AlarmViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LingualertTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    InitialScreenFlow(viewModel = settingsViewModel, modifier = Modifier.padding(14.dp))
                    //MainScreen(viewModel = viewModel, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

// navigation controller to handle multiple screens
@Composable
fun Navigation(viewModel: AlarmViewModel, modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.MainActivity.route
    ) {
        composable(route = Screen.MainActivity.route) {
            MainScreen(viewModel = viewModel, modifier = modifier)
        }

        composable(route = Screen.InitialSettings.route) {

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: AlarmViewModel, modifier: Modifier = Modifier) {
    // USES BY rather than = because by allows alarms to update when state changes.
    val alarms by viewModel.allAlarms.observeAsState(emptyList()) // future ref observeAsState requires gradle import
    val timeDialogState = rememberMaterialDialogState()

    Scaffold(
        topBar = { TopAppBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { timeDialogState.show() },
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Filled.Add, null)
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
        ) {
            Crossfade(targetState = alarms) { alarmList ->
                if (alarmList.isEmpty()) {
                    NoAlarmsBox()
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(alarms) {alarm ->
                            AlarmBox(
                                hour = alarm.hour,
                                min = alarm.min,
                                deleteFunction = { viewModel.deleteAlarm(alarm) }
                            )
                        }
                    }
                }
            }

            MaterialDialog(
                dialogState = timeDialogState,
                buttons = {
                    positiveButton(text = "Add") {
                        // add to the db maybe
                    }
                    negativeButton(text = "Cancel")
                },
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                timepicker(
                    initialTime = LocalTime.now(),
                    title = "Set time",
                    colors = TimePickerDefaults.colors(
                        activeBackgroundColor = MaterialTheme.colorScheme.primary,
                        selectorColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    ) {
                    viewModel.addAlarm(Alarm(hour = it.hour, min = it.minute))
                }
            }
        }
    }
}

@Composable
fun TopAppBar() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        Image(
            painterResource(id = R.drawable.lingualert),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
        )
        Spacer(modifier = Modifier.size(12.dp))
        Text(
            text = "Lingualert",
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun AlarmBox(hour: Int, min: Int, deleteFunction: () -> Unit) {
    // converting time into 12h time
    var hour12: Int = 0
    var am_pm: String = "AM"
    if (hour == 0) {
        hour12 = 12
    } else if (hour == 12) {
        hour12 = 12
        am_pm = "PM"
    } else if (hour in 13..23) {
        hour12 = hour - 12
        am_pm = "PM"
    } else {
        hour12 = hour
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(16.dp)
    ) {
        Column() {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = painterResource(id = R.drawable.alarm),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = if (min.toString().length == 1) {
                        "$hour12:0$min $am_pm"
                    } else {
                        "$hour12:$min $am_pm"
                    },
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(2f)
                )
                Button(
                    onClick = { deleteFunction() },
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete alarm"
                    )
                }
            }
        }
    }
}

@Composable
fun NoAlarmsBox() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 56.dp, bottom = 48.dp)
        ) {
            Image(painter = painterResource(id = R.drawable.duck), contentDescription = null)
            Spacer(modifier = Modifier.size(40.dp))
            Text(
                text = "No alarms set...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun AlarmPreview() {
    LingualertTheme {
        AlarmBox(hour = 8, min = 12, deleteFunction = {})
    }
}


@Preview(showBackground = true)
@Composable
fun NoAlarmsBoxPreview() {
    LingualertTheme {
        NoAlarmsBox()
    }
}
