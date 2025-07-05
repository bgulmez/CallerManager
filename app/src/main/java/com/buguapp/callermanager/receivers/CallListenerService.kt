package com.buguapp.callermanager.receivers

import android.annotation.SuppressLint
import android.telephony.PhoneStateListener
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat

class CallListenerService : Service() {

    override fun onCreate() {
        super.onCreate()
        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val callReceiver = CallReceiver(this)
        telephonyManager.listen(callReceiver, PhoneStateListener.LISTEN_CALL_STATE)
        startForegroundServiceNotification()
    }

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundServiceNotification() {
        val channelId = "call_listener_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Call Listener", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Aramalar dinleniyor")
            .setContentText("Belirli numaralar takipte")
            .setSmallIcon(android.R.drawable.sym_call_incoming)
            .build()

        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
