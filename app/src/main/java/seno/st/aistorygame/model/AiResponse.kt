package seno.st.aistorygame.model

import com.google.gson.annotations.SerializedName

data class AiResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("object")
    val obj: String,
    @SerializedName("created")
    val created: Long,
    @SerializedName("model")
    val model: String,
    @SerializedName("choices")
    val choices: List<AiChoice>,
    @SerializedName("usage")
    val usageToken: UsageToken
)

data class UsageToken(
    @SerializedName("prompt_tokens")
    val promptTokens: Int,
    @SerializedName("completion_tokens")
    val completionTokens: Int,
    @SerializedName("total_tokens")
    val totalTokens: Int
)


data class AiChoice(
    @SerializedName("index")
    val index: Int,
    @SerializedName("message")
    val message: AiMessage,
    @SerializedName("finish_reason")
    val finishReason: String
)

data class AiMessage(
    @SerializedName("role")
    val role: String,
    @SerializedName("content")
    val content: String
)