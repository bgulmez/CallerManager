package com.buguapp.callermanager.helper

import android.content.Context
import android.media.AudioManager
import android.util.Log

object SoundHelper {
    private var previousRingerMode: Int? = null

    fun setLoudRinger(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (previousRingerMode == null) {
            previousRingerMode = audioManager.ringerMode
            Log.d("SoundHelper", "Eski mod kaydedildi: $previousRingerMode")
        }

        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
        audioManager.setStreamVolume(
            AudioManager.STREAM_RING,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
            AudioManager.FLAG_PLAY_SOUND
        )
    }

    fun restoreRingerMode(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        previousRingerMode?.let {
            audioManager.ringerMode = it
            Log.d("SoundHelper", "Önceki moda dönüldü: $it")
            previousRingerMode = null
        }
    }
}
