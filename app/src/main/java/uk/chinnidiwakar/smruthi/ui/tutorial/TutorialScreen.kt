package uk.chinnidiwakar.smruthi.ui.tutorial

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun TutorialScreen(
    continueLabel: String,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
    onStartTraining: (() -> Unit)? = null,
    onRunCalibration: (() -> Unit)? = null
) {
    val steps = remember {
        listOf(
            "N-Back is a memory game. Tap MATCH when the current letter is the same as N steps back.",
            "N-1 Example: B, C, C, D.\nAt the third letter C, MATCH because it equals the previous letter.",
            "N-2 Example: A, B, A, C, A.\nAt the third and fifth letters, MATCH because they equal the letters two steps back."
        )
    }

    var step by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("How to Play", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Quick walkthrough before you start.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            AnimatedContent(
                targetState = step,
                transitionSpec = { (fadeIn() togetherWith fadeOut()) },
                label = "tutorialStep"
            ) { currentStep ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Step ${currentStep + 1} of ${steps.size}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(steps[currentStep], style = MaterialTheme.typography.bodyLarge)

                        if (currentStep == 1) {
                            Spacer(modifier = Modifier.height(16.dp))
                            ExampleSequence(nLevel = 1, letters = listOf('B', 'C', 'C', 'D'))
                        }

                        if (currentStep == 2) {
                            Spacer(modifier = Modifier.height(16.dp))
                            ExampleSequence(nLevel = 2, letters = listOf('A', 'B', 'A', 'C', 'A'))
                        }
                    }
                }
            }
        }

        Column {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(steps.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(width = if (index == step) 24.dp else 10.dp, height = 10.dp)
                            .background(
                                color = if (index == step) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant
                                },
                                shape = RoundedCornerShape(999.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onSkip,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Skip")
                    }

                    Button(
                        onClick = {
                            if (step < steps.lastIndex) step++ else onContinue()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (step < steps.lastIndex) "Next" else continueLabel)
                    }
                }

                // --- Optional action buttons (shown only in tutorial/home) ---

                if (onStartTraining != null || onRunCalibration != null) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (onStartTraining != null) {
                            Button(
                                onClick = onStartTraining,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Start Training")
                            }
                        }

                        if (onRunCalibration != null) {
                            OutlinedButton(
                                onClick = onRunCalibration,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Run Calibration")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExampleSequence(
    nLevel: Int,
    letters: List<Char>
) {
    var activeIndex by remember(nLevel, letters) { mutableIntStateOf(0) }

    LaunchedEffect(nLevel, letters) {
        while (true) {
            delay(700)
            activeIndex = (activeIndex + 1) % letters.size
        }
    }
    val transition = androidx.compose.animation.core.rememberInfiniteTransition(label = "example")

    // Animate time progression across the sequence
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = letters.size.toFloat(),
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(
                durationMillis = letters.size * 700,
                easing = androidx.compose.animation.core.LinearEasing
            )
        ),
        label = "progress"
    )

    // Derive the active index from animation time
    val activeIndex = progress.toInt() % letters.size

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("N-$nLevel animation", fontWeight = FontWeight.SemiBold)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            letters.forEachIndexed { index, letter ->
                val isCurrent = index == activeIndex
                val isMatch = index >= nLevel && letters[index] == letters[index - nLevel]

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .border(
                            width = if (isCurrent) 2.dp else 1.dp,
                            color = if (isCurrent) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outlineVariant
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            color = if (isCurrent && isMatch) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                            color = if (isCurrent)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            color = if (isCurrent && isMatch)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(letter.toString(), style = MaterialTheme.typography.titleLarge)
                }
            }
        }

        val hint =
            if (activeIndex >= nLevel && letters[activeIndex] == letters[activeIndex - nLevel]) {
                "This is a MATCH. Tap the MATCH button now."
            } else {
                "No match here. Wait for the next letter."
            }

        Text(hint, style = MaterialTheme.typography.bodySmall)
    }
}
