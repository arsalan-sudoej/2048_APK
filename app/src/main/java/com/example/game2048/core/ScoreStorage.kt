package com.example.game2048.core

import android.content.Context
import android.content.SharedPreferences

/**
 * Persists best score using SharedPreferences.
 */
class ScoreStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("game2048_prefs", Context.MODE_PRIVATE)

    fun loadBestScore(): Int = prefs.getInt(KEY_BEST, 0)

    fun saveBestScore(score: Int) {
        prefs.edit().putInt(KEY_BEST, score).apply()
    }

    companion object {
        private const val KEY_BEST = "best_score"
    }
}
