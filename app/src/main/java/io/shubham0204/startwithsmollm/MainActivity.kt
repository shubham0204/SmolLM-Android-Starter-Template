package io.shubham0204.startwithsmollm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.shubham0204.startwithsmollm.ui.theme.SmolLMStarterTemplateTheme
import kotlinx.collections.immutable.ImmutableList

class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmolLMStarterTemplateTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        val uiState by viewModel.chatUiStateFlow.collectAsState()
                        ScreenUI(uiState)
                    }
                }
            }
        }
    }

    @Composable
    private fun ScreenUI(uiState: ChatUIState) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ChatMessagesList(uiState.messages)
            MessageInput(
                uiState.modelLoadingState,
                onQuerySubmit = { query ->
                    viewModel.onEvent(ChatUIEvent.SubmitQuery(query))
                }
            )
        }
    }

    @Composable
    private fun ColumnScope.ChatMessagesList(messages: ImmutableList<ChatMessage>) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { message ->
                ChatMessageListItem(message)
            }
        }
    }

    @Composable
    private fun ChatMessageListItem(message: ChatMessage) {
        Text(
            text = message.content,
            modifier = Modifier.padding(8.dp),
            color = when (message.userRole) {
                UserRole.HUMAN -> Color.Black
                UserRole.LLM -> Color.Blue
            }
        )
    }

    @Composable
    private fun MessageInput(
        modelLoadingState: ModelLoadingState,
        onQuerySubmit: (String) -> Unit
    ) {
        var queryText by remember { mutableStateOf("") }
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = queryText,
                onValueChange = { queryText = it },
                modifier = Modifier.weight(1f),
                enabled = modelLoadingState == ModelLoadingState.SUCCESS
            )
            when (modelLoadingState) {
                ModelLoadingState.LOADING -> {
                    CircularProgressIndicator()
                }
                ModelLoadingState.SUCCESS -> {
                    IconButton(
                        onClick = {
                            onQuerySubmit(queryText)
                            queryText = ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send Query"
                        )
                    }
                }
                else -> {
                    // NOT_LOADED and FAILURE cases
                }
            }

        }

    }
}
