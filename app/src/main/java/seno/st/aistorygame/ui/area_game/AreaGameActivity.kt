package seno.st.aistorygame.ui.area_game

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import seno.st.aistorygame.extension.startActivity
import seno.st.aistorygame.theme.AiStoryGameTheme

class AreaGameActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AiStoryGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize()) {
                    AreaGameScreen()
                }
            }
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(AreaGameActivity::class.java)
        }
    }
}