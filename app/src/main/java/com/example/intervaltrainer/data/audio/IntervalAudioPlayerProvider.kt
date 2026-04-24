package com.example.intervaltrainer.data.audio

import android.app.Application

object IntervalAudioPlayerProvider {
    fun create(application: Application): IntervalAudioPlayer {
        val timing = IntervalPlaybackTiming()
        val teddyDir = "audio/piano_teddy"
        val hasTeddySamples = runCatching {
            application.assets.list(teddyDir)?.any { name ->
                name.endsWith(".wav", ignoreCase = true) && midiFromAssetFileName(name) != null
            } == true
        }.getOrDefault(false)
        if (hasTeddySamples) {
            return MultiSampleIntervalAudioPlayer(application, timing = timing, assetDir = teddyDir)
        }
        val hasRef = runCatching {
            application.assets.open("audio/piano_ref.wav").use { it.read() }
        }.isSuccess
        return if (hasRef) {
            AssetSampleIntervalAudioPlayer(application, timing = timing)
        } else {
            SineWaveIntervalAudioPlayer(timing = timing)
        }
    }
}
