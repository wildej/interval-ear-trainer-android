package com.example.intervaltrainer.data.audio

import android.app.Application
import com.example.intervaltrainer.domain.Note
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.LazyThreadSafetyMode

/**
 * Plays intervals using a bundled mono 16-bit WAV in assets (see [assetPath]), pitch-shifted
 * by resampling against [referenceRootHz] (fundamental of the recorded clip).
 */
class AssetSampleIntervalAudioPlayer(
    private val application: Application,
    private val timing: IntervalPlaybackTiming = IntervalPlaybackTiming(),
    private val assetPath: String = "audio/piano_ref.wav",
    /** Hz of the fundamental in the reference WAV (default: C4). */
    private val referenceRootHz: Double = 261.6255653005986,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : IntervalAudioPlayer {

    private val master: Pair<Int, ShortArray> by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        application.assets.open(assetPath).use { stream ->
            WavMono16Reader.read(stream)
        }
    }

    override suspend fun playInterval(root: Note, top: Note) = withContext(dispatcher) {
        val (sampleRate, ref) = master
        val outLen = sampleRate * timing.toneDurationMs / 1000
        val a = SamplePitchResampler.pitchShiftToLength(ref, referenceRootHz, root.frequencyHz, outLen)
        val b = SamplePitchResampler.pitchShiftToLength(ref, referenceRootHz, top.frequencyHz, outLen)
        val chord = mix(a, b)
        playMono16Pcm(sampleRate, chord)
        delay(timing.chordToArpeggioPauseMs)
        val arpeggio = ArpeggioMixer.mergeSequential(a, b, sampleRate, timing.arpeggioGapMs)
        playMono16Pcm(sampleRate, arpeggio)
    }

    private fun mix(a: ShortArray, b: ShortArray): ShortArray {
        val n = minOf(a.size, b.size)
        val out = ShortArray(n)
        for (i in 0 until n) {
            val v = (a[i].toInt() + b[i].toInt()) / 2
            out[i] = v.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return out
    }
}
