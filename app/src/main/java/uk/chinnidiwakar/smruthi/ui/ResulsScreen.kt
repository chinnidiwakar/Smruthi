package uk.chinnidiwakar.smruthi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uk.chinnidiwakar.smruthi.domain.PerformanceEvaluator
import uk.chinnidiwakar.smruthi.domain.SessionSummary

@Composable
fun ResultsScreen(
    summary: SessionSummary?,
    onDone: () -> Unit
) {
    if (summary == null) {
        Text("No session data available.")
        return
    }

    val total = summary.hits + summary.misses + summary.falseAlarms + summary.correctRejections
    val accuracy =
        if (total > 0)
            ((summary.hits + summary.correctRejections).toFloat() / total * 100).toInt()
        else 0

    val recommendation = PerformanceEvaluator.evaluate(
        currentN = summary.nLevel,
        hits = summary.hits,
        misses = summary.misses,
        falseAlarms = summary.falseAlarms,
        correctRejections = summary.correctRejections,
        avgHitRt = summary.averageHitReactionTimeMs
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Session Complete", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        Text("Accuracy: $accuracy%")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Hits: ${summary.hits}")
        Text("Misses: ${summary.misses}")
        Text("False Alarms: ${summary.falseAlarms}")
        Text("Correct Rejections: ${summary.correctRejections}")
        Text("Avg Hit RT: ${summary.averageHitReactionTimeMs} ms")
        Text("Avg False RT: ${summary.averageFalseAlarmReactionTimeMs} ms")

        Spacer(modifier = Modifier.height(20.dp))

        Text(recommendation.message, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Home")
        }
    }
}
