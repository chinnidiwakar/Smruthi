package uk.chinnidiwakar.smruthi.ui.calibration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import uk.chinnidiwakar.smruthi.domain.SessionSummary
import uk.chinnidiwakar.smruthi.ui.GameScreen
import uk.chinnidiwakar.smruthi.ui.game.GameViewModel

@Composable
fun CalibrationPracticeGateScreen(
    onReady: () -> Unit,
    onBackHome: () -> Unit
) {
    var latest by remember { mutableStateOf<SessionSummary?>(null) }
    var attempt by remember { mutableStateOf(0) }

    if (latest == null) {
        GameScreen(
            nLevel = 1,
            duration = 0,
            trialCount = 6,
            stimulusIntervalMs = 1000,
            adaptiveEnabled = false,
            adaptiveBlockSize = 20,
            gameViewModel = viewModel(key = "practice_$attempt"),
            onFinish = { latest = it }
        )
        return
    }

    val summary = latest!!
    val targetTotal = summary.hits + summary.misses
    val nonTargetTotal = summary.falseAlarms + summary.correctRejections
    val hitRate = if (targetTotal > 0) summary.hits.toFloat() / targetTotal else 0f
    val falseRate = if (nonTargetTotal > 0) summary.falseAlarms.toFloat() / nonTargetTotal else 0f
    val passed = targetTotal >= 2 && hitRate >= 0.6f && falseRate <= 0.4f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Calibration Practice Check", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Hit Rate: ${(hitRate * 100).toInt()}%")
        Text("False Alarm: ${(falseRate * 100).toInt()}%")

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            if (passed) "Great! You are ready for calibration."
            else "Let's retry practice once more to make sure rules are clear."
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (passed) {
            Button(onClick = onReady, modifier = Modifier.fillMaxWidth()) {
                Text("Start Calibration")
            }
        } else {
            Button(
                onClick = {
                    attempt += 1
                    latest = null
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Retry Practice")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = onBackHome, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Home")
        }
    }
}
