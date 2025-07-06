package com.buguapp.callermanager.receivers

import android.annotation.SuppressLint
import android.telephony.PhoneStateListener
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat
import com.buguapp.callermanager.R

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
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Arama Takibi",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Arama Dinleyici")
            .setContentText("Gelen aramalar takip ediliyor")
            .setSmallIcon(R.drawable.ic_notification)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+ (API 34+)
            startForeground(
                1,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL
            )
        } else {
            startForeground(1, notification)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
