package com.buguapp.callermanager.helper

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import com.buguapp.callermanager.R

object SoundPlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun playSound(context: Context) {
        stopSound()

        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0)

        mediaPlayer = MediaPlayer.create(context, R.raw.huawei)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    fun stopSound() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
