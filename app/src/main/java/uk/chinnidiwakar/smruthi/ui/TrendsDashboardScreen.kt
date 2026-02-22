package uk.chinnidiwakar.smruthi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import uk.chinnidiwakar.smruthi.domain.DayTrend

@Composable
fun TrendsDashboardScreen(
    trends: List<DayTrend>,
    exportJson: String,
    exportCsv: String,
    onBack: () -> Unit
) {
    val clipboard = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Weekly Trends", style = MaterialTheme.typography.headlineMedium)

        trends.forEach { trend ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(trend.dayLabel, style = MaterialTheme.typography.titleMedium)
                    Text("Hit Rate: ${(trend.avgHitRate * 100).toInt()}%")
                    Text("False Alarm: ${(trend.avgFalseAlarmRate * 100).toInt()}%")
                    Text("Avg Hit RT: ${trend.avgHitRtMs} ms")
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { clipboard.setText(AnnotatedString(exportJson)) }, modifier = Modifier.weight(1f)) {
                Text("Copy JSON")
            }
            OutlinedButton(onClick = { clipboard.setText(AnnotatedString(exportCsv)) }, modifier = Modifier.weight(1f)) {
                Text("Copy CSV")
            }
        }

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}
