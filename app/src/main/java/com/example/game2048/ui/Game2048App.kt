package com.example.game2048.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game2048.core.Direction
import com.example.game2048.core.GameEngine
import com.example.game2048.core.GameState
import com.example.game2048.ui.theme.BoardBackground
import com.example.game2048.ui.theme.BoardEmpty
import com.example.game2048.ui.theme.UiBackground
import com.example.game2048.ui.theme.UiButton
import com.example.game2048.ui.theme.UiButtonText
import com.example.game2048.ui.theme.UiText
import kotlin.math.abs

@Composable
fun Game2048App() {
    var state by remember { mutableStateOf(GameState.newGame(bestScore = 0)) }
    var showWin by remember { mutableStateOf(false) }

    fun onMove(direction: Direction) {
        val newState = GameEngine.move(state, direction) ?: return
        if (newState.isWin && !state.isWin) showWin = true
        state = newState
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UiBackground)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header: title + scores + new game
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
                color = UiText
            )
            ScoreBox(label = "SCORE", value = state.score)
            ScoreBox(label = "BEST", value = state.bestScore)
            Button(
                onClick = {
                    state = GameState.newGame(state.bestScore)
                    showWin = false
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = UiButton,
                    contentColor = UiButtonText
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 10.dp, vertical = 4.dp
                )
            ) {
                Text("NEW", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        // Board with swipe input
        var dragX by remember { mutableFloatStateOf(0f) }
        var dragY by remember { mutableFloatStateOf(0f) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(BoardBackground, RoundedCornerShape(10.dp))
                .padding(6.dp)
                .pointerInput(Unit) {
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
                                        if (value == 0) BoardEmpty else tileColor(value),
                                        RoundedCornerShape(6.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (value > 0) {
                                    Text(
                                        text = "$value",
                                        color = if (value >= 8) Color(0xFFF9F6F2) else UiText,
                                        fontSize = if (value < 100) 26.sp else if (value < 1000) 20.sp else 16.sp,
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
                    onRestart = {
                        state = GameState.newGame(state.bestScore)
                    }
                )
            }
            if (showWin) {
                WinOverlay(
                    onContinue = { showWin = false },
                    onRestart = {
                        state = GameState.newGame(state.bestScore)
                        showWin = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Swipe to move tiles. Merge tiles to reach 2048!",
            fontSize = 13.sp,
            color = UiText
        )
    }
}

@Composable
private fun ScoreBox(label: String, value: Int) {
    Column(
        modifier = Modifier
            .background(UiScoreBackground, RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontSize = 10.sp, color = Color(0xFFEEE4DA), fontWeight = FontWeight.Bold)
        Text(
            "$value",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF9F6F2)
        )
    }
}

fun tileColor(value: Int): Color = when (value) {
    2 -> com.example.game2048.ui.theme.Tile2
    4 -> com.example.game2048.ui.theme.Tile4
    8 -> com.example.game2048.ui.theme.Tile8
    16 -> com.example.game2048.ui.theme.Tile16
    32 -> com.example.game2048.ui.theme.Tile32
    64 -> com.example.game2048.ui.theme.Tile64
    128 -> com.example.game2048.ui.theme.Tile128
    256 -> com.example.game2048.ui.theme.Tile256
    512 -> com.example.game2048.ui.theme.Tile512
    1024 -> com.example.game2048.ui.theme.Tile1024
    else -> com.example.game2048.ui.theme.Tile2048
}
