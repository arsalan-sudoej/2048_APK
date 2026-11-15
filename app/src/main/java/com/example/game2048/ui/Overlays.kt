package com.example.game2048.ui

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game2048.core.ScoreStorage
import com.example.game2048.ui.theme.GameTheme
import com.example.game2048.ui.theme.Tile2048
import com.example.game2048.ui.theme.Tile8
import com.example.game2048.ui.theme.Tile16
import com.example.game2048.ui.theme.Tile32
import com.example.game2048.ui.theme.Tile64
import com.example.game2048.ui.theme.Tile128
import com.example.game2048.ui.theme.Tile256
import com.example.game2048.ui.theme.Tile512
import com.example.game2048.ui.theme.Tile1024
import com.example.game2048.ui.theme.Tile2
import com.example.game2048.ui.theme.Tile4
import com.example.game2048.ui.theme.TileSuper

// ---------------------------------------------------------------------------
// Overlays
// ---------------------------------------------------------------------------

@Composable
fun GameOverOverlay(onRestart: () -> Unit, score: Int, onUndo: (() -> Unit)?) {
    val colors = GameTheme.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.overlayScrim),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Game Over!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = colors.text)
        Text("Score: $score", fontSize = 18.sp, color = colors.text)
        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.button,
                contentColor = colors.buttonText
            ),
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Try Again", fontWeight = FontWeight.Bold)
        }
        if (onUndo != null) {
            TextButton(onClick = onUndo, modifier = Modifier.padding(top = 4.dp)) {
                Text("Undo last move", color = colors.text)
            }
        }
    }
}

@Composable
fun WinOverlay(onContinue: () -> Unit, onRestart: () -> Unit) {
    val colors = GameTheme.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.overlayScrim),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("You Win! 🎉", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = colors.text)
        Text("Keep going?", fontSize = 16.sp, color = colors.text)
        Row {
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.button,
                    contentColor = colors.buttonText
                ),
                modifier = Modifier.padding(6.dp)
            ) {
                Text("Keep Going", fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.button,
                    contentColor = colors.buttonText
                ),
                modifier = Modifier.padding(6.dp)
            ) {
                Text("New Game", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Score HUD
// ---------------------------------------------------------------------------

@Composable
fun ScoreBox(label: String, value: Int) {
    val colors = GameTheme.colors
    val animatedValue by animateFloatAsState(
        targetValue = value.toFloat(),
        animationSpec = tween(200),
        label = "score"
    )
    Column(
        modifier = Modifier
            .background(colors.scoreBackground, androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontSize = 10.sp, color = colors.textBright, fontWeight = FontWeight.Bold)
        Text(
            "${animatedValue.toInt()}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textBright
        )
    }
}

// ---------------------------------------------------------------------------
// Settings dialog
// ---------------------------------------------------------------------------

@Composable
fun SettingsDialog(
    visible: Boolean,
    storage: ScoreStorage,
    onDismiss: () -> Unit,
    onDarkModeChanged: (Boolean) -> Unit,
    onSoundChanged: (Boolean) -> Unit
) {
    if (!visible) return
    val colors = GameTheme.colors
    var darkMode by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(storage.isDarkMode()) }
    var sound by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(storage.isSoundEnabled()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done", color = colors.text) }
        },
        title = { Text("Settings", color = colors.text, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dark mode", color = colors.text)
                    androidx.compose.material3.Switch(
                        checked = darkMode,
                        onCheckedChange = {
                            darkMode = it
                            storage.setDarkMode(it)
                            onDarkModeChanged(it)
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sound", color = colors.text)
                    androidx.compose.material3.Switch(
                        checked = sound,
                        onCheckedChange = {
                            sound = it
                            storage.setSoundEnabled(it)
                            onSoundChanged(it)
                        }
                    )
                }
            }
        }
    )
}

// ---------------------------------------------------------------------------
// Stats dialog
// ---------------------------------------------------------------------------

@Composable
fun StatsDialog(
    visible: Boolean,
    storage: ScoreStorage,
    onDismiss: () -> Unit
) {
    if (!visible) return
    val colors = GameTheme.colors
    val stats = listOf(
        "Games played" to storage.loadGamesPlayed(),
        "Best score" to storage.loadBestScore(),
        "Highest tile" to storage.loadHighestTile(),
        "Total moves" to storage.loadTotalMoves(),
        "Total merges" to storage.loadTotalMerges(),
        "Coins" to storage.loadCoins()
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done", color = colors.text) }
        },
        title = { Text("Statistics", color = colors.text, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                stats.forEach { (label, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, color = colors.text)
                        Text("$value", color = colors.text, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    )
}

// ---------------------------------------------------------------------------
// Tile colors
// ---------------------------------------------------------------------------

fun tileColor(value: Int): Color = when (value) {
    2 -> Tile2
    4 -> Tile4
    8 -> Tile8
    16 -> Tile16
    32 -> Tile32
    64 -> Tile64
    128 -> Tile128
    256 -> Tile256
    512 -> Tile512
    1024 -> Tile1024
    else -> if (value >= 2048) Tile2048 else TileSuper
}
