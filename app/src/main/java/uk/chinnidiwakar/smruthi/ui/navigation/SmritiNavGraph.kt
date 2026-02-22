package uk.chinnidiwakar.smruthi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.chinnidiwakar.smruthi.domain.CalibrationResult
import uk.chinnidiwakar.smruthi.domain.SessionHistoryStore
import uk.chinnidiwakar.smruthi.domain.SessionSummary
import uk.chinnidiwakar.smruthi.ui.GameScreen
import uk.chinnidiwakar.smruthi.ui.HomeScreen
import uk.chinnidiwakar.smruthi.ui.ResultsScreen
import uk.chinnidiwakar.smruthi.ui.SetupScreen
import uk.chinnidiwakar.smruthi.ui.TrainingConfig
import uk.chinnidiwakar.smruthi.ui.TrainingProtocol
import uk.chinnidiwakar.smruthi.ui.TrendsDashboardScreen
import uk.chinnidiwakar.smruthi.ui.calibration.CalibrationFlow
import uk.chinnidiwakar.smruthi.ui.calibration.CalibrationPracticeGateScreen
import uk.chinnidiwakar.smruthi.ui.calibration.CalibrationResultScreen
import uk.chinnidiwakar.smruthi.ui.tutorial.TutorialScreen

object NavigationGraph {
    @Composable
    fun AppNavGraph() {
        val navController = rememberNavController()
        val context = LocalContext.current
        val prefs = remember(context) { context.getSharedPreferences("smruthi_prefs", 0) }
        val historyStore = remember { SessionHistoryStore(context) }

        var hasSeenTutorial by remember {
            mutableStateOf(prefs.getBoolean("has_seen_tutorial", false))
        }

        fun markTutorialSeen() {
            if (!hasSeenTutorial) {
                hasSeenTutorial = true
                prefs.edit().putBoolean("has_seen_tutorial", true).apply()
            }
        }

        var hasCalibrated by rememberSaveable { mutableStateOf(false) }
        var recommendedN by rememberSaveable { mutableIntStateOf(2) }
        var latestCalibration by remember { mutableStateOf<CalibrationResult?>(null) }
        var latestSummary by remember { mutableStateOf<SessionSummary?>(null) }

        val startDestination = remember(hasSeenTutorial) {
            if (hasSeenTutorial) "home" else "tutorial/first_launch"
        }

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable("home") {
                HomeScreen(
                    hasCalibrated = hasCalibrated,
                    recommendedN = recommendedN,
                    lastSummary = latestSummary,
                    streakDays = historyStore.getCurrentStreak(),
                    onStartTraining = { navController.navigate("setup") },
                    onRunCalibration = { navController.navigate("calibration") },
                    onViewTutorial = { navController.navigate("tutorial/home") },
                    onViewTrends = { navController.navigate("trends") }
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
                    onSkip = { navController.popBackStack() },
                    onStartTraining = { navController.navigate("tutorial/training") },
                    onRunCalibration = { navController.navigate("tutorial/calibration") }
                )
            }


            composable("tutorial/training") {
                TutorialScreen(
                    continueLabel = "Go to Training",
                    onContinue = {
                        navController.navigate("setup")
                    },
                    onSkip = {
                        navController.navigate("setup")
                    }
                )
            }

            composable("tutorial/calibration") {
                TutorialScreen(
                    continueLabel = "Go to Calibration",
                    onContinue = {
                        navController.navigate("calibration")
                    },
                    onSkip = {
                        navController.navigate("calibration")
                    }
                )
            }

            composable("setup") {
                SetupScreen(
                    defaultNLevel = recommendedN,
                    onStart = { config: TrainingConfig ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("nLevel", config.nLevel)
                        navController.currentBackStackEntry?.savedStateHandle?.set("duration", config.durationSeconds)
                        navController.currentBackStackEntry?.savedStateHandle?.set("protocol", config.protocol.name)
                        navController.currentBackStackEntry?.savedStateHandle?.set("adaptive", config.protocol.adaptive)
                        navController.currentBackStackEntry?.savedStateHandle?.set("stimulusMs", config.protocol.stimulusMs)
                        navController.currentBackStackEntry?.savedStateHandle?.set("blockSize", config.protocol.blockSize)
                        navController.navigate("game")
                    }
                )
            }

            composable("game") {
                val nLevel = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("nLevel") ?: recommendedN
                val duration = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("duration") ?: 120
                val protocolName = navController.previousBackStackEntry?.savedStateHandle?.get<String>("protocol") ?: TrainingProtocol.STANDARD.name
                val adaptive = navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>("adaptive") ?: true
                val stimulusMs = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("stimulusMs") ?: 1000
                val blockSize = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("blockSize") ?: 20

                GameScreen(
                    nLevel = nLevel,
                    duration = duration,
                    stimulusIntervalMs = stimulusMs,
                    adaptiveEnabled = adaptive,
                    adaptiveBlockSize = blockSize,
                    onFinish = { summary ->
                        latestSummary = summary
                        historyStore.addSession(summary, protocolName)
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

            composable("trends") {
                TrendsDashboardScreen(
                    trends = historyStore.getWeeklyTrends(),
                    exportJson = historyStore.exportAsJson(30),
                    exportCsv = historyStore.exportAsCsv(30),
                    onBack = { navController.popBackStack() }
                )
            }

            composable("calibration_practice") {
                CalibrationPracticeGateScreen(
                    onReady = { navController.navigate("calibration") },
                    onBackHome = {
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
                        navController.currentBackStackEntry?.savedStateHandle?.set("protocol", TrainingProtocol.STANDARD.name)
                        navController.currentBackStackEntry?.savedStateHandle?.set("adaptive", true)
                        navController.currentBackStackEntry?.savedStateHandle?.set("stimulusMs", 1000)
                        navController.currentBackStackEntry?.savedStateHandle?.set("blockSize", 20)
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
