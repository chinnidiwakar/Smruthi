package uk.chinnidiwakar.smruthi.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun SetupScreen() {

    var nLevel by remember { mutableStateOf(2) }
    var duration by remember { mutableStateOf(120) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Smriti", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        Text("N Level: $nLevel")

        Row {
            Button(onClick = { if (nLevel > 1) nLevel-- }) {
                Text("-")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { if (nLevel < 5) nLevel++ }) {
                Text("+")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Duration: ${duration / 60} min")

        Row {
            Button(onClick = { if (duration > 60) duration -= 60 }) {
                Text("-")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { if (duration < 300) duration += 60 }) {
                Text("+")
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                // For now just log start
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Training")
        }
    }
}