package com.example.intervaltrainer.data.audio

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Minimal RIFF WAVE reader: PCM 16-bit little-endian, mono or stereo (stereo is downmixed to mono).
 */
internal object WavMono16Reader {
    fun read(input: InputStream): Pair<Int, ShortArray> {
        val bytes = input.readBytes()
        require(bytes.size >= 44) { "WAV too small" }
        require(bytes.decodeToString(0, 4) == "RIFF") { "Not a RIFF file" }
        require(bytes.decodeToString(8, 12) == "WAVE") { "Not a WAVE file" }

        var sampleRate = 44100
        var bitsPerSample = 16
        var numChannels = 1
        var dataOffset = -1
        var dataSize = 0

        var pos = 12
        while (pos + 8 <= bytes.size) {
            val chunkId = bytes.decodeToString(pos, pos + 4)
            val chunkSize = ByteBuffer.wrap(bytes, pos + 4, 4).order(ByteOrder.LITTLE_ENDIAN).int
            val chunkDataStart = pos + 8
            when (chunkId) {
                "fmt " -> {
                    val sub = ByteBuffer.wrap(bytes, chunkDataStart, chunkSize).order(ByteOrder.LITTLE_ENDIAN)
                    val audioFormat = sub.short.toInt() and 0xFFFF
                    require(audioFormat == 1) { "Only PCM WAV supported, got format $audioFormat" }
                    numChannels = sub.short.toInt() and 0xFFFF
                    sampleRate = sub.int
                    sub.int // byte rate
                    sub.short // block align
                    bitsPerSample = sub.short.toInt() and 0xFFFF
                    require(bitsPerSample == 16) { "Only 16-bit WAV supported" }
                }
                "data" -> {
                    dataOffset = chunkDataStart
                    dataSize = chunkSize
                }
            }
            pos += 8 + chunkSize + (chunkSize and 1) // word align
        }
        require(dataOffset >= 0 && dataSize > 0) { "No data chunk in WAV" }

        val frameBytes = (bitsPerSample / 8) * numChannels
        val sampleCount = dataSize / frameBytes
        val shorts = ShortArray(sampleCount)
        var o = 0
        var p = dataOffset
        while (o < sampleCount && p + frameBytes <= bytes.size) {
            if (numChannels == 1) {
                shorts[o] = ByteBuffer.wrap(bytes, p, 2).order(ByteOrder.LITTLE_ENDIAN).short
                p += 2
            } else {
                var acc = 0
                repeat(numChannels) {
                    acc += ByteBuffer.wrap(bytes, p, 2).order(ByteOrder.LITTLE_ENDIAN).short.toInt()
                    p += 2
                }
                shorts[o] = (acc / numChannels).toShort()
            }
            o++
        }
        return sampleRate to shorts.copyOf(o)
    }
}
