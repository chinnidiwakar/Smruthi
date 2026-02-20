package uk.chinnidiwakar.smruthi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import uk.chinnidiwakar.smruthi.ui.SetupScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmritiApp()
        }
    }
}

@Composable
fun SmritiApp() {
    MaterialTheme {
        Surface {
            SetupScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    SmritiApp()
}