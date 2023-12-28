package seno.st.aistorygame.ui.area_game

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable

const val playerWidth = 20

@SuppressLint("UnrememberedMutableState")
@Composable
fun AreaGameScreen() {
//    val context = LocalContext.current
//    val areaState = rememberAreaState(
//        player1Position = mutableStateOf(100.dpToPx().toFloat() to 100.dpToPx().toFloat()),
//        player2Position = mutableStateOf(screenWidth - 100.dpToPx().toFloat() to screenHeight - 100.dpToPx().toFloat()),
//    )
//
//    LaunchedEffect(key1 = areaState.isMovePlayer1JoyStick.value, key2 = areaState.player1ContinueCount.value) {
//        if (!areaState.isMovePlayer1JoyStick.value) return@LaunchedEffect
//
//        val xPos = areaState.player1Position.value.first + areaState.player1speed.value.first
//        val yPos = areaState.player1Position.value.second + areaState.player1speed.value.second
//        val correction = (playerWidth / 2f).dpToPx()
//
//        if (xPos >= 0 + playerWidth.dpToPx() && xPos < (screenWidth - playerWidth.dpToPx()) && yPos >= 0 && yPos < screenHeight - (playerWidth * 2).dpToPx()) {
//            areaState.drawCanvas.value?.let { canvas ->
//                canvas.addPlayer1Command(
//                    x = xPos + correction,
//                    y = yPos + correction,
//                    penState = if (canvas.player1CommandSize > 0) {
//                        Pen.STATE_MOVE
//                    } else {
//                        Pen.STATE_START
//                    },
//                    penColor = context.getColor(R.color.area_green)
//                )
//                canvas.invalidate()
//                areaState.player1Position.value = xPos to yPos
//            }
//        }
//
//        areaState.player1ContinueCount.value += 1
//    }
//
//    LaunchedEffect(key1 = areaState.isMovePlayer2JoyStick.value, key2 = areaState.player2ContinueCount.value) {
//        if (!areaState.isMovePlayer2JoyStick.value) return@LaunchedEffect
//
//        val xPos = areaState.player2Position.value.first + areaState.player2speed.value.first
//        val yPos = areaState.player2Position.value.second + areaState.player2speed.value.second
//        val correction = (playerWidth / 2f).dpToPx()
//
//        if (xPos >= 0 + playerWidth.dpToPx() && xPos < (screenWidth - playerWidth.dpToPx()) && yPos >= 0 && yPos < screenHeight - (playerWidth * 2).dpToPx()) {
//            areaState.drawCanvas.value?.let { canvas ->
//                canvas.addPlayer2Command(
//                    x = xPos + correction,
//                    y = yPos + correction,
//                    penState = if (canvas.player2CommandSize > 0) {
//                        Pen.STATE_MOVE
//                    } else {
//                        Pen.STATE_START
//                    },
//                    penColor = context.getColor(R.color.area_blue)
//                )
//                canvas.invalidate()
//                areaState.player2Position.value = xPos to yPos
//            }
//        }
//
//        areaState.player2ContinueCount.value += 1
//    }
//
//    Box {
//        GameField(areaState = areaState)
//        JoyStickView(
//            isPlayer1 = true,
//            areaState = areaState,
//            modifier = Modifier
//                .align(alignment = Alignment.BottomStart)
//                .offset(x = joyStickMargin.dp, y = (-joyStickMargin).dp)
//        )
//        JoyStickView(
//            isPlayer1 = false,
//            areaState = areaState,
//            modifier = Modifier
//                .align(alignment = Alignment.TopEnd)
//                .offset(x = (-joyStickMargin).dp, y = (joyStickMargin).dp)
//        )
//        playCharacter(
//            isPlayer1 = true,
//            playerPositionX = areaState.player1Position.value.first,
//            playerPositionY = areaState.player1Position.value.second,
//            areaState = areaState
//        )
//
//        playCharacter(
//            isPlayer1 = false,
//            playerPositionX = areaState.player2Position.value.first,
//            playerPositionY = areaState.player2Position.value.second,
//            areaState = areaState
//        )
//    }
}

//@Composable
//fun GameField(areaState: AreaState) {
//    Box(modifier = Modifier.fillMaxSize()) {
//        Box(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            AndroidView(
//                factory = { context ->
//                    DrawCanvas(context).also {
//                        areaState.drawCanvas.value = it
//                    }
//                },
//                modifier = Modifier.fillMaxSize()
//            )
//        }
//
//        AnimatedVisibility(
//            visible = areaState.isShowResult.value,
//            enter = fadeIn(),
//            exit = fadeOut()
//        ) {
//            AndroidView(
//                factory = { context -> AppCompatImageView(context) },
//                update = { imageView ->
//                    areaState.drawCanvasBitmap.value?.let { imageView.setImageBitmap(it) }
//                },
//                modifier = Modifier.fillMaxSize()
//                    .background(Color.Red)
//            )
//        }
//    }
//}
//
//@Composable
//fun playCharacter(
//    isPlayer1: Boolean,
//    playerPositionX: Float,
//    playerPositionY: Float,
//    areaState: AreaState,
//) {
//    val context = LocalContext.current
//
//    Box(
//        modifier = Modifier
//            .size(size = playerWidth.dp)
//            .graphicsLayer {
//                translationX = playerPositionX
//                translationY = playerPositionY
//            }
//            .background(
//                color = if (isPlayer1) {
//                    AreaGreen
//                } else {
//                    AreaBlue
//                },
//                shape = CircleShape
//            )
//            .clickable {
//                areaState.isShowResult.value = !areaState.isShowResult.value
//                areaState.drawCanvas.value?.let {
//                    val maxScorePair = getMaxScorePair(
//                        context = context,
//                        opencvUtil = areaState.opencvUtil,
//                        bitmap = it.currentCanvas
//                    )
//                    val strokeMat = areaState.opencvUtil.drawBorder(mat = maxScorePair.first)
//                    areaState.drawCanvasBitmap.value = strokeMat.bitmapFrom()
//                }
//            }
//    )
//}
//
//private fun getMaxScorePair(context: Context, opencvUtil: AreaOpencvUtil, bitmap: Bitmap): Pair<Mat, Int> {
//    val greenExtractedPair = opencvUtil.extractColor(
//        bitmap = bitmap,
//        gameColor = GameColor.GREEN,
//        context = context
//    )
//    val redExtractedPair = opencvUtil.extractColor(
//        bitmap = bitmap,
//        gameColor = GameColor.RED,
//        context = context
//    )
//    val blueExtractedPair = opencvUtil.extractColor(
//        bitmap = bitmap,
//        gameColor = GameColor.BLUE,
//        context = context
//    )
//
//    val firstCompareScore = greenExtractedPair.second.coerceAtLeast(redExtractedPair.second)
//
//    return when (firstCompareScore.coerceAtLeast(blueExtractedPair.second)) {
//        greenExtractedPair.second -> greenExtractedPair
//        redExtractedPair.second -> redExtractedPair
//        else -> blueExtractedPair
//    }
//}