package seno.st.aistorygame.ui.story_game.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import seno.st.aistorygame.R
import seno.st.aistorygame.extension.textDp
import seno.st.aistorygame.theme.Color_FF2E2F39
import seno.st.aistorygame.theme.Color_FF43444D
import seno.st.aistorygame.theme.Color_FF4F505B
import seno.st.aistorygame.theme.TransParent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatInputView(onClickSend: (String) -> Unit, modifier: Modifier) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var inputText by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color_FF2E2F39)
    ) {
        Box(
            modifier = Modifier
                .padding(start = 24.dp, end = 12.dp, bottom = 24.dp)
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color_FF4F505B
                    ),
                    shape = RoundedCornerShape(size = 16.dp),
                )
        ) {
            Row(
                modifier = Modifier.align(alignment = Alignment.BottomCenter)
            ) {
                TextField(
                    value = "",
                    onValueChange = {},
                    enabled = false,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = TransParent,
                        textColor = Color.White,
                        cursorColor = Color.White,
                        focusedIndicatorColor = TransParent,
                        unfocusedIndicatorColor = TransParent,
                        disabledIndicatorColor = TransParent
                    ),
                    modifier = Modifier.weight(1f)
                )
                Image(
                    painter = if (inputText.isNotEmpty()) {
                        painterResource(id = R.drawable.ic_send_active)
                    } else {
                        painterResource(id = R.drawable.ic_send)
                    },
                    contentDescription = "send",
                    colorFilter = ColorFilter.tint(
                        color = if (inputText.isNotEmpty()) {
                            Color.White
                        } else {
                            Color_FF43444D
                        }
                    ),
                    modifier = Modifier
                        .size(size = 45.dp)
                        .align(alignment = Alignment.CenterVertically)
                        .padding(end = 10.dp)
                        .clickable {
                            if (inputText.isNotEmpty()) {
                                onClickSend.invoke(inputText)
                                inputText = ""
                                keyboardController?.hide()
                            }
                        }
                )
            }
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = TransParent,
                    textColor = Color.White,
                    cursorColor = Color.White,
                    focusedIndicatorColor = TransParent,
                    unfocusedIndicatorColor = TransParent,
                    disabledIndicatorColor = TransParent
                ),
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 16.textDp
                ),
                placeholder = {
                    Text(
                        text = "메시지를 입력하세요.",
                        color = Color_FF43444D,
                        fontSize = 16.textDp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 40.dp)
            )
        }
    }
}