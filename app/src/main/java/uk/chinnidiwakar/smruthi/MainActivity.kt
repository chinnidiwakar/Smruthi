package uk.chinnidiwakar.smruthi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import uk.chinnidiwakar.smruthi.ui.SetupScreen
import uk.chinnidiwakar.smruthi.ui.navigation.SmritiNavGraph
import uk.chinnidiwakar.smruthi.ui.theme.SmruthiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmruthiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SmritiNavGraph()
                }
            }
        }
    }
}

@Composable
fun SmruthiApp() {
    SmruthiTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SmritiNavGraph()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    SmruthiApp()
}