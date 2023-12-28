package seno.st.aistorygame.extension

import kotlinx.coroutines.CoroutineScope

suspend fun <T> CoroutineScope.executeWithDividedBlock(
    startBlock: suspend () -> Unit,
    fetchDataBlock: suspend () -> T,
    endBlock: suspend (T?) -> Unit
) : T? {
    var result: T? = null
    try {
        startBlock.invoke()
        result = fetchDataBlock.invoke()
    } catch (e: Exception) {
        // 예외 처리 로직 작성
    } finally {
        endBlock.invoke(result)
    }
    return result
}