package com.muxaeji.intervalo.data.audio

/**
 * Wall-clock timings for one interval playback cycle (chord then arpeggio).
 * Tweak here to iterate on feel without touching player logic.
 */
data class IntervalPlaybackTiming(
    val toneDurationMs: Int = 720,
    val chordToArpeggioPauseMs: Long = 360L,
    /**
     * Gap between the two arpeggio notes (after the chord).
     * Positive: silence between notes. Negative: overlap (second note starts before the first ends).
     */
    val arpeggioGapMs: Long = -110L
)
