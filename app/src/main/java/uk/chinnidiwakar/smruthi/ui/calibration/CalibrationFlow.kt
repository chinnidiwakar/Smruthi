package uk.chinnidiwakar.smruthi.ui.calibration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.chinnidiwakar.smruthi.domain.CalibrationResult
import uk.chinnidiwakar.smruthi.ui.game.GameViewModel
import uk.chinnidiwakar.smruthi.ui.GameScreen

@Composable
fun CalibrationFlow(
    navController: NavController,
    onCalibrationFinished: (CalibrationResult) -> Unit
) {
    val parentEntry = remember(navController) {
        navController.getBackStackEntry("calibration")
    }
    val calibrationViewModel: CalibrationViewModel = viewModel(parentEntry)
    val gameViewModel: GameViewModel = viewModel(parentEntry)

    val currentN = calibrationViewModel.getCurrentN()

    GameScreen(
        nLevel = currentN,
        duration = 60,
        onFinish = { summary ->
            val finished = calibrationViewModel.onBlockCompleted(summary)
            if (finished) {
                calibrationViewModel.calibrationResult?.let(onCalibrationFinished)
            }
        },
        gameViewModel = gameViewModel
    )
}
