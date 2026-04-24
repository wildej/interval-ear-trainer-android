package com.example.intervaltrainer.data.audio

import android.app.Application
import com.example.intervaltrainer.domain.Note
import dev.kotlinds.fluidsynthkmp.AudioConfig
import dev.kotlinds.fluidsynthkmp.FluidSynthPlayer
import dev.kotlinds.fluidsynthkmp.Interpolation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.min

/**
 * Renders intervals via FluidSynth + SF2 (Chorium by default). Copies the SoundFont from assets
 * to app files dir on first use (FluidSynth loads by file path).
 *
 * Note: [FluidSynthPlayer] starts a platform audio driver on construction; we still drive output
 * through [playMono16Pcm] using [FluidSynthPlayer.renderFloat] for deterministic playback timing.
 */
class SoundFontIntervalAudioPlayer(
    private val application: Application,
    private val timing: IntervalPlaybackTiming = IntervalPlaybackTiming(),
    private val assetPath: String = "soundfonts/Chorium.SF2",
    private val channel: Int = 0,
    private val presetProgram: Int = 0
) : IntervalAudioPlayer {

    private val mutex = Mutex()
    private var engine: FluidSynthPlayer? = null

    override suspend fun playInterval(root: Note, top: Note) {
        withContext(Dispatchers.Default) {
            mutex.withLock {
                val player = ensureEngineLocked()
                val sr = 44100
                val toneFrames = sr * timing.toneDurationMs / 1000

                allNotesOff(player)
                player.programChange(channel, presetProgram)

                player.noteOn(channel, root.midi, 96)
                player.noteOn(channel, top.midi, 96)
                val chord = renderMonoPcm(player, toneFrames)
                player.noteOff(channel, root.midi)
                player.noteOff(channel, top.midi)
                playMono16Pcm(sr, chord)

                delay(timing.chordToArpeggioPauseMs)

                val a = renderNote(player, root.midi, toneFrames)
                val b = renderNote(player, top.midi, toneFrames)
                val arpeggio = ArpeggioMixer.mergeSequential(a, b, sr, timing.arpeggioGapMs)
                playMono16Pcm(sr, arpeggio)
            }
        }
    }

    private fun ensureEngineLocked(): FluidSynthPlayer {
        if (engine != null) return engine!!
        val path = ensureSf2OnDisk()
        val p = FluidSynthPlayer(
            AudioConfig(
                sampleRate = 44100,
                interpolation = Interpolation.HIGH,
                periodSize = 256,
                periods = 2
            )
        )
        val id = p.loadSoundFont(path)
        require(id >= 0) { "loadSoundFont failed for $path" }
        p.programChange(channel, presetProgram)
        p.setGain(0.42f)
        p.setReverb(0.0, 0.0, 0.0, 0.0)
        p.setChorus(0, 0.0, 0.5, 0.0)
        engine = p
        return p
    }

    private fun ensureSf2OnDisk(): String {
        val out = File(application.filesDir, "Chorium.SF2")
        if (!out.exists() || out.length() == 0L) {
            application.assets.open(assetPath).use { input ->
                out.outputStream().use { output -> input.copyTo(output) }
            }
        }
        return out.absolutePath
    }

    private fun allNotesOff(player: FluidSynthPlayer) {
        for (k in 0..127) {
            player.noteOff(channel, k)
        }
    }

    private fun renderNote(player: FluidSynthPlayer, midi: Int, totalFrames: Int): ShortArray {
        allNotesOff(player)
        player.noteOn(channel, midi, 96)
        val pcm = renderMonoPcm(player, totalFrames)
        player.noteOff(channel, midi)
        return pcm
    }

    private fun renderMonoPcm(player: FluidSynthPlayer, totalFrames: Int): ShortArray {
        val out = ShortArray(totalFrames)
        var done = 0
        val chunk = 256
        while (done < totalFrames) {
            val n = min(chunk, totalFrames - done)
            val floats = player.renderFloat(n)
            require(floats.size == n * 2) { "Expected stereo interleaved buffer" }
            for (i in 0 until n) {
                val l = floats[2 * i]
                val r = floats[2 * i + 1]
                val s = ((l + r) * 0.5f * 32767f).toInt().coerceIn(
                    Short.MIN_VALUE.toInt(),
                    Short.MAX_VALUE.toInt()
                )
                out[done + i] = s.toShort()
            }
            done += n
        }
        return out
    }
}
