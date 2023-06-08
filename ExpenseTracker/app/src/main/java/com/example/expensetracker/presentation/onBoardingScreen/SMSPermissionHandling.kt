package com.example.expensetracker.presentation.onBoardingScreen

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.example.expensetracker.Home
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SMSPermissionHandling(
    mainNavController: NavHostController
) {
    val permissionState = rememberPermissionState(permission = Manifest.permission.READ_SMS)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val sharedPreferences = LocalContext.current.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
    val permissionGrantedToastShown = sharedPreferences.getBoolean("permissionGrantedToastShown", false)
    val permissionDialogCanceled = sharedPreferences.getBoolean("permissionDialogCanceled", false)

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    if (!permissionState.hasPermission && !permissionState.shouldShowRationale && !permissionState.permissionRequested && !permissionDialogCanceled) {
                        permissionState.launchPermissionRequest()
                    }
                }
                else -> {
                    if(permissionDialogCanceled){
                        mainNavController.navigate(Home.route)
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })

    LaunchedEffect(permissionState.hasPermission, permissionState.shouldShowRationale) {
        if (permissionState.hasPermission) {
            if (!permissionGrantedToastShown) {
                Toast.makeText(context, "SMS permission has been granted", Toast.LENGTH_SHORT).show()
                sharedPreferences.edit().putBoolean("permissionGrantedToastShown", true).apply()
            }
            mainNavController.navigate(Home.route)
        }
    }

    LaunchedEffect(permissionState.permissionRequested) {
        if (permissionState.permissionRequested && !permissionState.hasPermission && !permissionState.shouldShowRationale) {
            coroutineScope.launch {
                mainNavController.navigate(Home.route)
            }
        }
    }

    when {
        permissionState.shouldShowRationale && !permissionDialogCanceled -> {
            // Show rationale in an AlertDialog
            AlertDialog(
                onDismissRequest = {
                    // Navigate to the main screen when dismissed
                    sharedPreferences.edit().putBoolean("permissionDialogCanceled", true).apply()
                    mainNavController.navigate(Home.route)
                },
                title = {
                    Text(text = "Permission Required")
                },
                text = {
                    Text(
                        text = "Granting SMS permission is optional but enables automated expense tracking."
                    )
                },
                confirmButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                // Request the permission again
                                coroutineScope.launch {
                                    permissionState.launchPermissionRequest()
                                }
                            },
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(text = "Request Permission")
                        }
                        Button(
                            onClick = {
                                // Navigate to the main screen when dismissed
                                sharedPreferences.edit().putBoolean("permissionDialogCanceled", true).apply()
                                mainNavController.navigate(Home.route)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text(text = "Cancel")
                        }
                    }
                },
                dismissButton = null
            )
        }
    }
}





