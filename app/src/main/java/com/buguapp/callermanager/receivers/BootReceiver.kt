package com.buguapp.callermanager.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class BootReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BOOT", "Boot completed! Starting service...")
            val serviceIntent = Intent(context, CallListenerService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}
