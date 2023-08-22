package com.example.lingualert

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.lingualert.ui.theme.LingualertTheme

/*
 settings screen to be used on first launch and get permission from user to ring alarm
 */

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun InitialScreenFlow(viewModel: SettingsViewModel, activity: Activity, modifier: Modifier) {
    /*
        Animated initial screen:
            IntialScreen - introduce user to app
            RequestPermissionScreen - get permission for alarm and explain rationale behind permission request
            LoginScreen - get and save username from user
            Complete Screen - transition from initial screen to main screen
     */
    AnimatedContent(
        targetState = viewModel.currentStep,
        transitionSpec = {
            slideInHorizontally { height -> height } + fadeIn() with
                slideOutHorizontally { height -> - height } + fadeOut()
        },
        modifier = modifier.offset{IntOffset.Zero}
        ) {currentStep ->
        when (currentStep) {
            0 -> InitialScreen(viewModel = viewModel, modifier = modifier)
            1 -> RequestPermissionScreen(viewModel = viewModel, activity = activity, modifier = modifier)
            2 -> LoginScreen(viewModel = viewModel, modifier = modifier)
            3 -> CompleteScreen(viewModel = viewModel, modifier = modifier)
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

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun RequestPermissionScreen(viewModel: SettingsViewModel, activity: Activity, modifier: Modifier) {
    // permission launcher needed to get permission from user

    val context = LocalContext.current
    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else mutableStateOf(true)
    }

    var hasDrawOverAppsPermission by remember {
        mutableStateOf(Settings.canDrawOverlays(context))
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
        }
    )

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
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                if (!hasNotificationPermission && viewModel.permissionRequested == true) {
                    viewModel.toggleDialog()
                }
                viewModel.togglePermissionRequest()
            }
            ) {
                Text("REQUEST PERMISSION")
            }
            Button(onClick = {
                // must request draw over apps permission through settings
                if (!hasDrawOverAppsPermission) {
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:com.example.lingualert"))
                    activity.startActivityForResult(intent, 0)
                }

                if (!hasDrawOverAppsPermission && viewModel.permissionRequested == true) {
                    viewModel.toggleDialog()
                }
                viewModel.togglePermissionRequest()
            }
            ) {
                Text("REQUEST PERMISSION")
            }

            Button(
                onClick = {
                    if (hasNotificationPermission && hasDrawOverAppsPermission) {
                        viewModel.advanceScreen()
                    } else {
                        Toast.makeText(context, "Please enable notification permissions.", Toast.LENGTH_LONG).show()
                    }
                }
            ) {
                Text("NEXT")
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        // change to snackbar
        Crossfade(targetState = (hasNotificationPermission && hasDrawOverAppsPermission)) { hasPermission ->
            if (hasPermission) {
                SnackBar()
            }
        }
    }

    if (viewModel.showDialog && !hasNotificationPermission) {
        WarningDialog(
            viewModel = viewModel, onAllowRequest = {notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
        )
    }


}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(viewModel: SettingsViewModel, modifier: Modifier) {
    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(painterResource(id = R.drawable.onboarding_login),
                contentDescription = null
            )
            Text(
                "Login",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            AnimatedContent(
                targetState = !viewModel.canShowWebView,
                transitionSpec = {
                    fadeIn(animationSpec = tween(150, 150)) with
                            fadeOut(animationSpec = tween(150)) using
                            SizeTransform { initialSize, targetSize ->
                                if (targetState) {
                                    keyframes {
                                        IntSize(initialSize.width, targetSize.height) at 150
                                        durationMillis = 300
                                    }
                                } else {
                                    keyframes {
                                        IntSize(initialSize.width, initialSize.height)
                                    }
                                }
                            }
                }
            ) {targetExpanded ->
                if (targetExpanded) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextField(
                            value = viewModel.username,
                            onValueChange = { username ->
                                viewModel.username = username
                            },
                            singleLine = true,
                            isError = viewModel.textFieldError.isNotBlank(),
                            label = { Text(text = "Duolingo Username") },
                            placeholder = { Text(text = "Please enter your username.") },
                            supportingText = { Text(viewModel.textFieldError) },
                            trailingIcon = {
                                if (viewModel.textFieldError.isNotBlank()) {
                                    Icon(
                                        painterResource(id = R.drawable.error),
                                        null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        )
                        Button(onClick = {
                            // check for errors in username, launch profile webview, dismiss keyboard if input is valid
                            viewModel.checkTextView()
                            viewModel.tryWebView()
                            if (viewModel.textFieldError.isBlank()) {
                                focusManager.clearFocus()
                            }
                        }) {
                            Text("LOGIN")
                        }
                    }
                }
            }

            AnimatedContent(
                targetState = viewModel.canShowWebView,
                transitionSpec = {
                    fadeIn(animationSpec = tween(150, 150)) with
                            fadeOut(animationSpec = tween(150)) using
                            SizeTransform { initialSize, targetSize ->
                                if (targetState) {
                                    keyframes {
                                        IntSize(initialSize.width, targetSize.height) at 150
                                        durationMillis = 300
                                    }
                                } else {
                                    keyframes {
                                        IntSize(initialSize.width, initialSize.height)
                                    }
                                }
                            }
                }
            ) {targetExpanded ->
                if (targetExpanded) {
                    DuolingoProfile(viewModel = viewModel, modifier = modifier.height(400.dp))
                }
                if (targetExpanded && !viewModel.webViewLoaded) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator(
                            modifier
                                .size(40.dp)
                        )
                    }
                }
            }
            Crossfade(targetState = viewModel.webViewLoaded) { webViewReady ->
                if (webViewReady) {
                    ConfirmUsernameBox(
                        onConfirm = {
                            viewModel.saveUsername()
                            viewModel.advanceScreen()
                        },
                        onDeny = {
                            viewModel.resetLogin()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CompleteScreen(viewModel: SettingsViewModel, modifier: Modifier) {
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
                "You're all set!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Button(onClick = {
                viewModel.navigateToMainScreen()
            }
            ) {
                Text("LETS GO!")
            }
        }
    }
}


@Composable
fun WarningDialog(viewModel: SettingsViewModel, onAllowRequest: () -> Unit) {
    Dialog(
        onDismissRequest = { viewModel.toggleDialog() }
    ) {
        Box(
            Modifier
                .clip(MaterialTheme.shapes.medium)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.errorContainer)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.duck), contentDescription = null
                )
                Text(
                    text = "We need notification permissions!",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Without your permission, the alarm won't be able to function properly. Please head to settings to enable the permission.",
                    style = MaterialTheme.typography.bodySmall
                )
                Row {
                    Button(
                        onClick = { viewModel.toggleDialog() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onErrorContainer)
                    ) {
                        Text("DECLINE")
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Button(
                        onClick = {
                            onAllowRequest()
                            viewModel.toggleDialog()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onErrorContainer)
                    ) {
                        Text("ALLOW")
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmUsernameBox(onConfirm: () -> Unit, onDeny: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                "Is this profile correct?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(3f)
            )
            Row(modifier = Modifier.weight(1f)) {
                IconButton(
                    onClick = {
                        onConfirm()
                    }) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Yes",
                    )
                }
                IconButton(
                    onClick = {
                        onDeny()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "No",
                    )
                }
            }
        }
    }
}

@Composable
fun SnackBar() {
    Box {
        Row(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "You're all set! Time to move on.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun DuolingoProfile(viewModel: SettingsViewModel, modifier: Modifier) {
    var webView: WebView? = null

    AndroidView(
        modifier = modifier
            .alpha(0.99f)
            .clip(MaterialTheme.shapes.medium),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                loadUrl("https://www.duolingo.com/profile/${viewModel.username}")
                webView = this
                webView!!.webViewClient = object: WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        //viewModel.webViewLoading = true
                        Log.d("TAG", "webview loading")
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        viewModel.webViewLoaded = true
                        Log.d("TAG", "webview loaded")
                    }
                }
            }
        }, update = {
            webView = it
        })
}

@Preview(showBackground = true)
@Composable
fun InitialSettingsPreview() {
    LingualertTheme {
        InitialScreen(viewModel = SettingsViewModel(Application()),modifier = Modifier)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true)
@Composable
fun RequestPermissionScreenPreview() {
    LingualertTheme {
        RequestPermissionScreen(viewModel = SettingsViewModel(Application()), activity = Activity(), modifier = Modifier)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LingualertTheme {
        LoginScreen(viewModel = SettingsViewModel(Application()), modifier = Modifier)
    }
}

@Preview(showBackground = true)
@Composable
fun WarningDialogPreview() {
    LingualertTheme {
        WarningDialog(viewModel = SettingsViewModel(Application()), onAllowRequest = {})
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmUsernameBoxPreview() {
    LingualertTheme {
        ConfirmUsernameBox(onConfirm = {}, onDeny = {})
    }
}

@Preview(showBackground = true)
@Composable
fun SnackBarPreview() {
    LingualertTheme {
        SnackBar()
    }
}