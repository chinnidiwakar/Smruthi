package uk.chinnidiwakar.smruthi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import uk.chinnidiwakar.smruthi.domain.SessionSummary
import uk.chinnidiwakar.smruthi.ui.game.GameViewModel

@Composable
fun GameScreen(
    nLevel: Int,
    duration: Int,
    onFinish: (SessionSummary) -> Unit,
    gameViewModel: GameViewModel = viewModel()
) {
    val uiState by gameViewModel.uiState.collectAsState()
    val finishEvent by gameViewModel.finishEvent.collectAsState()

    LaunchedEffect(nLevel, duration) {
        gameViewModel.startGame(nLevel, duration)
    }

    LaunchedEffect(finishEvent) {
        if (finishEvent) {
            gameViewModel.getSessionSummary()?.let(onFinish)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("N = $nLevel", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Time left: ${uiState.timeRemaining}s")
            }
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = uiState.currentLetter,
                fontSize = 96.sp,
                style = MaterialTheme.typography.displayLarge
            )
        }

        Button(
            onClick = { gameViewModel.onMatchPressed() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("MATCH")
        }
    }
}
