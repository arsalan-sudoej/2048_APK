package com.example.game2048.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.game2048.R

/**
 * Plays short game sound effects via SoundPool.
 * Toggleable through settings; loads lazily on first play.
 */
class SoundManager(context: Context) {

    private val appContext = context.applicationContext

    @Volatile
    private var enabled: Boolean = true

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private var moveSound: Int = 0
    private var mergeSound: Int = 0
    private var winSound: Int = 0
    private var gameOverSound: Int = 0
    private var loaded = false

    private fun ensureLoaded() {
        if (loaded) return
        moveSound = soundPool.load(appContext, R.raw.move, 1)
        mergeSound = soundPool.load(appContext, R.raw.merge, 1)
        winSound = soundPool.load(appContext, R.raw.win, 1)
        gameOverSound = soundPool.load(appContext, R.raw.gameover, 1)
        loaded = true
    }

    fun setEnabled(value: Boolean) {
        enabled = value
    }

    fun playMove() = play(moveSound)
    fun playMerge() = play(mergeSound)
    fun playWin() = play(winSound)
    fun playGameOver() = play(gameOverSound)

    private fun play(soundId: Int) {
        if (!enabled) return
        ensureLoaded()
        if (soundId > 0) {
            soundPool.play(soundId, 0.8f, 0.8f, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool.release()
        loaded = false
    }
}
