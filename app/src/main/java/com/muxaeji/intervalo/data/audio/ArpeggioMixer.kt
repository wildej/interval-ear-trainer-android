package com.muxaeji.intervalo.data.audio

import kotlin.math.max
import kotlin.math.roundToInt

internal object ArpeggioMixer {
    /**
     * Places [second] so it starts [gapMs] after the end of [first] (positive gap = silence).
     * Negative [gapMs] pulls the second note earlier (overlap / legato).
     */
    fun mergeSequential(
        first: ShortArray,
        second: ShortArray,
        sampleRate: Int,
        gapMs: Long
    ): ShortArray {
        if (first.isEmpty()) return second.copyOf()
        if (second.isEmpty()) return first.copyOf()
        val gapSamples = (gapMs * sampleRate / 1000.0).roundToInt()
        val bStart = first.size + gapSamples
        val totalLen = max(first.size, bStart + second.size)
        val out = ShortArray(totalLen)
        for (i in first.indices) {
            out[i] = first[i]
        }
        for (j in second.indices) {
            val i = bStart + j
            if (i in out.indices) {
                out[i] = mixSamples(out[i].toInt(), second[j].toInt())
            }
        }
        return out
    }

    private fun mixSamples(a: Int, b: Int): Short {
        val s = a + b
        return s.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
    }
}
