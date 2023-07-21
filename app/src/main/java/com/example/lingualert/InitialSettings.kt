package com.example.lingualert

import android.app.Application
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.lingualert.ui.theme.LingualertTheme

/*
 settings screen to be used on first launch and get permission from user to ring alarm
 */

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun InitialScreenFlow(viewModel: SettingsViewModel, modifier: Modifier) {
    AnimatedContent(
        targetState = viewModel.currentStep,
        transitionSpec = {
            slideInHorizontally { height -> height } + fadeIn() with
                slideOutHorizontally { height -> - height } + fadeOut()
        },
        modifier = Modifier.offset{IntOffset.Zero}
        ) {currentStep ->
        when (currentStep) {
            0 -> InitialScreen(viewModel = viewModel, modifier = modifier)
            1 -> RequestPermissionScreen(viewModel = viewModel, modifier = modifier)
            2 -> LoginScreen(viewModel = viewModel, modifier = modifier)
        }
    }
}

@Composable
fun InitialScreen(viewModel: SettingsViewModel, modifier: Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
    ) {
        Image(
            painterResource(id = R.drawable.onboarding),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Welcome to Lingualert!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "Start everyday one step closer to your language goals.",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(onClick = {
                viewModel.advanceScreen()
            }
            ) {
                Text("GET STARTED")
            }
        }
    }
}

@Composable
fun RequestPermissionScreen(viewModel: SettingsViewModel, modifier: Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(painterResource(id = R.drawable.onboarding_requestperms),
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Just a few things...",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "Enable notification permissions so we can notify you to do your lesson!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Button(onClick = {
                viewModel.advanceScreen()
            }
            ) {
                Text("REQUEST PERMISSION")
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: SettingsViewModel, modifier: Modifier) {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(painterResource(id = R.drawable.onboarding_login),
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Login",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            TextField(
                value = viewModel.username,
                onValueChange = { username ->
                    viewModel.username = username
                },
                label = { Text(text = "Duolingo Username") },
                placeholder = { Text(text = "Please enter your username.") }
            )
            Button(onClick = { /*TODO*/ }) {
                Text("LOGIN")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InitialSettingsPreview() {
    LingualertTheme {
        InitialScreen(viewModel = SettingsViewModel(Application()),modifier = Modifier)
    }
}

@Preview(showBackground = true)
@Composable
fun RequestPermissionScreenPreview() {
    LingualertTheme {
        RequestPermissionScreen(viewModel = SettingsViewModel(Application()),modifier = Modifier)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LingualertTheme {
        LoginScreen(viewModel = SettingsViewModel(Application()), modifier = Modifier)
    }
}