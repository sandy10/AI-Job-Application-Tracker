package com.sandeep.aijobapplicationtracker.presentation.screens.aiinterviewprep

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.utils.UiState
import com.sandeep.aijobapplicationtracker.utils.AnalyticsHelper
import com.sandeep.aijobapplicationtracker.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FocusArea(
    val topic: String,
    val priority: String // e.g. "High priority", "Medium priority"
)

data class QuestionAnswer(
    val question: String,
    val answerHint: String
)

data class InterviewPlan(
    val company: String,
    val role: String,
    val strategyTip: String,
    val focusAreas: List<FocusArea>,
    val likelyQuestions: List<QuestionAnswer>,
    val behavioralQuestions: List<QuestionAnswer>
) {
    fun toJsonString(): String {
        val obj = org.json.JSONObject()
        obj.put("company", company)
        obj.put("role", role)
        obj.put("strategyTip", strategyTip)

        val faArray = org.json.JSONArray()
        focusAreas.forEach {
            val faObj = org.json.JSONObject()
            faObj.put("topic", it.topic)
            faObj.put("priority", it.priority)
            faArray.put(faObj)
        }
        obj.put("focusAreas", faArray)

        val lqArray = org.json.JSONArray()
        likelyQuestions.forEach {
            val lqObj = org.json.JSONObject()
            lqObj.put("question", it.question)
            lqObj.put("answerHint", it.answerHint)
            lqArray.put(lqObj)
        }
        obj.put("likelyQuestions", lqArray)

        val bqArray = org.json.JSONArray()
        behavioralQuestions.forEach {
            val bqObj = org.json.JSONObject()
            bqObj.put("question", it.question)
            bqObj.put("answerHint", it.answerHint)
            bqArray.put(bqObj)
        }
        obj.put("behavioralQuestions", bqArray)

        return obj.toString()
    }

    companion object {
        fun fromJsonString(json: String): InterviewPlan? {
            if (json.isBlank()) return null
            try {
                val obj = org.json.JSONObject(json)
                
                val focusAreas = mutableListOf<FocusArea>()
                val faArray = obj.optJSONArray("focusAreas")
                if (faArray != null) {
                    for (i in 0 until faArray.length()) {
                        val faObj = faArray.getJSONObject(i)
                        focusAreas.add(
                            FocusArea(
                                topic = faObj.optString("topic", ""),
                                priority = faObj.optString("priority", "")
                            )
                        )
                    }
                }

                val likelyQuestions = mutableListOf<QuestionAnswer>()
                val lqArray = obj.optJSONArray("likelyQuestions")
                if (lqArray != null) {
                    for (i in 0 until lqArray.length()) {
                        val lqObj = lqArray.getJSONObject(i)
                        likelyQuestions.add(
                            QuestionAnswer(
                                question = lqObj.optString("question", ""),
                                answerHint = lqObj.optString("answerHint", "")
                            )
                        )
                    }
                }

                val behavioralQuestions = mutableListOf<QuestionAnswer>()
                val bqArray = obj.optJSONArray("behavioralQuestions")
                if (bqArray != null) {
                    for (i in 0 until bqArray.length()) {
                        val bqObj = bqArray.getJSONObject(i)
                        behavioralQuestions.add(
                            QuestionAnswer(
                                question = bqObj.optString("question", ""),
                                answerHint = bqObj.optString("answerHint", "")
                            )
                        )
                    }
                }

                return InterviewPlan(
                    company = obj.optString("company", ""),
                    role = obj.optString("role", ""),
                    strategyTip = obj.optString("strategyTip", ""),
                    focusAreas = focusAreas,
                    likelyQuestions = likelyQuestions,
                    behavioralQuestions = behavioralQuestions
                )
            } catch (e: Exception) {
                return null
            }
        }
    }
}

@HiltViewModel
class AiInterviewPrepViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val jobRepository: com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository,
    private val aiRepository: com.sandeep.aijobapplicationtracker.domain.repository.AiAnalyzerRepository,
    private val networkMonitor: com.sandeep.aijobapplicationtracker.utils.NetworkMonitor,
    private val analyticsHelper: AnalyticsHelper
) : ViewModel() {

    private val jobId: String = savedStateHandle.get<String>("jobId") ?: ""

    private val _uiState = MutableStateFlow<UiState<InterviewPlan>>(UiState.Loading)
    val uiState: StateFlow<UiState<InterviewPlan>> = _uiState

    init {
        generatePrepPlan()
    }

    private val _isGeneratingMore = MutableStateFlow(false)
    val isGeneratingMore: StateFlow<Boolean> = _isGeneratingMore

    fun generatePrepPlan(forceRegenerate: Boolean = false) {
        analyticsHelper.trackAiFeatureUsed(Constants.Analytics.FEATURE_INTERVIEW_PREP, jobId)
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val apps = jobRepository.getApplications().first()
                val app = apps.find { it.id == jobId }
                
                if (app != null) {
                    if (!forceRegenerate && app.aiInterviewPlanJson.isNotBlank()) {
                        val cachedPlan = InterviewPlan.fromJsonString(app.aiInterviewPlanJson)
                        if (cachedPlan != null) {
                            _uiState.value = UiState.Success(cachedPlan)
                            return@launch
                        }
                    }

                    if (!networkMonitor.isOnline()) {
                        _uiState.value = UiState.Error("No internet connection")
                        return@launch
                    }

                    val result = aiRepository.generateInterviewPlan(
                        jobDescription = app.jobDescription,
                        role = app.jobTitle,
                        company = app.company
                    )
                    
                    if (result.isSuccess) {
                        val plan = result.getOrNull()!!
                        _uiState.value = UiState.Success(plan)
                        
                        // Save back to repository
                        val updatedApp = app.copy(aiInterviewPlanJson = plan.toJsonString())
                        jobRepository.saveApplication(updatedApp)
                    } else {
                        _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Failed to generate plan.")
                    }
                } else {
                    _uiState.value = UiState.Error("Application not found.")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun generateMoreQuestions() {
        val currentState = _uiState.value
        if (currentState is UiState.Success && !_isGeneratingMore.value) {
            viewModelScope.launch {
                val currentPlan = currentState.data
                _isGeneratingMore.value = true
                
                try {
                    val result = aiRepository.generateMoreQuestions(
                        role = currentPlan.role,
                        existingQuestions = currentPlan.likelyQuestions.map { it.question }
                    )
                    
                    if (result.isSuccess) {
                        val newQuestions = currentPlan.likelyQuestions.toMutableList()
                        newQuestions.addAll(result.getOrNull() ?: emptyList())
                        _uiState.value = UiState.Success(currentPlan.copy(likelyQuestions = newQuestions))
                    }
                } catch (e: Exception) {
                    // Ignore or handle
                } finally {
                    _isGeneratingMore.value = false
                }
            }
        }
    }
}
