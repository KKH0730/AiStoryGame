package seno.st.aistorygame.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import seno.st.aistorygame.model.AiResponse
import seno.st.aistorygame.model.CreateImageResponse
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val aiService: AiService
) : AiRepository {
    override suspend fun send(body: Map<String, Any>): Flow<AiResponse> {
        return flow {
            emit(aiService.send(body))
        }
    }

    override suspend fun createImage(body: Map<String, Any>): Flow<CreateImageResponse> {
        return flow {
            emit(aiService.createImage(body))
        }
    }
}