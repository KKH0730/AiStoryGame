package seno.st.aistorygame.ui.story_game.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import seno.st.aistorygame.R
import seno.st.aistorygame.extension.pxToDp
import seno.st.aistorygame.extension.screenWidth
import seno.st.aistorygame.extension.textDp
import seno.st.aistorygame.theme.Color_807950F2
import seno.st.aistorygame.theme.Color_FF2E2F39
import seno.st.aistorygame.theme.Color_FF495057
import seno.st.aistorygame.theme.Color_FF4F505B
import seno.st.aistorygame.theme.Color_FFCBCFD6
import seno.st.aistorygame.theme.White
import seno.st.aistorygame.ui.story_game.model.ImageLoadingStatus
import seno.st.aistorygame.ui.story_game.model.StoryChatModel
import timber.log.Timber

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoryChatContainer(
    aiStoryHistory: List<StoryChatModel>,
    isShowKeyboard: Boolean,
    onClickReGenerate: () -> Unit,
    modifier: Modifier
) {
    val scrollState = rememberLazyListState()

    LaunchedEffect(key1 = aiStoryHistory.size) {

    }

    CompositionLocalProvider( LocalOverscrollConfiguration provides null) {
        LazyColumn(
            state = scrollState,
            verticalArrangement = Arrangement.spacedBy(space = 20.dp),
            contentPadding = PaddingValues(bottom = 20.dp),
            reverseLayout = true,
            modifier = modifier
                .background(color = Color_FF2E2F39)
        ) {
            items(
                count = aiStoryHistory.size,
                key = {
                    when (val model = aiStoryHistory[it]) {
                        is StoryChatModel.TutorialChat -> model.id
                        is StoryChatModel.UserChat -> model.id
                        is StoryChatModel.AssistantChat -> model.id
                        is StoryChatModel.Loading -> model.id
                        is StoryChatModel.StreamError -> model.id
                    }
                },
                contentType = {
                    when (val model = aiStoryHistory[it]) {
                        is StoryChatModel.TutorialChat -> model
                        is StoryChatModel.UserChat -> model
                        is StoryChatModel.AssistantChat -> model
                        is StoryChatModel.Loading -> model
                        is StoryChatModel.StreamError -> model
                    }
                },
                itemContent = {
                    when (val model = aiStoryHistory[it]) {
                        is StoryChatModel.TutorialChat -> { TutorialChatModule(content = model.content, messageSize = aiStoryHistory.size) }
                        is StoryChatModel.UserChat -> { UserChatModule(content = model.content) }
                        is StoryChatModel.AssistantChat -> {
                            AssistantChatModule(
                                content = model.content,
                                images = model.images,
                                isGenImageMode = model.isGenImageMode,
                                imageLoadingStatus = model.imageLoadingStatus
                            )
                        }
                        is StoryChatModel.Loading -> { AssistantLoadingModule() }
                        is StoryChatModel.StreamError -> { StreamErrorModule(onClickReGenerate = onClickReGenerate) }
                    }
                }
            )
        }
    }
}

@Composable
fun TutorialChatModule(
    content: String,
    messageSize: Int
) {
    val tutorialChatModuleHeight by animateDpAsState(
        targetValue = if (messageSize > 1) {
            0.dp
        } else {
            200.dp
        },
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing, delayMillis = 400)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = tutorialChatModuleHeight)
            .animateContentSize()
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        Text(
            text = content,
            textAlign = TextAlign.Center,
            fontSize = 16.textDp,
            color = Color_FFCBCFD6,
            lineHeight = 25.textDp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun AssistantChatModule(
    content: String,
    isGenImageMode: Boolean,
    imageLoadingStatus: ImageLoadingStatus,
    images: List<String>
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        Image(
            painter = painterResource(id = R.drawable.img_story_game_builder_profile),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size = 36.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .widthIn(min = 0.dp, max = (screenWidth.pxToDp() * 0.8).dp)
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color_807950F2
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Column {
                AnimatedVisibility(
                    visible = isGenImageMode,
                    modifier = Modifier.padding(top = 5.dp, bottom = 10.dp)
                ) {
                    when(imageLoadingStatus) {
                        ImageLoadingStatus.Failure -> {
                            Image(
                                painter = painterResource(id = R.drawable.img_no_img),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                colorFilter = ColorFilter.tint(color = Color_FF4F505B),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(ratio = 1.0f)
                            )
                        }
                        ImageLoadingStatus.Loading -> {
                            GenerateImageLoading()
                        }
                        ImageLoadingStatus.Complete -> {
                            GlideImage(
                                imageModel = { images[0] },
                                imageOptions = ImageOptions(
                                    contentScale = ContentScale.Crop,
                                    alignment = Alignment.Center
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(ratio = 1.0f)
                            )
                        }
                    }
                }
                Text(
                    text = content,
                    fontSize = 14.textDp,
                    color = White,
                    lineHeight = 25.textDp,
                    modifier = Modifier
                        .padding(all = 10.dp)
                )
            }
        }
    }
}

@Composable
fun AssistantLoadingModule() {
    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        Image(
            painter = painterResource(id = R.drawable.img_story_game_builder_profile),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size = 36.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .widthIn(min = 0.dp, max = (screenWidth.pxToDp() * 0.8).dp)
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color_807950F2
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            CircularProgressIndicator(
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Square,
                modifier = Modifier
                    .size(40.dp)
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
fun UserChatModule(content: String) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .widthIn(min = 0.dp, max = (screenWidth.pxToDp() * 0.8).dp)
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color_FF495057
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Text(
                text = content,
                fontSize = 14.textDp,
                color = White,
                textAlign = TextAlign.End,
                lineHeight = 25.textDp,
                modifier = Modifier
                    .padding(all = 10.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
    }
}

@Composable
fun GenerateImageLoading() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(
            strokeWidth = 4.dp,
            strokeCap = StrokeCap.Square,
            modifier = Modifier
                .size(40.dp)
                .padding(all = 10.dp)
        )
        Text(
            text = "이미지를 생성 중 입니다...",
            color = White,
            fontSize = 14.textDp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxHeight()
        )
    }
}

@Composable
fun StreamErrorModule(
    onClickReGenerate: () -> Unit
) {
    var currentRotation by remember { mutableFloatStateOf(0f) }
    val rotation = remember { Animatable(currentRotation) }

    LaunchedEffect(key1 = Unit) {
        rotation.animateTo(
            targetValue = currentRotation + 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = FastOutLinearInEasing, delayMillis = 400),
                repeatMode = RepeatMode.Restart,
            )
        ) {
            currentRotation = value
        }
    }

    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        Image(
            painter = painterResource(id = R.drawable.img_story_game_builder_profile),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size = 36.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .widthIn(min = 0.dp, max = (screenWidth.pxToDp() * 0.8).dp)
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color_807950F2
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable(onClick = onClickReGenerate)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,) {
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "오류가 발생했습니다.",
                    color = White,
                    fontSize = 14.textDp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxHeight()
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_reload),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(color = White),
                    modifier = Modifier
                        .size(size = 35.dp)
                        .padding(all = 10.dp)
                        .rotate(degrees = rotation.value)
                )
            }
        }
    }
}