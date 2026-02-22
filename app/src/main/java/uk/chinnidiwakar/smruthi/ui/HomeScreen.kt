package uk.chinnidiwakar.smruthi.ui

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import uk.chinnidiwakar.smruthi.domain.SessionSummary

@Composable
fun HomeScreen(
    hasCalibrated: Boolean,
    recommendedN: Int?,
    lastSummary: SessionSummary?,
    streakDays: Int,
    onStartTraining: () -> Unit,
    onRunCalibration: () -> Unit,
    onViewTutorial: () -> Unit,
    onViewTrends: () -> Unit
) {
    val appeared = remember { androidx.compose.animation.core.Animatable(0f) }

    // Entrance animation (quick, calm)
    LaunchedEffect(Unit) {
        appeared.animateTo(1f, animationSpec = tween(450))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 24.dp)
            .graphicsLayer { alpha = appeared.value },
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        Spacer(Modifier.height(8.dp))

        // ---- Header ----
        Column {
            Text("Smruthi", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Working Memory Training",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // ---- Recommended Level ----
        if (hasCalibrated && recommendedN != null) {
            ElevatedCard {
                Column(Modifier.padding(20.dp)) {
                    Text("Recommended Level", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "N-$recommendedN",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // ---- Streak Indicator ----
        if (streakDays > 0) {
            AssistChip(
                onClick = {},
                label = { Text("🔥 $streakDays day streak") }
            )
        }

        // ---- Last Session Preview ----
        if (lastSummary != null) {
            ElevatedCard {
                Column(Modifier.padding(20.dp)) {
                    Text("Last Session", style = MaterialTheme.typography.titleMedium)

                    Spacer(Modifier.height(8.dp))

                    Text("Hits: ${lastSummary.hits}")
                    Text("Misses: ${lastSummary.misses}")
                    Text("False Alarms: ${lastSummary.falseAlarms}")
                }
            }
        }

        // ---- Primary Action ----
        Button(
            onClick = onStartTraining,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Text("Start Training", style = MaterialTheme.typography.titleMedium)
        }

        // ---- Secondary Actions ----
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

            OutlinedButton(
                onClick = onRunCalibration,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (hasCalibrated) "Recalibrate" else "Run Calibration")
            }

            TextButton(onClick = onViewTutorial, modifier = Modifier.fillMaxWidth()) {
                Text("How This Works")
            }

            TextButton(onClick = onViewTrends, modifier = Modifier.fillMaxWidth()) {
                Text("Progress & Export Data")
            }
        }
    }
}