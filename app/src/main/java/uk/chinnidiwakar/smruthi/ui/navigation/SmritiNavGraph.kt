package uk.chinnidiwakar.smruthi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
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
import uk.chinnidiwakar.smruthi.ui.tutorial.TutorialScreen

object NavigationGraph {
    @Composable
    fun AppNavGraph() {
        val navController = rememberNavController()
        val context = LocalContext.current
        val prefs = remember(context) { context.getSharedPreferences("smruthi_prefs", 0) }
        var hasSeenTutorial by remember { mutableStateOf(prefs.getBoolean("has_seen_tutorial", false)) }

        fun markTutorialSeen() {
            if (!hasSeenTutorial) {
                hasSeenTutorial = true
                prefs.edit().putBoolean("has_seen_tutorial", true).apply()
            }
        }

        var hasCalibrated by remember { mutableStateOf(false) }
        var recommendedN by remember { mutableIntStateOf(2) }
        var latestSummary by remember { mutableStateOf<SessionSummary?>(null) }
        var latestCalibration by remember { mutableStateOf<CalibrationResult?>(null) }

        NavHost(
            navController = navController,
            startDestination = if (hasSeenTutorial) "home" else "tutorial/first_launch"
        ) {
            composable("home") {
                HomeScreen(
                    hasCalibrated = hasCalibrated,
                    onStartTraining = { navController.navigate("setup") },
                    onRunCalibration = { navController.navigate("calibration") },
                    onViewTutorial = { navController.navigate("tutorial/home") }
                )
            }

            composable("tutorial/first_launch") {
                TutorialScreen(
                    continueLabel = "Go to Home",
                    onContinue = {
                        markTutorialSeen()
                        navController.navigate("home") {
                            popUpTo("tutorial/first_launch") { inclusive = true }
                        }
                    },
                    onSkip = {
                        markTutorialSeen()
                        navController.navigate("home") {
                            popUpTo("tutorial/first_launch") { inclusive = true }
                        }
                    }
                )
            }

            composable("tutorial/home") {
                TutorialScreen(
                    continueLabel = "Back to Home",
                    onContinue = { navController.popBackStack() },
                    onSkip = { navController.popBackStack() }
                )
            }

            composable("setup") {
                SetupScreen(
                    defaultNLevel = recommendedN,
                    onStart = { nLevel, duration ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("nLevel", nLevel)
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "duration",
                            duration
                        )
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
