package com.muxaeji.intervalo.data.audio

import kotlin.math.roundToInt

internal object SamplePitchResampler {
    /**
     * Resample a master clip recorded at [sourceRootHz] so it sounds at [targetHz],
     * producing exactly [outputSamples] output samples (linear interpolation).
     */
    fun pitchShiftToLength(
        samples: ShortArray,
        sourceRootHz: Double,
        targetHz: Double,
        outputSamples: Int
    ): ShortArray {
        if (samples.isEmpty() || outputSamples <= 0) return ShortArray(0)
        val out = ShortArray(outputSamples)
        val ratio = sourceRootHz / targetHz
        for (k in 0 until outputSamples) {
            val srcPos = k * ratio
            out[k] = lerpSample(samples, srcPos)
        }
        return out
    }

    private fun lerpSample(samples: ShortArray, index: Double): Short {
        if (index <= 0.0) return samples[0]
        val last = samples.lastIndex
        if (index >= last) return samples[last]
        val i0 = index.toInt()
        val frac = index - i0
        val s0 = samples[i0].toDouble()
        val s1 = samples[i0 + 1].toDouble()
        val v = s0 + frac * (s1 - s0)
        return v.roundToInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
    }
}
