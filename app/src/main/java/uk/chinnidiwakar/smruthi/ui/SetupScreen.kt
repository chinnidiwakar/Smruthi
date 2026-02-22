package uk.chinnidiwakar.smruthi.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class TrainingProtocol(val title: String, val adaptive: Boolean, val stimulusMs: Int, val blockSize: Int) {
    BEGINNER("Beginner", adaptive = false, stimulusMs = 1200, blockSize = 16),
    STANDARD("Standard", adaptive = true, stimulusMs = 1000, blockSize = 20),
    RESEARCH("Research", adaptive = true, stimulusMs = 800, blockSize = 24)
}

data class TrainingConfig(
    val nLevel: Int,
    val durationSeconds: Int,
    val protocol: TrainingProtocol
)

@Composable
fun SetupScreen(
    defaultNLevel: Int = 2,
    onStart: (TrainingConfig) -> Unit
) {
    var nLevel by remember { mutableIntStateOf(defaultNLevel.coerceIn(1, 5)) }
    var duration by remember { mutableIntStateOf(120) }
    var protocol by remember { mutableStateOf(TrainingProtocol.STANDARD) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.widthIn(max = 420.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Text(
                text = "Training Setup",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Configure your working memory session",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SetupCard(title = "Protocol") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TrainingProtocol.values().forEach { preset ->
                        FilterChip(
                            selected = protocol == preset,
                            onClick = { protocol = preset },
                            label = { Text(preset.title) }
                        )
                    }
                }
            }

            SetupCard(title = "N-Level") {
                StepperControl(
                    value = nLevel,
                    range = 1..5,
                    displayMapper = { "Level $it" },
                    onValueChange = { nLevel = it }
                )
            }

            SetupCard(title = "Duration") {
                StepperControl(
                    value = duration,
                    range = 60..300,
                    step = 60,
                    displayMapper = { "${it / 60} minutes" },
                    onValueChange = { duration = it }
                )
            }

            Text(
                text = "Adaptive: ${if (protocol.adaptive) "On" else "Off"} · Stimulus: ${protocol.stimulusMs}ms · Block: ${protocol.blockSize}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onStart(
                        TrainingConfig(
                            nLevel = nLevel,
                            durationSeconds = duration,
                            protocol = protocol
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Start Training")
            }
        }
    }
}

@Composable
private fun SetupCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    var appeared by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { appeared = true }

    val scale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.96f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "cardScale"
    )

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium
            )
            content()
        }
    }
}

@Composable
private fun StepperControl(
    value: Int,
    range: IntRange,
    step: Int = 1,
    displayMapper: (Int) -> String,
    onValueChange: (Int) -> Unit
) {
    val haptics = LocalHapticFeedback.current

    fun update(newValue: Int) {
        if (newValue != value) {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onValueChange(newValue)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {

        StepperButton(
            text = "−",
            enabled = value > range.first
        ) {
            update((value - step).coerceAtLeast(range.first))
        }

        AnimatedContent(
            targetState = value,
            transitionSpec = {
                (scaleIn(
                    animationSpec = spring(
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    initialScale = 0.85f
                ) + fadeIn()) togetherWith
                    (scaleOut(targetScale = 1.1f) + fadeOut())
            },
            label = "valueAnimation"
        ) { target ->
            Text(
                text = displayMapper(target),
                style = MaterialTheme.typography.headlineSmall
            )
        }

        StepperButton(
            text = "+",
            enabled = value < range.last
        ) {
            update((value + step).coerceAtMost(range.last))
        }
    }
}

@Composable
private fun StepperButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val scale = if (pressed) 0.92f else 1f

    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interaction,
        modifier = Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        shape = MaterialTheme.shapes.medium
    ) {
        Text(text, style = MaterialTheme.typography.titleLarge)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSetup() {
    MaterialTheme {
        SetupScreen { }
    }
}
