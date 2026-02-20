package uk.chinnidiwakar.smruthi.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ResultsScreen(
    hits: Int,
    misses: Int,
    falseAlarms: Int,
    correctRejections: Int,
    onDone: () -> Unit
) {

    val total = hits + misses + falseAlarms + correctRejections
    val accuracy =
        if (total > 0)
            ((hits + correctRejections).toFloat() / total * 100).toInt()
        else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Session Complete",
            style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        Text("Accuracy: $accuracy%")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Hits: $hits")
        Text("Misses: $misses")
        Text("False Alarms: $falseAlarms")
        Text("Correct Rejections: $correctRejections")

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Setup")
        }
    }
}