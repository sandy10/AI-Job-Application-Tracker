package com.sandeep.aijobapplicationtracker.presentation.screens.videointerview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.domain.repository.AiAnalyzerRepository
import com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class InterviewState {
    object Initializing : InterviewState()
    object SetupCamera : InterviewState()
    object WaitingForFace : InterviewState()
    
    // Mic check phase
    object PromptingMicTest : InterviewState()
    data class ListeningToMicTest(val partialAnswer: String = "") : InterviewState()
    object MicTestConfirmed : InterviewState()
    
    data class GeneratingQuestions(val progressText: String) : InterviewState()
    data class AskingQuestion(val question: String, val index: Int, val total: Int) : InterviewState()
    data class ListeningToAnswer(val question: String, val partialAnswer: String) : InterviewState()
    object ProcessingAnswer : InterviewState()
    data class Evaluating(val progressText: String) : InterviewState()
    data class Finished(val feedbackMarkdown: String) : InterviewState()
    data class Error(val message: String) : InterviewState()
}

@HiltViewModel
class VideoInterviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val jobRepository: JobApplicationRepository,
    private val aiRepository: AiAnalyzerRepository
) : ViewModel() {

    private val jobId: String = checkNotNull(savedStateHandle["jobId"])
    
    private val _uiState = MutableStateFlow<InterviewState>(InterviewState.Initializing)
    val uiState: StateFlow<InterviewState> = _uiState

    private var questions = listOf<String>()
    private var currentQuestionIndex = 0
    private val qaPairs = mutableListOf<Pair<String, String>>()
    
    private var jobRole = ""
    
    // For accumulating continuous speech results
    private var accumulatedText = ""
    private var speechTimeoutJob: Job? = null

    init {
        _uiState.value = InterviewState.SetupCamera
    }

    fun onPermissionsGranted() {
        if (_uiState.value is InterviewState.SetupCamera) {
            _uiState.value = InterviewState.WaitingForFace
        }
    }

    fun onFaceDetected(isDetected: Boolean) {
        if (isDetected && _uiState.value is InterviewState.WaitingForFace) {
            _uiState.value = InterviewState.PromptingMicTest
        }
    }

    private fun startInterview() {
        _uiState.value = InterviewState.GeneratingQuestions("Preparing AI Interview...")
        
        viewModelScope.launch {
            try {
                val app = jobRepository.getApplications().first().find { it.id == jobId }
                    ?: throw Exception("Application not found")
                
                jobRole = app.jobTitle
                val resumeContent = "Primary Resume ID: ${app.selectedResumeId}"
                
                val result = aiRepository.generateVideoInterviewQuestions(
                    jobDescription = app.jobDescription,
                    resumeContent = resumeContent
                )
                
                result.onSuccess { generatedQs ->
                    questions = generatedQs
                    if (questions.isEmpty()) {
                        _uiState.value = InterviewState.Error("Failed to generate questions")
                        return@launch
                    }
                    currentQuestionIndex = 0
                    askCurrentQuestion()
                }.onFailure { e ->
                    _uiState.value = InterviewState.Error(e.message ?: "Failed to generate questions")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error starting interview")
                _uiState.value = InterviewState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun askCurrentQuestion() {
        if (currentQuestionIndex < questions.size) {
            _uiState.value = InterviewState.AskingQuestion(
                question = questions[currentQuestionIndex],
                index = currentQuestionIndex + 1,
                total = questions.size
            )
        } else {
            finishInterview()
        }
    }

    private fun resetSpeechTimeout() {
        speechTimeoutJob?.cancel()
        speechTimeoutJob = viewModelScope.launch {
            delay(15000) // STRICT 15 SECONDS WAIT
            val currentState = _uiState.value
            
            if (currentState is InterviewState.ListeningToMicTest) {
                if (currentState.partialAnswer.isBlank()) {
                    _uiState.value = InterviewState.PromptingMicTest
                } else {
                    _uiState.value = InterviewState.MicTestConfirmed
                }
            } else if (currentState is InterviewState.ListeningToAnswer) {
                if (currentState.partialAnswer.isBlank()) {
                    val q = questions.getOrNull(currentQuestionIndex) ?: return@launch
                    _uiState.value = InterviewState.AskingQuestion(
                        question = "Please answer the given question. $q",
                        index = currentQuestionIndex + 1,
                        total = questions.size
                    )
                } else {
                    onAnswerRecorded(currentState.partialAnswer)
                }
            }
        }
    }

    fun onTtsFinished() {
        accumulatedText = ""
        when (_uiState.value) {
            is InterviewState.PromptingMicTest -> {
                _uiState.value = InterviewState.ListeningToMicTest("")
                resetSpeechTimeout()
            }
            is InterviewState.MicTestConfirmed -> {
                startInterview()
            }
            is InterviewState.AskingQuestion -> {
                val q = questions.getOrNull(currentQuestionIndex) ?: return
                _uiState.value = InterviewState.ListeningToAnswer(q, "")
                resetSpeechTimeout()
            }
            else -> {}
        }
    }

    // Called whenever speech recognizer produces a partial transcription
    fun onPartialSpeechResult(partial: String) {
        resetSpeechTimeout()
        val combined = if (accumulatedText.isNotBlank()) "$accumulatedText $partial" else partial
        
        when (val currentState = _uiState.value) {
            is InterviewState.ListeningToAnswer -> {
                _uiState.value = InterviewState.ListeningToAnswer(currentState.question, combined.trim())
            }
            is InterviewState.ListeningToMicTest -> {
                _uiState.value = InterviewState.ListeningToMicTest(combined.trim())
            }
            else -> {}
        }
    }

    // Called when speech recognizer hits a pause and finalizes a chunk of text
    fun onSpeechFinal(finalText: String) {
        resetSpeechTimeout()
        if (finalText.isNotBlank()) {
            accumulatedText = if (accumulatedText.isNotBlank()) "$accumulatedText $finalText" else finalText
        }
        
        when (val currentState = _uiState.value) {
            is InterviewState.ListeningToAnswer -> {
                _uiState.value = InterviewState.ListeningToAnswer(currentState.question, accumulatedText.trim())
            }
            is InterviewState.ListeningToMicTest -> {
                _uiState.value = InterviewState.ListeningToMicTest(accumulatedText.trim())
            }
            else -> {}
        }
    }

    // Manual button click or final timeout
    fun onAnswerRecorded(answer: String) {
        speechTimeoutJob?.cancel()
        
        if (_uiState.value is InterviewState.ListeningToMicTest) {
            if (answer.isNotBlank()) {
                _uiState.value = InterviewState.MicTestConfirmed
            }
            return
        }
        
        if (currentQuestionIndex < questions.size) {
            if (answer.isBlank()) {
                val q = questions.getOrNull(currentQuestionIndex) ?: return
                _uiState.value = InterviewState.AskingQuestion(
                    question = "Please answer the given question. $q",
                    index = currentQuestionIndex + 1,
                    total = questions.size
                )
                return
            }
            
            val q = questions[currentQuestionIndex]
            qaPairs.add(Pair(q, answer))
            
            currentQuestionIndex++
            _uiState.value = InterviewState.ProcessingAnswer
            askCurrentQuestion()
        }
    }
    
    fun onSpeechError(errorMsg: String) {
        // Just reset the timer, since we let the timeout job handle the actual transition.
        // The VoiceHelper will automatically restart the recognizer if it's still in a listening state.
        resetSpeechTimeout()
    }

    private fun finishInterview() {
        _uiState.value = InterviewState.Evaluating("Evaluating your responses...")
        
        viewModelScope.launch {
            val result = aiRepository.evaluateVideoInterview(qaPairs, jobRole)
            result.onSuccess { feedback ->
                _uiState.value = InterviewState.Finished(feedback)
            }.onFailure { e ->
                _uiState.value = InterviewState.Error(e.message ?: "Evaluation failed")
            }
        }
    }
}
