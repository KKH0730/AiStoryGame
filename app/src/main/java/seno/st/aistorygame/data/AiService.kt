package seno.st.aistorygame.data

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import seno.st.aistorygame.model.AiResponse
import seno.st.aistorygame.model.CreateImageResponse

interface AiService {
    @POST("/v1/chat/completions")
    @Headers(value = ["Authorization: Bearer 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"])
    @JvmSuppressWildcards
    suspend fun send(@Body body : Map<String, Any>): AiResponse

    @POST("/v1/images/generations")
    @Headers(value = ["Authorization: Bearer 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"])
    @JvmSuppressWildcards
    suspend fun createImage(@Body body : Map<String, Any>): CreateImageResponse
}