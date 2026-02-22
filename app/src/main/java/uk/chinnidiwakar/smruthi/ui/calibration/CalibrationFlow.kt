package uk.chinnidiwakar.smruthi.ui.calibration

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.chinnidiwakar.smruthi.domain.SessionSummary
import uk.chinnidiwakar.smruthi.ui.GameScreen

@Composable
fun CalibrationFlow(navController: NavController) {

    val parentEntry = remember(navController) {
        navController.getBackStackEntry("calibration")
    }
    val calibrationViewModel: CalibrationViewModel = viewModel(parentEntry)

    val currentN = calibrationViewModel.getCurrentN()


    GameScreen(
        nLevel = currentN,
        duration = 60,
        onFinish = { summary ->
            val finished = calibrationViewModel.onBlockCompleted(summary)

            if (finished) {
                navController.navigate("calibration_result")
            } else {
                // Relaunch same screen with next N
                navController.navigate("calibration") {
                    popUpTo("calibration") { inclusive = true }
                }
            }
        }
    )
}