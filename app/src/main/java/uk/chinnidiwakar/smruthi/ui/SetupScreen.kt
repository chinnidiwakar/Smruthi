package uk.chinnidiwakar.smruthi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SetupScreen(
    defaultNLevel: Int = 2,
    onStart: (Int, Int) -> Unit
) {
    var nLevel by remember(defaultNLevel) { mutableIntStateOf(defaultNLevel.coerceIn(1, 5)) }
    var duration by remember { mutableIntStateOf(120) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Training Setup", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("N Level: $nLevel", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    Button(onClick = { if (nLevel > 1) nLevel-- }) { Text("-") }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { if (nLevel < 5) nLevel++ }) { Text("+") }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Duration: ${duration / 60} min", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    Button(onClick = { if (duration > 60) duration -= 60 }) { Text("-") }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { if (duration < 300) duration += 60 }) { Text("+") }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onStart(nLevel, duration) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Training")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSetup() {
    SetupScreen { _, _ -> }
}
