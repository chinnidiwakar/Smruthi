package uk.chinnidiwakar.smruthi.ui.calibration

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CalibrationResultScreen(
    onStartTraining: (Int) -> Unit,
    onBackHome: () -> Unit
) {

    val viewModel: CalibrationViewModel = viewModel()
    val result = viewModel.calibrationResult

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