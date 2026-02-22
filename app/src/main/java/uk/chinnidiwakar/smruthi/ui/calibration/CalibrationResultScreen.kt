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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uk.chinnidiwakar.smruthi.domain.CalibrationResult

@Composable
fun CalibrationResultScreen(
    result: CalibrationResult?,
    onStartTraining: (Int) -> Unit,
    onBackHome: () -> Unit
) {
    if (result == null) {
        Text("No calibration data.")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Calibration Complete",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Recommended N Level: ${result.recommendedN}")
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onStartTraining(result.recommendedN) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Training")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onBackHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Home")
        }
    }
}
