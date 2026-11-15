package com.example.game2048.core

import android.content.Context
import android.content.SharedPreferences

/**
 * Persists best score, stats, settings and autosave using SharedPreferences.
 */
class ScoreStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("game2048_prefs", Context.MODE_PRIVATE)

    // -- Best score --
    fun loadBestScore(): Int = prefs.getInt(KEY_BEST, 0)

    fun saveBestScore(score: Int) {
        prefs.edit().putInt(KEY_BEST, score).apply()
    }

    // -- Autosave --
    fun saveGame(state: GameState) {
        prefs.edit().putString(KEY_AUTOSAVE, GameState.toJson(state)).apply()
    }

    fun loadGame(): GameState? = GameState.fromJson(prefs.getString(KEY_AUTOSAVE, null))

    fun clearGame() {
        prefs.edit().remove(KEY_AUTOSAVE).apply()
    }

    // -- Stats --
    fun loadGamesPlayed(): Int = prefs.getInt(KEY_GAMES_PLAYED, 0)

    fun incrementGamesPlayed() {
        prefs.edit().putInt(KEY_GAMES_PLAYED, loadGamesPlayed() + 1).apply()
    }

    fun loadHighestTile(): Int = prefs.getInt(KEY_HIGHEST_TILE, 0)

    fun saveHighestTileIfBetter(tile: Int) {
        if (tile > loadHighestTile()) {
            prefs.edit().putInt(KEY_HIGHEST_TILE, tile).apply()
        }
    }

    fun loadTotalMoves(): Int = prefs.getInt(KEY_TOTAL_MOVES, 0)

    fun addTotalMoves(moves: Int) {
        prefs.edit().putInt(KEY_TOTAL_MOVES, loadTotalMoves() + moves).apply()
    }

    fun loadTotalMerges(): Int = prefs.getInt(KEY_TOTAL_MERGES, 0)

    fun addTotalMerges(merges: Int) {
        prefs.edit().putInt(KEY_TOTAL_MERGES, loadTotalMerges() + merges).apply()
    }

    // -- Settings --
    fun isSoundEnabled(): Boolean = prefs.getBoolean(KEY_SOUND, true)

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply()
    }

    fun isDarkMode(): Boolean = prefs.getBoolean(KEY_DARK_MODE, false)

    fun setDarkMode(dark: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, dark).apply()
    }

    // -- Coins --
    fun loadCoins(): Int = prefs.getInt(KEY_COINS, 0)

    fun addCoins(amount: Int) {
        prefs.edit().putInt(KEY_COINS, loadCoins() + amount).apply()
    }

    fun spendCoins(amount: Int): Boolean {
        val current = loadCoins()
        if (current < amount) return false
        prefs.edit().putInt(KEY_COINS, current - amount).apply()
        return true
    }

    companion object {
        private const val KEY_BEST = "best_score"
        private const val KEY_AUTOSAVE = "autosave_state"
        private const val KEY_GAMES_PLAYED = "games_played"
        private const val KEY_HIGHEST_TILE = "highest_tile"
        private const val KEY_TOTAL_MOVES = "total_moves"
        private const val KEY_TOTAL_MERGES = "total_merges"
        private const val KEY_SOUND = "sound_enabled"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_COINS = "coins"
    }
}
