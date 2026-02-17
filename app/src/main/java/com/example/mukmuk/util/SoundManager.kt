package com.example.mukmuk.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.sin

class SoundManager {
    private var isReleased = false

    fun playTick() {
        if (isReleased) return
        Thread {
            try {
                val sampleRate = 22050
                val durationMs = 30
                val numSamples = sampleRate * durationMs / 1000
                val samples = ShortArray(numSamples)
                val frequency = 800.0

                for (i in 0 until numSamples) {
                    val angle = 2.0 * PI * frequency * i / sampleRate
                    val envelope = if (i < numSamples / 4) i.toFloat() / (numSamples / 4)
                                   else 1f - (i - numSamples / 4).toFloat() / (numSamples * 3 / 4)
                    samples[i] = (sin(angle) * Short.MAX_VALUE * envelope * 0.3).toInt().toShort()
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(sampleRate)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(samples.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(samples, 0, samples.size)
                audioTrack.play()
                Thread.sleep(durationMs.toLong() + 10)
                audioTrack.release()
            } catch (_: Exception) {}
        }.start()
    }

    fun playFanfare() {
        if (isReleased) return
        Thread {
            try {
                val sampleRate = 22050
                val frequencies = listOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
                val noteDuration = 120 // ms per note
                val totalSamples = sampleRate * noteDuration * frequencies.size / 1000
                val samples = ShortArray(totalSamples)

                var offset = 0
                for (freq in frequencies) {
                    val noteSamples = sampleRate * noteDuration / 1000
                    for (i in 0 until noteSamples) {
                        val angle = 2.0 * PI * freq * i / sampleRate
                        val envelope = if (i < noteSamples / 8) i.toFloat() / (noteSamples / 8)
                                       else 1f - (i.toFloat() / noteSamples) * 0.5f
                        samples[offset + i] = (sin(angle) * Short.MAX_VALUE * envelope * 0.25).toInt().toShort()
                    }
                    offset += noteSamples
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(sampleRate)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(samples.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(samples, 0, samples.size)
                audioTrack.play()
                Thread.sleep((noteDuration * frequencies.size).toLong() + 50)
                audioTrack.release()
            } catch (_: Exception) {}
        }.start()
    }

    fun release() {
        isReleased = true
    }
}
