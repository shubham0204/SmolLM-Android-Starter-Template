package io.shubham0204.startwithsmollm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

data class ChatUIState(
    val messages: ImmutableList<ChatMessage> = emptyList<ChatMessage>().toImmutableList(),
    val modelLoadingState: ModelLoadingState = ModelLoadingState.NOT_LOADED
)

sealed interface ChatUIEvent {
    data class SubmitQuery(val query: String): ChatUIEvent
}

class MainActivityViewModel : ViewModel() {

    private val _chatUiStateFlow = MutableStateFlow<ChatUIState>(ChatUIState())
    val chatUiStateFlow: StateFlow<ChatUIState> = _chatUiStateFlow

    init {
        loadModel()
    }

    fun onEvent(event: ChatUIEvent) {
        when (event) {
            is ChatUIEvent.SubmitQuery -> {
                val query = event.query
                _chatUiStateFlow.update {
                    it.copy(messages = it.messages.addChatMessage(
                        ChatMessage(
                            content = query,
                            userRole = UserRole.HUMAN
                        )
                    ))
                }
                viewModelScope.launch {
                    // TODO: Write query processing logic here
                    delay(3000L)
                    _chatUiStateFlow.update {
                        it.copy(messages = it.messages.addChatMessage(
                            ChatMessage(
                                content = query,
                                userRole = UserRole.LLM
                            )
                        ))
                    }
                }
            }
        }
    }

    private fun loadModel() {
        _chatUiStateFlow.update { it.copy(modelLoadingState = ModelLoadingState.LOADING) }
        viewModelScope.launch {
            // TODO: Write model loading logic here
            delay(3000L)
            _chatUiStateFlow.update { it.copy(modelLoadingState = ModelLoadingState.SUCCESS) }
        }
    }

    private fun ImmutableList<ChatMessage>.addChatMessage(chatMessage: ChatMessage): ImmutableList<ChatMessage> {
        val mutableList = this.toMutableList()
        mutableList.add(chatMessage)
        return mutableList.toImmutableList()
    }
}