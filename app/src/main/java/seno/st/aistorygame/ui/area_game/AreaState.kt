package seno.st.aistorygame.ui.area_game

//import android.graphics.Bitmap
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import seno.st.aistorygame.util.AreaOpencvUtil
//import seno.st.aistorygame.view.DrawCanvas
//
//class AreaState(
//    val drawCanvas: MutableState<DrawCanvas?>,
//    val drawCanvasBitmap: MutableState<Bitmap?>,
//    val penColor: MutableState<Int>,
//    val isShowResult: MutableState<Boolean>,
//    val isMovePlayer1JoyStick: MutableState<Boolean>,
//    val player1Position: MutableState<Pair<Float, Float>>,
//    val player1speed: MutableState<Pair<Float, Float>>,
//    val player1ContinueCount: MutableState<Int>,
//    val isMovePlayer2JoyStick: MutableState<Boolean>,
//    val player2Position: MutableState<Pair<Float, Float>>,
//    val player2speed: MutableState<Pair<Float, Float>>,
//    val player2ContinueCount: MutableState<Int>,
//    var bitmap: MutableState<Bitmap?>
//) {
//    val opencvUtil = AreaOpencvUtil()
//}
//
//@Composable
//fun rememberAreaState(
//    drawCanvas: MutableState<DrawCanvas?> = mutableStateOf(null),
//    drawCanvasBitmap: MutableState<Bitmap?> = mutableStateOf(null),
//    penColor: MutableState<Int> = mutableStateOf(-1),
//    isShowResult: MutableState<Boolean> = mutableStateOf(false),
//    isMovePlayer1JoyStick: MutableState<Boolean> = mutableStateOf(false),
//    player1Position: MutableState<Pair<Float, Float>>,
//    player1speed: MutableState<Pair<Float, Float>> = mutableStateOf(0f to 0f),
//    player1ContinueCount: MutableState<Int> = mutableStateOf(0),
//    isMovePlayer2JoyStick: MutableState<Boolean> = mutableStateOf(false),
//    player2Position: MutableState<Pair<Float, Float>>,
//    player2speed: MutableState<Pair<Float, Float>> = mutableStateOf(0f to 0f),
//    player2ContinueCount: MutableState<Int> = mutableStateOf(0),
//    bitmap: MutableState<Bitmap?> = mutableStateOf(null)
//) = remember {
//    AreaState(
//        drawCanvas = drawCanvas,
//        drawCanvasBitmap = drawCanvasBitmap,
//        penColor = penColor,
//        isShowResult = isShowResult,
//        isMovePlayer1JoyStick = isMovePlayer1JoyStick,
//        player1Position = player1Position,
//        player1speed = player1speed,
//        player1ContinueCount = player1ContinueCount,
//        isMovePlayer2JoyStick = isMovePlayer2JoyStick,
//        player2Position = player2Position,
//        player2speed = player2speed,
//        player2ContinueCount = player2ContinueCount,
//        bitmap = bitmap
//    )
//}