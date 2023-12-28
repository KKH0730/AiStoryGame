package seno.st.aistorygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import seno.st.aistorygame.theme.AiStoryGameTheme
import seno.st.aistorygame.ui.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            this.window.statusBarColor = ContextCompat.getColor(this, R.color.black)

            AiStoryGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainUI()
                }
            }
        }
    }

    @Composable
    private fun MainUI() {
        HomeScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AiStoryGameTheme {

    }
}