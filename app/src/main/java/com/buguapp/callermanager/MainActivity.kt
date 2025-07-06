package com.buguapp.callermanager


import android.Manifest
import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
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

    private lateinit var dialerRoleLauncher: ActivityResultLauncher<Intent>


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

        dialerRoleLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Kullanıcı uygulamayı dialer olarak atadı ✅
            } else {
                // Reddetti veya iptal etti ❌
            }
        }


        requestDialerRoleIfNeeded()

        setContent {
            CallerManagerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PhoneSelectorUI()
                }
            }
        }
    }

    private fun requestDialerRoleIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
            if (roleManager.isRoleAvailable(RoleManager.ROLE_DIALER) &&
                !roleManager.isRoleHeld(RoleManager.ROLE_DIALER)
            ) {
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
                dialerRoleLauncher.launch(intent)
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
