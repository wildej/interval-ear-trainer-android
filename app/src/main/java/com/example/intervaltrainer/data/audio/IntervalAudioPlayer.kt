package com.example.intervaltrainer.data.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.intervaltrainer.domain.Note
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.sin

interface IntervalAudioPlayer {
    suspend fun playInterval(root: Note, top: Note)
}

class SineWaveIntervalAudioPlayer(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : IntervalAudioPlayer {
    private val sampleRate = 44_100
    private val toneDurationMs = 800
    private val arpeggioPauseMs = 150L

    override suspend fun playInterval(root: Note, top: Note) = withContext(dispatcher) {
        playChord(root.frequencyHz, top.frequencyHz)
        delay(60)
        playTone(root.frequencyHz)
        delay(arpeggioPauseMs)
        playTone(top.frequencyHz)
    }

    private fun playChord(freqA: Double, freqB: Double) {
        val pcm = generatePcm(toneDurationMs, freqA, freqB)
        playPcm(pcm)
    }

    private fun playTone(freq: Double) {
        val pcm = generatePcm(toneDurationMs, freq, null)
        playPcm(pcm)
    }

    private fun generatePcm(durationMs: Int, freqA: Double, freqB: Double?): ShortArray {
        val sampleCount = sampleRate * durationMs / 1000
        val rampSize = sampleRate / 100
        val result = ShortArray(sampleCount)
        for (i in 0 until sampleCount) {
            val t = i.toDouble() / sampleRate
            var value = sin(2.0 * PI * freqA * t)
            if (freqB != null) {
                value = (value + sin(2.0 * PI * freqB * t)) * 0.5
            }
            val env = when {
                i < rampSize -> i.toDouble() / rampSize
                i > sampleCount - rampSize -> (sampleCount - i).toDouble() / rampSize
                else -> 1.0
            }
            result[i] = (value * env * Short.MAX_VALUE * 0.25).toInt().toShort()
        }
        return result
    }

    private fun playPcm(pcm: ShortArray) {
        val minSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val track = AudioTrack(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build(),
            AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build(),
            maxOf(minSize, pcm.size * 2),
            AudioTrack.MODE_STATIC,
            AudioTrack.WRITE_NON_BLOCKING
        )
        track.write(pcm, 0, pcm.size, AudioTrack.WRITE_BLOCKING)
        track.play()
        while (track.playState == AudioTrack.PLAYSTATE_PLAYING &&
            track.playbackHeadPosition < pcm.size
        ) {
            Thread.sleep(10)
        }
        track.stop()
        track.release()
    }
}
