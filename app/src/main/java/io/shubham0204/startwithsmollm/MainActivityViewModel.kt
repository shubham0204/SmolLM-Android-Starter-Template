package io.shubham0204.startwithsmollm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.shubham0204.smollm.SmolLM
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class UserRole {
    HUMAN,
    LLM
}

data class ChatMessage(
    val content: String,
    val userRole: UserRole
)

enum class ModelLoadingState {
    NOT_LOADED,
    LOADING,
    SUCCESS,
    FAILURE
}

enum class ModelInferenceState {
    IDLE,
    LOADING,
    SUCCESS,
    FAILURE
}

data class ChatUIState(
    val messages: ImmutableList<ChatMessage> = emptyList<ChatMessage>().toImmutableList(),
    val modelLoadingState: ModelLoadingState = ModelLoadingState.NOT_LOADED,
    val modelInferenceState: ModelInferenceState = ModelInferenceState.IDLE
)

sealed interface ChatUIEvent {
    data class SubmitQuery(val query: String) : ChatUIEvent
}

class MainActivityViewModel : ViewModel() {

    private val _chatUiStateFlow = MutableStateFlow<ChatUIState>(ChatUIState())
    val chatUiStateFlow: StateFlow<ChatUIState> = _chatUiStateFlow

    // TODO: Step 2
    private val smolLM = SmolLM()
    private val modelPath = "/data/local/tmp/smollm2-360m-instruct-q8_0.gguf"

    init {
        loadModel()
    }

    fun onEvent(event: ChatUIEvent) {
        when (event) {
            is ChatUIEvent.SubmitQuery -> {
                val query = event.query
                // TODO: Step 4
                // smolLM.addUserMessage(query)
                _chatUiStateFlow.update {
                    it.copy(
                        messages = it.messages.addChatMessage(
                            ChatMessage(
                                content = query,
                                userRole = UserRole.HUMAN
                            )
                        ),
                        modelInferenceState = ModelInferenceState.LOADING
                    )
                }
                viewModelScope.launch(Dispatchers.Default) {
                    // TODO: Step 5
                    // val llmResponse = smolLM.getResponse(query)
                    val llmResponse = ""
                    withContext(Dispatchers.Main) {
                        _chatUiStateFlow.update {
                            it.copy(
                                messages = it.messages.addChatMessage(
                                    ChatMessage(
                                        content = llmResponse,
                                        userRole = UserRole.LLM
                                    )
                                ),
                                modelInferenceState = ModelInferenceState.SUCCESS
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadModel() {
        _chatUiStateFlow.update { it.copy(modelLoadingState = ModelLoadingState.LOADING) }
        viewModelScope.launch {
            // TODO: Step 3
//            val reader = GGUFReader()
//            reader.load(modelPath)
//            val chatTemplate = reader.getChatTemplate()
//            val contextSize = reader.getContextSize()
//            smolLM.load(
//                modelPath = modelPath,
//                params = SmolLM.InferenceParams(
//                    minP = 0.01f,
//                    temperature = 1.0f,
//                    storeChats = true,
//                    contextSize = contextSize,
//                    chatTemplate = chatTemplate,
//                    numThreads = 4,
//                    useMmap = true,
//                    useMlock = false
//                )
//            )
//            smolLM.addSystemPrompt("You are a helpful assistant.")
            _chatUiStateFlow.update { it.copy(modelLoadingState = ModelLoadingState.SUCCESS) }
        }
    }

    private fun ImmutableList<ChatMessage>.addChatMessage(chatMessage: ChatMessage): ImmutableList<ChatMessage> {
        val mutableList = this.toMutableList()
        mutableList.add(chatMessage)
        return mutableList.toImmutableList()
    }
}