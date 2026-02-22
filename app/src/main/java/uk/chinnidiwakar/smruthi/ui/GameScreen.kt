package uk.chinnidiwakar.smruthi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.chinnidiwakar.smruthi.domain.SessionSummary
import uk.chinnidiwakar.smruthi.ui.game.GameViewModel

private val LETTERS = listOf(
    'B','C','D','F','G','H','J','K','L',
    'M','N','P','Q','R','S','T','V','X','Z'
)

@Composable
fun GameScreen(
    nLevel: Int,
    duration: Int,
    onFinish: (SessionSummary) -> Unit
) {

    val viewModel: GameViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val uiState = viewModel.uiState.collectAsState().value
    val finishEvent = viewModel.finishEvent.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.startGame(nLevel, duration)
    }

    LaunchedEffect(finishEvent) {
        if (finishEvent) {
            val summary = viewModel.getSessionSummary()
            if (summary != null) {
                onFinish(summary)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("N = $nLevel")
        Text("Time: ${uiState.timeRemaining}")

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = uiState.currentLetter,
                fontSize = 96.sp
            )
        }

        Button(
            onClick = { viewModel.onMatchPressed() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("MATCH")
        }
    }
}