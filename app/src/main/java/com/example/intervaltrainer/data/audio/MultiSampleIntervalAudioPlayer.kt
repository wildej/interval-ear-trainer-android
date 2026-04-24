package com.example.intervaltrainer.data.audio

import android.app.Application
import com.example.intervaltrainer.domain.Note
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.LazyThreadSafetyMode
import kotlin.math.abs

/**
 * One-shot piano samples from assets/[assetDir] (WAV, 16-bit). Each file is mapped to the nearest
 * MIDI note from its filename; playback uses a small pitch correction from that sample to the target pitch.
 *
 * Pack layout: Freesound "piano notes" by Teddy_Frost (CC0) — see `LICENSE_README.txt` in asset folder.
 */
class MultiSampleIntervalAudioPlayer(
    private val application: Application,
    private val timing: IntervalPlaybackTiming = IntervalPlaybackTiming(),
    private val assetDir: String = "audio/piano_teddy",
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : IntervalAudioPlayer {

    private data class Bank(
        val sampleRate: Int,
        val layers: List<Layer>
    )

    private data class Layer(val rootMidi: Int, val pcm: ShortArray)

    private val bank: Bank by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { loadBank() }

    override suspend fun playInterval(root: Note, top: Note) = withContext(dispatcher) {
        val sr = bank.sampleRate
        val outLen = sr * timing.toneDurationMs / 1000
        val a = toneForTargetMidi(root.midi, outLen)
        val b = toneForTargetMidi(top.midi, outLen)
        val chord = mixHalves(a, b)
        playMono16Pcm(sr, chord)
        delay(timing.chordToArpeggioPauseMs)
        val arpeggio = ArpeggioMixer.mergeSequential(a, b, sr, timing.arpeggioGapMs)
        playMono16Pcm(sr, arpeggio)
    }

    private fun toneForTargetMidi(targetMidi: Int, outLen: Int): ShortArray {
        val layer = bank.layers.minBy { abs(it.rootMidi - targetMidi) }
        val srcHz = Note(layer.rootMidi).frequencyHz
        val dstHz = Note(targetMidi).frequencyHz
        return SamplePitchResampler.pitchShiftToLength(layer.pcm, srcHz, dstHz, outLen)
    }

    private fun loadBank(): Bank {
        val names = application.assets.list(assetDir) ?: emptyArray()
        val layers = mutableListOf<Layer>()
        var srCommon: Int? = null
        for (name in names) {
            if (!name.endsWith(".wav", ignoreCase = true)) continue
            val midi = midiFromAssetFileName(name) ?: continue
            val (sr, pcm) = application.assets.open("$assetDir/$name").use { WavMono16Reader.read(it) }
            if (srCommon == null) srCommon = sr
            require(sr == srCommon) { "All piano WAVs must share sample rate; $name is $sr vs $srCommon" }
            layers.add(Layer(midi, pcm))
        }
        require(layers.isNotEmpty()) { "No WAV samples found in assets/$assetDir" }
        return Bank(srCommon!!, layers.sortedBy { it.rootMidi })
    }

    private fun mixHalves(a: ShortArray, b: ShortArray): ShortArray {
        val n = minOf(a.size, b.size)
        val out = ShortArray(n)
        for (i in 0 until n) {
            val v = (a[i].toInt() + b[i].toInt()) / 2
            out[i] = v.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return out
    }
}

/** Parse pitch from Freesound-style filenames, e.g. `...__c4.wav`, `...__piano-normal-d4.wav`. */
internal fun midiFromAssetFileName(fileName: String): Int? {
    val lower = fileName.lowercase(Locale.ROOT)
    val re = Regex("([a-g])(#|b)?(\\d+)", RegexOption.IGNORE_CASE)
    val m = re.findAll(lower).lastOrNull() ?: return null
    val letter = m.groupValues[1].first()
    val acc = m.groupValues[2].ifEmpty { null }?.first()
    val octave = m.groupValues[3].toIntOrNull() ?: return null
    return letterAccOctaveToMidi(letter, acc, octave)
}

private fun letterAccOctaveToMidi(letter: Char, acc: Char?, octave: Int): Int {
    val basePc = when (letter.lowercaseChar()) {
        'c' -> 0
        'd' -> 2
        'e' -> 4
        'f' -> 5
        'g' -> 7
        'a' -> 9
        'b' -> 11
        else -> return 60
    }
    val accOffset: Int = when (acc) {
        '#' -> 1
        'b' -> -1
        else -> 0
    }
    val rawPc = basePc + accOffset
    val pc = ((rawPc % 12) + 12) % 12
    return 12 * (octave + 1) + pc
}
