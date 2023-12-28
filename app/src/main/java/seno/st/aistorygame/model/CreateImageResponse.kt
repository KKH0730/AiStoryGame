package seno.st.aistorygame.model

import com.google.gson.annotations.SerializedName

data class CreateImageResponse(
    @SerializedName("created")
    val created: Long,
    @SerializedName("data")
    val images: List<AiImage>
)

data class AiImage(
    @SerializedName("url")
    val url: String
)
