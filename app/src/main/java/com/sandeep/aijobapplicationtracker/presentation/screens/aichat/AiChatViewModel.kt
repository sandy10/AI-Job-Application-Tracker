package com.sandeep.aijobapplicationtracker.presentation.screens.aichat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.domain.repository.AiAnalyzerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.sandeep.aijobapplicationtracker.utils.AnalyticsHelper
import com.sandeep.aijobapplicationtracker.utils.Constants

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean
)

data class AiChatUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            text = "Welcome to AI Interview Preparation! Ask me any technical or behavioral questions to start practicing.",
            isUser = false
        )
    ),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val aiAnalyzerRepository: AiAnalyzerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiChatUiState())
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = ChatMessage(text = text, isUser = true)
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            // Build conversation history for context
            val history = _uiState.value.messages.joinToString("\n") { 
                if (it.isUser) "User: ${it.text}" else "AI: ${it.text}"
            }
            // We append the latest user message directly so no need to append it again here since it's in the history already.
            val fullPrompt = "$history\nAI:"

            val result = aiAnalyzerRepository.sendChatMessage(fullPrompt)
            result.onSuccess { answer ->
                val aiMessage = ChatMessage(text = answer, isUser = false)
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + aiMessage,
                    isLoading = false
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.localizedMessage ?: "Failed to generate answer"
                )
            }
        }
    }
}
