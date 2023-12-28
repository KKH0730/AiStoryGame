package seno.st.aistorygame.ui.story_game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import seno.st.aistorygame.ui.story_game.component.ChatInputView
import seno.st.aistorygame.ui.story_game.component.StoryChatContainer
import seno.st.aistorygame.ui.story_game.model.StoryChatModel

@Composable
fun StoryGameScreen(
    aiStoryHistory: List<StoryChatModel>,
    isShowKeyboard: Boolean,
    onClickSend: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxSize()
    ) {
        StoryChatContainer(
            aiStoryHistory = aiStoryHistory,
            isShowKeyboard = isShowKeyboard,
            modifier = Modifier.weight(weight = 1f)
        )
        ChatInputView(onClickSend = onClickSend, modifier = Modifier)
    }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}