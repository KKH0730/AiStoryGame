package seno.st.aistorygame.data

import kotlinx.coroutines.flow.Flow
import seno.st.aistorygame.model.AiResponse
import seno.st.aistorygame.model.CreateImageResponse

interface AiRepository {
    suspend fun send(body: Map<String, Any>): Flow<AiResponse>

    suspend fun createImage(body: Map<String, Any>): Flow<CreateImageResponse>
}