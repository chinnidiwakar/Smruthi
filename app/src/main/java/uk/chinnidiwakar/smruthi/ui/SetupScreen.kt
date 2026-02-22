package uk.chinnidiwakar.smruthi.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun SetupScreen(
    defaultNLevel: Int = 2,
    onStart: (Int, Int) -> Unit
) {
    var nLevel by remember { mutableIntStateOf(defaultNLevel.coerceIn(1, 5)) }
    var duration by remember { mutableIntStateOf(120) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Constrain width for tablets / landscape
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

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onStart(nLevel, duration) },
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
        SetupScreen { _, _ -> }
    }
}