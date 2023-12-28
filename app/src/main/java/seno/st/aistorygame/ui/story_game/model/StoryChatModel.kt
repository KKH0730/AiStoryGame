package seno.st.aistorygame.ui.story_game.model

sealed class StoryChatModel {
    data class TutorialChat(
        val id: Int,
        val role: String,
        val content: String
    ) : StoryChatModel()

    data class UserChat(
        val id: Int,
        val role: String,
        val content: String
    ) : StoryChatModel()

    data class AssistantChat(
        val id: Int,
        val role: String,
        var content: String,
        var isGenImageMode: Boolean = true,
        val images: List<String> = listOf(),
        var imageLoadingStatus: ImageLoadingStatus = ImageLoadingStatus.Loading
    ) : StoryChatModel()

    data class Loading(
        val id: Int
    ) : StoryChatModel()

    data class StreamError(
        val id: Int
    ) : StoryChatModel()
}

sealed class ImageLoadingStatus {
    object Loading : ImageLoadingStatus()
    object Complete : ImageLoadingStatus()
    object Failure : ImageLoadingStatus()
}