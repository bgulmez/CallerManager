package com.buguapp.callermanager.receivers

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.buguapp.callermanager.helper.CallHelper
import com.buguapp.callermanager.helper.SoundHelper

class CallReceiver(private val context: Context) : PhoneStateListener() {

    override fun onCallStateChanged(state: Int, incomingNumber: String?) {
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                val watchedNumbers = CallHelper.getSavedNumbers(context)
                var incomingNumberValue : String = incomingNumber ?: ""
                if((incomingNumber?.length ?: 0)>10) {
                    incomingNumberValue = incomingNumber?.takeLast(10) ?: ""
                }
                // Check if the incoming number is not null and is contained in any of the watched numbers
                if (watchedNumbers.any { watchedNumber ->
                        watchedNumber.let { it.contains(incomingNumberValue) }
                    }) {
                    SoundHelper.setLoudRinger(context)
                    /** if you want to play custom sound, uncomment the following line and updated to your sound file */
//                    SoundPlayer.playSound(context)
                }

            }

            TelephonyManager.CALL_STATE_OFFHOOK,
            TelephonyManager.CALL_STATE_IDLE -> {
                SoundHelper.restoreRingerMode(context)
                /** if you want to play custom sound, uncomment the following line and updated to your sound file */
//                SoundPlayer.stopSound()
            }
        }
    }
}
