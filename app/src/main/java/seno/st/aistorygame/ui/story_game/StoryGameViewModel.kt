package seno.st.aistorygame.ui.story_game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import seno.st.aistorygame.App
import seno.st.aistorygame.R
import seno.st.aistorygame.data.AiRepositoryImpl
import seno.st.aistorygame.extension.executeWithDividedBlock
import seno.st.aistorygame.extension.isNotNullAndNotEmpty
import seno.st.aistorygame.ui.story_game.model.ImageLoadingStatus
import seno.st.aistorygame.ui.story_game.model.StoryChatModel
import timber.log.Timber
import javax.inject.Inject

//const val SYSTEM_INSTRUCTIONS = "You are a story game builder. Your job is to generate stories and choices that fit the theme and mood of the story presented by the user. The first time the user enters a topic, you generate a story and choices. Then you generate the next story and choices that match the answers the user chooses. After the cycle of story creation and user choices is repeated five times, you write the ending of the story and wrap up the story. It is important to note that you must speak in Korean.\u2028\u2028\u2028Here's what you need to do\n" +
//        "1.Always create a choice or question along with the story creation.\u20282. show how many choices are left at the end of each story.\u20283.Always speak in Korean.\n" +
//        "4. use 700 or fewer tokens for the story and choices you create."
const val SYSTEM_INSTRUCTIONS = "당신은 스토리 게임 빌더입니다. 당신의 임무는 사용자가 제시하는 주제와 분위기에 맞는 창의적인 스토리와 재미있는 선택지를 생성하는 것입니다. 사용자가 처음 주제를 입력하면 당신은 스토리와 선택지를 생성하고 대화를 멈춥니다. 그런 다음 사용자가 선택지에서 선택하면, 당신은 선택된 내용을 활용하여 다음 스토리와 선택지를 생성하고 대화를 멈춥니다. 당신이 스토리를 총 5번 생성하면 6번째 생성 시 스토리의 엔딩을 작성하고 대화를 끝냅니다.\n" +
        "\n" +
        " 당신이 명심해야 할 사항은 다음과 같습니다.\n" +
        "1. 스토리는 반드시 1000자 이하로 작성해야한다.\n" +
        "2. 엔딩을 제외한 스토리가 생성할 때마다 선택지도 반드시 생성한다.\n" +
        "3. 엔딩을 제외한 스토리가 생성할 때마다 생성된 텍스트 마지막에 엔딩까지 남은 횟수를 “엔딩까지 남은 횟수: %d” 형식으로 보여준다.\n" +
        "4. 하나의 스토리를 만들고 그에 대한 선택지를 생성한 후에 대화를 멈춥니다.\n" +
        "6. 스토리를 디테일하게 작성한다."

//const val ROLE_SYSTEM = "system"
//const val ROLE_ASSISTANT = "assistant"


const val ROLE_ASSISTANT = "model"
const val ROLE_USER = "user"
const val ROLE_TUTORIAL = "tutorial"
const val MAX_TOKENS = 2000

@HiltViewModel
class StoryGameViewModel @Inject constructor(
    private val aiRepository: AiRepositoryImpl
) : ViewModel() {

    private val _storyList = MutableStateFlow<List<StoryChatModel>>(
        listOf(
            StoryChatModel.TutorialChat(id = 0, role = ROLE_TUTORIAL, content = App.getInstance().getString(R.string.story_game_tutorial_chat))
        )
    )
    val storyList: StateFlow<List<StoryChatModel>> get() = _storyList

    private val aiStoryHistory = arrayListOf(
        StoryChatModel.UserChat(id = 0, role = ROLE_USER, content = SYSTEM_INSTRUCTIONS),
        StoryChatModel.AssistantChat(id = 1, role = ROLE_ASSISTANT, content = "Sure, Let me know what you want to talk about.")
    )
    private var isCanSendMessage = true

    //키보드 visible 상태
    private val _isKeyboardVisible = MutableStateFlow(false)
    val isKeyboardVisible: StateFlow<Boolean> get() = _isKeyboardVisible.asStateFlow()

    private val generativeModel: GenerativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-pro",
            apiKey = "AIzaSyBg-3wmKQ8tGipTTJegLYLKSOXCbQh9jWM",
            safetySettings = listOf(
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.NONE),
                SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE),
                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.NONE),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.NONE)
            ),
            generationConfig = generationConfig {
                maxOutputTokens = 1500
                temperature = 1f
                stopSequences = listOf("stop")
            }
        )
    }

    fun updateKeyboardState(isVisible: Boolean) {
        _isKeyboardVisible.value = isVisible
    }


    fun send(content: String) {
        if (!isCanSendMessage) return
        val lastChat = aiStoryHistory.lastOrNull()
        if (lastChat != null && lastChat is StoryChatModel.UserChat) {
            return
        }

        viewModelScope.launch(
            SupervisorJob() + CoroutineExceptionHandler { _, throwable ->
                isCanSendMessage = true
                removeLoading()
                throwable.printStackTrace()
                Timber.e(throwable)
            }
        ) {
            launch {
                executeWithDividedBlock(
                    startBlock = {
                        isCanSendMessage = false
                        _storyList.value = _storyList.value.toMutableList().apply {
                            add(StoryChatModel.UserChat(id = this.size, role = ROLE_USER, content = content))
                            add(StoryChatModel.Loading(id = this.size))
                        }.toList()
                    },
                    fetchDataBlock = {
                        val assistantAnswer = StringBuilder()
                        var assistantIndex = -1

                        generativeModel.startChat(
                            history = aiStoryHistory.map {
                                if (it is StoryChatModel.UserChat) {
                                    content(role = it.role) { text(it.content) }
                                } else {
                                    val assistantChat = it as StoryChatModel.AssistantChat
                                    content(role = assistantChat.role) { text(assistantChat.content) }
                                }
                            }
                        ).sendMessageStream(content)
                            .flowOn(Dispatchers.IO)
                            .catch {
                                Timber.e("kkhdev stream error : ${it.message}")
                                removeLoading()
                                isCanSendMessage = true
                            }
                            .collectLatest { response ->
                                response.text
                                    ?.let { text ->
                                        assistantAnswer.append(text)

                                        _storyList.value = _storyList.value.toMutableList().apply {
                                            when(val lastStory = lastOrNull()) {
                                                is StoryChatModel.AssistantChat -> {
                                                    set(lastIndex, lastStory.copy(content = assistantAnswer.toString()))
                                                }
                                                is StoryChatModel.Loading -> {
                                                    removeLastOrNull()

                                                    add(StoryChatModel.AssistantChat(id = size, role = ROLE_ASSISTANT, content = assistantAnswer.toString()))
                                                    assistantIndex = lastIndex
                                                }
                                                else -> {}

                                            }
                                        }.toList()
                                    }
                            }
                        assistantAnswer.toString() to assistantIndex
                    },
                    endBlock = { pair ->
                        val assistantAnswer = pair?.first
                        val assistantIndex = pair?.second

                        assistantAnswer
                            ?.takeIf { it.isNotEmpty() }
                            ?.run {
                                aiStoryHistory.add(StoryChatModel.UserChat(id = aiStoryHistory.size, role = ROLE_USER, content = content))
                                aiStoryHistory.add(StoryChatModel.AssistantChat(id = aiStoryHistory.size, role = ROLE_ASSISTANT, content = this))
                            }

                        assistantIndex
                            ?.takeIf { it != -1 }
                            ?.run { createImageWithTest(index = this, assistantAnswer = assistantAnswer.toString()) }

                        isCanSendMessage = true
                    }
                )
            }
        }
    }

    private fun removeLoading() {
        _storyList.value = _storyList.value.toMutableList().apply {
            val lastStory = lastOrNull()
            if (lastStory is StoryChatModel.Loading) {
                removeLastOrNull()
            }
        }
    }

    private suspend fun createImage(index: Int, assistantAnswer: String) {
        withContext(viewModelScope.coroutineContext) {
            aiRepository.createImage(
                body = mapOf(
                    "model" to "dall-e-3",
                    "prompt" to "다음 스토리와 어울리는 실사 이미지 만들어줘. \n$assistantAnswer",
                    "n" to 1,
                    "size" to "1024x1024",
                    "style" to "vivid"
                )
            )
                .flowOn(Dispatchers.IO)
                .catch {
                    _storyList.value = _storyList.value.toMutableList().apply {
                        if (lastIndex >= index) {
                            val model = get(index)
                            if (model is StoryChatModel.AssistantChat) {
                                set(index, model.copy(imageLoadingStatus = ImageLoadingStatus.Failure))
                            }
                        }
                    }.toList()
                }
                .collectLatest { response ->
                    _storyList.value = _storyList.value.toMutableList().apply {
                        if (lastIndex >= index) {
                            val model = get(index)
                            if (model is StoryChatModel.AssistantChat) {
                                set(index, model.copy(images = response.images.map { it.url }, imageLoadingStatus = ImageLoadingStatus.Complete))
                            }
                        }
                    }.toList()
                }
        }
    }

    private suspend fun createImageWithTest(index: Int, assistantAnswer: String) {
        withContext(viewModelScope.coroutineContext) {
            delay(5000)

            _storyList.value = _storyList.value.toMutableList().apply {
                if (lastIndex >= index) {
                    val model = get(index)
                    if (model is StoryChatModel.AssistantChat) {
                        set(index, model.copy(images = listOf("https://media.4-paws.org/5/b/8/b/5b8bca3f74342210ccca652c651f2d7e23288753/VIER%20PFOTEN_2019-12-13_209-2890x2000.jpg"), imageLoadingStatus = ImageLoadingStatus.Complete))
                    }
                }
            }.toList()
        }
    }
}