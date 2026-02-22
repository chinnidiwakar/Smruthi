package uk.chinnidiwakar.smruthi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.chinnidiwakar.smruthi.domain.CalibrationResult
import uk.chinnidiwakar.smruthi.domain.SessionSummary
import uk.chinnidiwakar.smruthi.ui.GameScreen
import uk.chinnidiwakar.smruthi.ui.HomeScreen
import uk.chinnidiwakar.smruthi.ui.ResultsScreen
import uk.chinnidiwakar.smruthi.ui.SetupScreen
import uk.chinnidiwakar.smruthi.ui.calibration.CalibrationFlow
import uk.chinnidiwakar.smruthi.ui.calibration.CalibrationResultScreen

object NavigationGraph {
    @Composable
    fun AppNavGraph() {
        val navController = rememberNavController()

        var hasCalibrated by remember { mutableStateOf(false) }
        var recommendedN by remember { mutableIntStateOf(2) }
        var latestSummary by remember { mutableStateOf<SessionSummary?>(null) }
        var latestCalibration by remember { mutableStateOf<CalibrationResult?>(null) }

        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") {
                HomeScreen(
                    hasCalibrated = hasCalibrated,
                    onStartTraining = { navController.navigate("setup") },
                    onRunCalibration = { navController.navigate("calibration") }
                )
            }

            composable("setup") {
                SetupScreen(
                    defaultNLevel = recommendedN,
                    onStart = { nLevel, duration ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("nLevel", nLevel)
                        navController.currentBackStackEntry?.savedStateHandle?.set("duration", duration)
                        navController.navigate("game")
                    }
                )
            }

            composable("game") {
                val nLevel = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<Int>("nLevel") ?: recommendedN
                val duration = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<Int>("duration") ?: 120

                GameScreen(
                    nLevel = nLevel,
                    duration = duration,
                    onFinish = { summary ->
                        latestSummary = summary
                        navController.navigate("results")
                    }
                )
            }

            composable("results") {
                ResultsScreen(
                    summary = latestSummary,
                    onDone = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }

            composable("calibration") {
                CalibrationFlow(
                    navController = navController,
                    onCalibrationFinished = { result ->
                        hasCalibrated = true
                        recommendedN = result.recommendedN
                        latestCalibration = result
                        navController.navigate("calibration_result")
                    }
                )
            }

            composable("calibration_result") {
                CalibrationResultScreen(
                    result = latestCalibration,
                    onStartTraining = { nLevel ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("nLevel", nLevel)
                        navController.currentBackStackEntry?.savedStateHandle?.set("duration", 120)
                        navController.navigate("game")
                    },
                    onBackHome = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
