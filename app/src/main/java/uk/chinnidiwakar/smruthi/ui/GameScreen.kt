package uk.chinnidiwakar.smruthi.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import uk.chinnidiwakar.smruthi.domain.SessionSummary
import uk.chinnidiwakar.smruthi.ui.game.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun GameScreen(
    nLevel: Int,
    duration: Int,
    onFinish: (SessionSummary) -> Unit,
    gameViewModel: GameViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val uiState by gameViewModel.uiState.collectAsState()
    val finishEvent by gameViewModel.finishEvent.collectAsState()

    val haptics = LocalHapticFeedback.current
    var flash by remember { mutableStateOf(false) }

    LaunchedEffect(nLevel, duration) {
        gameViewModel.startGame(nLevel, duration)
    }

    LaunchedEffect(finishEvent) {
        if (finishEvent) {
            gameViewModel.getSessionSummary()?.let(onFinish)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // ---- HUD (Minimal) ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "N-$nLevel",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "${uiState.timeRemaining}s",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // ---- Stimulus Field ----
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                AnimatedContent(
                    targetState = uiState.currentLetter,
                    transitionSpec = {
                        (scaleIn(
                            initialScale = 0.7f,
                            animationSpec = spring(
                                stiffness = Spring.StiffnessLow
                            )
                        ) + fadeIn()) togetherWith
                                (scaleOut(targetScale = 1.2f) + fadeOut())
                    },
                    label = "stimulus"
                ) { letter ->

                    Text(
                        text = letter,
                        fontSize = 120.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.graphicsLayer {
                            if (flash) scaleX = 1.08f
                            if (flash) scaleY = 1.08f
                        }
                    )
                }
            }

            // ---- Response Pad ----
            FilledTonalButton(
                onClick = {
                    flash = true
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    gameViewModel.onMatchPressed()

                    // Launch animation reset safely from event scope
                    scope.launch {
                        delay(90)
                        flash = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                Text(
                    "MATCH",
                    style = MaterialTheme.typography.titleLarge,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}