package com.example.intervaltrainer.data.audio

/**
 * Wall-clock timings for one interval playback cycle (chord then arpeggio).
 * Tweak here to iterate on feel without touching player logic.
 */
data class IntervalPlaybackTiming(
    val toneDurationMs: Int = 750,
    val chordToArpeggioPauseMs: Long = 420L,
    val arpeggioPauseMs: Long = 180L
)
