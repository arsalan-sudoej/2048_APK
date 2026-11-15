package com.example.game2048.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.game2048.ui.theme.UiText

@Composable
fun Game2048App() {
    var state by remember { mutableStateOf(GameState.newGame(bestScore = 0)) }
    var bestScore by remember { mutableStateOf(0) }

    val swipeThreshold = 24.dp

    fun onMove(direction: Direction) {
        GameEngine.move(state, direction)?.let { newState ->
            state = newState
            if (newState.bestScore > bestScore) {
                bestScore = newState.bestScore
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UiBackground())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "2048",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = UiText()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Score row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("SCORE", fontSize = 12.sp, color = UiText())
                Text(
                    "${state.score}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = UiText()
                )
            }
            Column {
                Text("BEST", fontSize = 12.sp, color = UiText())
                Text(
                    "${state.bestScore}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = UiText()
                )
            }
            Button(
                onClick = { state = GameState.newGame(state.bestScore) },
                text = "NEW GAME"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Board
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(BoardBackground(), RoundedCornerShape(8.dp))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                        },
                        onDragEnd = {
                            val dx = accumulatedDrag?.first ?: 0f
                            val dy = accumulatedDrag?.second ?: 0f
                            accumulatedDrag = null
                            if (kotlin.math.abs(dx) > thresholdPx || kotlin.math.abs(dy) > thresholdPx) {
                                if (kotlin.math.abs(dx) > kotlin.math.abs(dy)) {
                                    onMove(if (dx > 0) Direction.RIGHT else Direction.LEFT)
                                } else {
                                    onMove(if (dy > 0) Direction.DOWN else Direction.UP)
                                }
                            }
                        }
                    ) { _, dragAmount ->
                        accumulatedDrag = Pair(
                            (accumulatedDrag?.first ?: 0f) + dragAmount.x,
                            (accumulatedDrag?.second ?: 0f) + dragAmount.y
                        )
                    }
                }
        ) {
            val cell = 1f / 4f
            val pad = 0.02f
            for (r in 0..3) {
                for (c in 0..3) {
                    val value = state.board[r][c]
                    if (value > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .offsetFraction(x = c * cell + pad, y = r * cell + pad)
                                .sizeFraction(cell - 2 * pad)
                                .background(tileColor(value), RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$value",
                                color = if (value >= 8) Color.White else UiText(),
                                fontSize = when {
                                    value < 100 -> 28.sp
                                    value < 1000 -> 22.sp
                                    else -> 18.sp
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .offsetFraction(x = c * cell + pad, y = r * cell + persistence)
                                .sizeFraction(cell - 2 * pad)
                                .background(BoardEmpty(), RoundedCornerShape(6.dp))
                        )
                    }
                }
            }
        }

        if (state.isGameOver) {
            GameOverOverlay(
                onRestart = { state = GameState.newGame(state.bestScore) }
            )
        }
        if (state.isWin) {
            WinOverlay(
                onContinue = {
                    state = state.copy(isWin = false)
                },
                onRestart = { state = GameState.newGame(state.bestScore) }
            )
        }
    }
}

private var accumulatedDrag: Pair<Float, Float>? = null
private var thresholdPx: Float = 80f

@Composable
fun UiBackground(): Color = com.example.game2048.ui.theme.UiBackground

@Composable
fun UiText(): Color = com.example.game2048.ui.theme.UiText

@Composable
fun BoardBackground(): Color = com.example.game2048.ui.theme.BoardBackground

@Composable
fun BoardEmpty(): Color = com.example.game2048.ui.theme.BoardEmpty

@Composable
fun Button(onClick: () -> Unit, text: String) {
    androidx.compose.material3.Button(
        onClick = onClick,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = com.example.game2048.ui.theme.UiButton,
            contentColor = com.example.game2048.ui.theme.UiButtonText
        )
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
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

fun Modifier.offsetFraction(x: Float, y: Float): Modifier = this.then(
    Modifier.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.placeRelative((x * placeable.width).toInt(), (y * placeable.height).toInt())
        }
    }
)

fun Modifier.sizeFraction(fraction: Float): Modifier = this.then(
    Modifier.layout { measurable, constraints ->
        val wrapped = constraints.copy(minWidth = 0, minHeight = 0)
        val placeable = measurable.measure(
            androidx.compose.foundation.layout.wrapContentSize(unbounded = true).run {
                wrapped
            }
        )
        layout((fraction * placeable.width).toInt(), (fraction * placeable.height).toInt()) {
            placeable.placeRelative(0, 0)
        }
    }
)
