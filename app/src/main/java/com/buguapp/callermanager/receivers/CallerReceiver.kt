package com.buguapp.callermanager.receivers

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.buguapp.callermanager.helper.SoundHelper
import com.buguapp.callermanager.helper.SoundPlayer
import com.buguapp.callermanager.helper.CallHelper

class CallReceiver(private val context: Context) : PhoneStateListener() {

    override fun onCallStateChanged(state: Int, incomingNumber: String?) {
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                val watchedNumber = CallHelper.getSavedNumber(context)
                if (incomingNumber?.contains(watchedNumber ?: "") == true) {
                    SoundHelper.setLoudRinger(context)
                    SoundPlayer.playSound(context)
                }

            }

            TelephonyManager.CALL_STATE_OFFHOOK,
            TelephonyManager.CALL_STATE_IDLE -> {
                SoundHelper.restoreRingerMode(context)
                SoundPlayer.stopSound()
            }
        }
    }
}
