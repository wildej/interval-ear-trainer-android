package com.example.intervaltrainer.data.audio

import android.app.Application

object IntervalAudioPlayerProvider {
    fun create(application: Application): IntervalAudioPlayer {
        val timing = IntervalPlaybackTiming()
        val assetOk = runCatching {
            application.assets.open("audio/piano_ref.wav").use { it.read() }
        }.isSuccess
        return if (assetOk) {
            AssetSampleIntervalAudioPlayer(application, timing = timing)
        } else {
            SineWaveIntervalAudioPlayer(timing = timing)
        }
    }
}
