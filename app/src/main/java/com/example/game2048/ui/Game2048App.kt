package com.example.game2048.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game2048.core.Direction
import com.example.game2048.core.GameEngine
import com.example.game2048.core.GameState
import com.example.game2048.core.ScoreStorage
import com.example.game2048.sound.SoundManager
import com.example.game2048.ui.theme.GameTheme
import kotlin.math.abs

@Composable
fun Game2048App(
    initialBestScore: Int = 0,
    initialSavedState: GameState? = null,
    onBestScoreChanged: (Int) -> Unit = {},
    onStateChanged: (GameState) -> Unit = {},
    storage: ScoreStorage? = null
) {
    val colors = GameTheme.colors
    var state by remember {
        mutableStateOf(initialSavedState ?: GameState.newGame(bestScore = initialBestScore))
    }
    var previousState by remember { mutableStateOf<GameState?>(null) }
    var showWin by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showStats by remember { mutableStateOf(false) }
    var darkMode by remember { mutableStateOf(storage?.isDarkMode() ?: false) }
    var soundEnabled by remember { mutableStateOf(storage?.isSoundEnabled() ?: true) }

    val soundManager = remember { SoundManager() }
    soundManager.setEnabled(soundEnabled)

    fun onMove(direction: Direction) {
        if (state.isGameOver) return
        val oldState = state
        val newState = GameEngine.move(oldState, direction) ?: return

        val merges = newState.mergeCount - oldState.mergeCount
        if (merges > 0) {
            soundManager.playMerge()
        } else {
            soundManager.playMove()
        }

        previousState = oldState
        if (newState.bestScore > oldState.bestScore) onBestScoreChanged(newState.bestScore)
        state = newState
        onStateChanged(newState)

        if (newState.isWin && !oldState.isWin) {
            showWin = true
            soundManager.playWin()
            storage?.saveHighestTileIfBetter(GameEngine.highestTile(newState.board))
            storage?.addCoins(100)
        }
        if (newState.isGameOver) {
            soundManager.playGameOver()
            storage?.incrementGamesPlayed()
            storage?.addTotalMoves(newState.moveCount)
            storage?.addTotalMerges(newState.mergeCount)
            storage?.saveHighestTileIfBetter(GameEngine.highestTile(newState.board))
            storage?.addCoins(newState.score / 100)
        }
    }

    fun restart() {
        state = GameState.newGame(state.bestScore)
        previousState = null
        showWin = false
        onStateChanged(state)
    }

    fun undo() {
        val prev = previousState ?: return
        state = prev
        previousState = null
        onStateChanged(state)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Header: title + score boxes ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "2048",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text
            )
            ScoreBox(label = "SCORE", value = state.score)
            ScoreBox(label = "BEST", value = state.bestScore)
        }

        // --- Controls: undo / new / stats / settings ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = ::undo,
                enabled = previousState != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.button,
                    contentColor = colors.buttonText,
                    disabledContainerColor = colors.scoreBackground,
                    disabledContentColor = colors.textBright
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("UNDO", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Button(
                onClick = ::restart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.button,
                    contentColor = colors.buttonText
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("NEW", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Button(
                onClick = { showStats = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.scoreBackground,
                    contentColor = colors.textBright
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("STATS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Button(
                onClick = { showSettings = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.scoreBackground,
                    contentColor = colors.textBright
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("MENU", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        // --- Board with swipe gestures ---
        var dragX by remember { mutableFloatStateOf(0f) }
        var dragY by remember { mutableFloatStateOf(0f) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(colors.boardBackground, RoundedCornerShape(10.dp))
                .padding(6.dp)
                .pointerInput(state.isGameOver) {
                    detectDragGestures(
                        onDrag = { change, amount ->
                            change.consume()
                            dragX += amount.x
                            dragY += amount.y
                        },
                        onDragEnd = {
                            val threshold = 40f
                            if (abs(dragX) > threshold || abs(dragY) > threshold) {
                                if (abs(dragX) > abs(dragY)) {
                                    onMove(if (dragX > 0) Direction.RIGHT else Direction.LEFT)
                                } else {
                                    onMove(if (dragY > 0) Direction.DOWN else Direction.UP)
                                }
                            }
                            dragX = 0f
                            dragY = 0f
                        }
                    )
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                for (r in 0..3) {
                    Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        for (c in 0..3) {
                            val value = state.board[r][c]
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(3.dp)
                                    .background(
                                        if (value == 0) colors.boardEmpty else tileColor(value),
                                        RoundedCornerShape(6.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (value > 0) {
                                    Text(
                                        text = "$value",
                                        color = if (value >= 8) colors.textBright else colors.text,
                                        fontSize = if (value < 100) 26.sp
                                        else if (value < 1000) 20.sp
                                        else 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (state.isGameOver) {
                GameOverOverlay(
                    onRestart = ::restart,
                    score = state.score,
                    onUndo = if (previousState != null) ::undo else null
                )
            }
            if (showWin) {
                WinOverlay(onContinue = { showWin = false }, onRestart = ::restart)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Swipe to move tiles. Merge tiles to reach 2048! Earn coins every game.",
            fontSize = 13.sp,
            color = colors.text
        )
    }

    if (showSettings && storage != null) {
        SettingsDialog(
            visible = true,
            storage = storage,
            onDismiss = { showSettings = false },
            onDarkModeChanged = { darkMode = it },
            onSoundChanged = { soundEnabled = it }
        )
    }
    if (showStats && storage != null) {
        StatsDialog(visible = true, storage = storage, onDismiss = { showStats = false })
    }
}
