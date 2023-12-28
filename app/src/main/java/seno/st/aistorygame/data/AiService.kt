package seno.st.aistorygame.data

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import seno.st.aistorygame.BuildConfig
import seno.st.aistorygame.model.AiResponse
import seno.st.aistorygame.model.CreateImageResponse

interface AiService {
    @POST("/v1/chat/completions")
    @Headers(value = ["Authorization: Bearer ${BuildConfig.OPEN_AI_API_KEY}"])
    @JvmSuppressWildcards
    suspend fun send(@Body body : Map<String, Any>): AiResponse

    @POST("/v1/images/generations")
    @Headers(value = ["Authorization: Bearer ${BuildConfig.OPEN_AI_API_KEY}"])
    @JvmSuppressWildcards
    suspend fun createImage(@Body body : Map<String, Any>): CreateImageResponse
}