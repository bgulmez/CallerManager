package com.buguapp.callermanager


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.buguapp.callermanager.receivers.CallListenerService
import com.buguapp.callermanager.ui.theme.CallerManagerTheme
import com.buguapp.callermanager.view.PhoneSelectorUI

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val granted = permissions[Manifest.permission.READ_PHONE_STATE] == true &&
                        permissions[Manifest.permission.READ_CALL_LOG] == true &&
                        permissions[Manifest.permission.READ_CONTACTS] == true

                if (granted) {
                    startCallListenerService()
                }
            }

        val allGranted = listOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS
        ).all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!allGranted) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS
                )
            )
        } else {
            startCallListenerService()
        }

        setContent {
            CallerManagerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PhoneSelectorUI()
                }
            }
        }
    }

    private fun startCallListenerService() {
        val serviceIntent = Intent(this, CallListenerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CallerManagerTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            PhoneSelectorUI()
        }
    }
}
