package com.example.game2048.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game2048.ui.theme.UiButton
import com.example.game2048.ui.theme.UiButtonText

@Composable
fun GameOverOverlay(onRestart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCCFAF8EF)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Game Over!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF776E65))
        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(
                containerColor = UiButton,
                contentColor = UiButtonText
            ),
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Try Again", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun WinOverlay(onContinue: () -> Unit, onRestart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCCFAF8EF)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("You Win!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF776E65))
        Text("Keep going?", fontSize = 16.sp, color = Color(0xFF776E65))
        androidx.compose.foundation.layout.Row {
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(
                    containerColor = UiButton,
                    contentColor = UiButtonText
                ),
                modifier = Modifier.padding(6.dp)
            ) {
                Text("Keep Going", fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = UiButton,
                    contentColor = UiButtonText
                ),
                modifier = Modifier.padding(6.dp)
            ) {
                Text("New Game", fontWeight = FontWeight.Bold)
            }
        }
    }
}
