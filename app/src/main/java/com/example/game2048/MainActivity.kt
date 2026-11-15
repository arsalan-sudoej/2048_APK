package com.example.game2048

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.game2048.core.ScoreStorage
import com.example.game2048.ui.Game2048App
import com.example.game2048.ui.theme.Game2048Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val storage = ScoreStorage(this)
        val savedState = storage.loadGame()
        val bestScore = maxOf(storage.loadBestScore(), savedState?.bestScore ?: 0)

        setContent {
            Game2048Theme(darkTheme = storage.isDarkMode()) {
                Game2048App(
                    initialBestScore = bestScore,
                    initialSavedState = savedState,
                    onBestScoreChanged = { storage.saveBestScore(it) },
                    onStateChanged = { state -> storage.saveGame(state) },
                    storage = storage
                )
            }
        }
    }
}
