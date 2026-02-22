package uk.chinnidiwakar.smruthi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    hasCalibrated: Boolean,
    onStartTraining: () -> Unit,
    onRunCalibration: () -> Unit,
    onViewTutorial: () -> Unit,
    onViewTrends: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column {

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Smruthi",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cognitive Training System",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (!hasCalibrated) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Calibration Recommended",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Run calibration to determine your optimal training level.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        Column {

            Button(
                onClick = onStartTraining,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Start Training")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onRunCalibration,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Run Calibration")
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onViewTutorial,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Tutorial")
            }

            TextButton(
                onClick = onViewTrends,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Trends & Export")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
