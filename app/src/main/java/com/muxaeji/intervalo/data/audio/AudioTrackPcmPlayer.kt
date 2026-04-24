package com.muxaeji.intervalo.data.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack

internal fun playMono16Pcm(sampleRate: Int, pcm: ShortArray) {
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
