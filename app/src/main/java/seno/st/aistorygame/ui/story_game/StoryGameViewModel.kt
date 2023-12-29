package seno.st.aistorygame.ui.story_game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.JsonArray
import com.google.gson.JsonObject
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import seno.st.aistorygame.App
import seno.st.aistorygame.BuildConfig
import seno.st.aistorygame.R
import seno.st.aistorygame.data.AiRepositoryImpl
import seno.st.aistorygame.extension.executeWithDividedBlock
import seno.st.aistorygame.extension.isNotNullAndNotEmpty
import seno.st.aistorygame.ui.story_game.model.ImageLoadingStatus
import seno.st.aistorygame.ui.story_game.model.StoryChatModel
import timber.log.Timber
import javax.inject.Inject


const val SYSTEM_INSTRUCTIONS = "당신은 스토리 게임 빌더입니다. 당신의 임무는 사용자가 제시하는 주제에 대한 갈등이 포함된 스토리를 생성하고, 마지막 스토리와 연결되는 주관식이나 객관식을 둘 중에 하나를 랜덤으로 생성하는 것입니다. 사용자가 처음 주제를 입력하면 당신은 스토리와 주관식이나 객관식주관식이나 객관식을 생성하고 대화를 멈춥니다. 그런 다음 사용자가 답변을 하면, 당신은 사용자의 답변을 활용하여 다음 스토리와 주관식이나 객관식을 생성하고 대화를 멈춥니다. 당신이 스토리를 총 5번 생성하면 6번째 생성 시 스토리의 엔딩을 작성하고 대화를 끝냅니다.\n" +
        "\n" +
        "당신이 명심해야 할 사항은 다음과 같습니다.\n" +
        "1. 당신은 반드시 스토리를 한국어(한글)로 생성한다.\n" +
        "2. 당신은 반드시 스토리를 1000자 이내로 작성한다.\n" +
        "3. 주관식이나 객관식 질문은 반드시 스토리의 갈등에 대해 주인공이 어떻게 문제를 해결할지 에 대한 질문이다.\n" +
        "4. 엔딩을 제외한 스토리가 생성할 때마다 다음 스토리의 대한 주관식이나 객관식 둘 중에 하나를 랜덤으로 반드시 생성한다. 주관식의 형식은 다음과 같습니다. \"**주관식** \n주관식 내용\". 객관식의 형식은 다음과 같습니다. \"**객관식** \n1. 첫번째 선택지\n.2. 2번째 선택지\n3. 3번째 선택지.\n" +
        "5. 엔딩을 제외한 스토리가 생성할 때마다 생성된 텍스트 마지막에 엔딩까지 남은 횟수를 “엔딩까지 남은 횟수: %d” 형식으로 보여준다.\n" +
        "6. 당신은 반드시 하나의 스토리를 만들고 그에 대한 선택지를 생성한 후에 대화를 멈춘다.\n" +
        "7. 당신은 반드시 전후 상황이 명확하고 개연성있는 스토리를 작성한다."


const val ROLE_ASSISTANT = "model"
const val ROLE_USER = "user"
const val ROLE_TUTORIAL = "tutorial"

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
            apiKey = BuildConfig.GEMINI_API_KEY,
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


    fun send(content: String = "", isReGenerate: Boolean = false) {
        var prompt = content

        if (!isCanSendMessage) return

        aiStoryHistory.firstOrNull()
            .takeIf { lastChat -> lastChat != null && lastChat is StoryChatModel.UserChat }
            ?: return

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

                        if (isReGenerate)  {
                            removeStreamError()

                            _storyList.value.firstOrNull()
                                ?.takeIf { lastChat -> lastChat is StoryChatModel.UserChat }
                                ?.run {
                                    val lastUserChat = this as StoryChatModel.UserChat
                                    prompt = lastUserChat.content
                                }?: return@executeWithDividedBlock
                        }

                        _storyList.value = _storyList.value.toMutableList().apply {
                            if (!isReGenerate) {
                                add(0, StoryChatModel.UserChat(id = this.size, role = ROLE_USER, content = prompt))
                            }
                            add(0, StoryChatModel.Loading(id = this.size))
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
                        )
                            .sendMessageStream(prompt = prompt)
                            .flowOn(Dispatchers.IO)
                            .catch {
                                removeLoading()
                                removeLastAssistantChat()
                                addStreamError()
                                isCanSendMessage = true
                            }
                            .collectLatest { response ->
                                response.text
                                    ?.let { text -> assistantAnswer.append(text) }
                                    ?.run { removeLoading() }
                                    ?.also {
                                        _storyList.value = _storyList.value.toMutableList().apply {
                                            when(val lastStory = firstOrNull()) {
                                                is StoryChatModel.AssistantChat -> {
                                                    set(0, lastStory.copy(content = assistantAnswer.toString()))
                                                }
                                                is StoryChatModel.UserChat -> {
                                                    add(0, StoryChatModel.AssistantChat(id = size, role = ROLE_ASSISTANT, content = assistantAnswer.toString()))
                                                    assistantIndex = 0
                                                }
                                                else -> {}

                                            }
                                        }.toList()
                                    }
                            }
                        Timber.e("kkhdev result : $assistantAnswer")
                        assistantAnswer.toString() to assistantIndex
                    },
                    endBlock = { pair ->
                        val assistantAnswer = pair?.first
                        val assistantIndex = pair?.second

                        assistantAnswer
                            ?.takeIf { it.isNotEmpty() }
                            ?.run {
                                aiStoryHistory.add(StoryChatModel.UserChat(id = aiStoryHistory.size, role = ROLE_USER, content = prompt))
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
            val lastStory = firstOrNull()
            if (lastStory is StoryChatModel.Loading) {
                removeFirstOrNull()
            }
        }
    }

    private fun removeLastAssistantChat() {
        _storyList.value = _storyList.value.toMutableList().apply {
            val lastStory = firstOrNull()
            if (lastStory is StoryChatModel.AssistantChat) {
                removeFirstOrNull()
            }
        }
    }

    private fun addStreamError() {
        _storyList.value = _storyList.value.toMutableList().apply {
            add(0, StoryChatModel.StreamError(id = this.size))
        }
    }

    private fun removeStreamError() {
        _storyList.value = _storyList.value.toMutableList().apply {
            val lastStory = firstOrNull()
            if (lastStory is StoryChatModel.StreamError) {
                removeFirstOrNull()
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
                    Timber.e(it)
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