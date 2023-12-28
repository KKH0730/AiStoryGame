package seno.st.aistorygame.ui.story_game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import seno.st.aistorygame.theme.AiStoryGameTheme
import seno.st.aistorygame.util.KeyboardHeightProvider

@AndroidEntryPoint
class StoryGameActivity : ComponentActivity() {
    private val aiStoryViewModel by viewModels<StoryGameViewModel>()
    private lateinit var keyboardHeightProvider: KeyboardHeightProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AiStoryGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize()) {
                    StoryGameScreen(
                        aiStoryHistory = aiStoryViewModel.storyList.collectAsStateWithLifecycle().value,
                        isShowKeyboard = aiStoryViewModel.isKeyboardVisible.collectAsStateWithLifecycle().value,
                        onClickSend = aiStoryViewModel::send
                    )
                }
            }
            keyboardObserve()
        }
    }

    private fun keyboardObserve() {
        keyboardHeightProvider = KeyboardHeightProvider(activity = this@StoryGameActivity)
            .init()
            .setHeightListener(object : KeyboardHeightProvider.HeightListener {
                override fun onKeyboardHeightChanged(keyboardHeight: Int) {
                    aiStoryViewModel.updateKeyboardState(isVisible = keyboardHeight > 0)
                }
            })
    }

    override fun onDestroy() {
        keyboardHeightProvider.release()
        super.onDestroy()
    }
}