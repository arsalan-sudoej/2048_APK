package com.example.game2048.sound

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack

/**
 * Synthesizes short game sound effects at runtime with AudioTrack.
 * No binary assets needed - everything is generated from simple sine
 * envelopes, keeping the APK small and license-free.
 * Toggleable through settings.
 */
class SoundManager {

    @Volatile
    private var enabled: Boolean = true

    private val sampleRate = 22050

    private fun playTone(
        startFreq: Float,
        endFreq: Float,
        durationMs: Int,
        volume: Float,
        kind: Wave = Wave.SINE
    ) {
        if (!enabled) return
        val numSamples = (sampleRate * durationMs / 1000)
        val samples = ShortArray(numSamples)
        var phase = 0f
        for (i in 0 until numSamples) {
            val t = i.toFloat() / numSamples
            val freq = startFreq + (endFreq - startFreq) * t
            phase += 2f * Math.PI.toFloat() * freq / sampleRate
            // attack-decay envelope
            val envelope = when {
                t < 0.1f -> t / 0.1f
                else -> 1f - (t - 0.1f) / 0.9f
            }
            val value = when (kind) {
                Wave.SINE -> kotlin.math.sin(phase)
                Wave.TRIANGLE -> {
                    val p = (phase % (2f * Math.PI.toFloat())) / (2f * Math.PI.toFloat())
                    if (p < 0.5f) 4f * p - 1f else 3f - 4f * p
                }
            }
            samples[i] = (value * envelope * volume * Short.MAX_VALUE * 0.5f).toInt().toShort()
        }
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        val format = AudioFormat.Builder()
            .setSampleRate(sampleRate)
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        val track = AudioTrack(
            attributes,
            format,
            samples.size * 2,
            AudioTrack.MODE_STATIC,
            AudioTrack.PERFORMANCE_MODE_LOW_LATENCY
        )
        track.write(samples, 0, samples.size)
        track.play()
        // Let the track finish then release its resources.
        Thread {
            Thread.sleep(durationMs.toLong() + 50L)
            try {
                track.stop()
            } catch (_: IllegalStateException) {
            }
            track.release()
        }.start()
    }

    private enum class Wave { SINE, TRIANGLE }

    fun setEnabled(value: Boolean) {
        enabled = value
    }

    /** Soft tick for a valid move. */
    fun playMove() = playTone(520f, 440f, 70, 0.35f)

    /** Warm thock for a merge - higher pitch for bigger merges. */
    fun playMerge(power: Int = 1) {
        val base = 380f * (1f + 0.12f * (power - 1).coerceAtMost(6))
        playTone(base, base * 1.6f, 120, 0.5f, Wave.TRIANGLE)
    }

    /** Short chime for reaching 2048. */
    fun playWin() {
        playTone(660f, 990f, 180, 0.5f)
        Thread {
            Thread.sleep(160)
            playTone(880f, 1320f, 240, 0.5f)
        }.start()
    }

    /** Low soft chord for game over. */
    fun playGameOver() {
        playTone(330f, 165f, 400, 0.4f, Wave.TRIANGLE)
    }
}
