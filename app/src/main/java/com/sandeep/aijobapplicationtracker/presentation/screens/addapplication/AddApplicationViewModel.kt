package com.sandeep.aijobapplicationtracker.presentation.screens.addapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository
import com.sandeep.aijobapplicationtracker.domain.model.JobApplicationModel
import java.util.UUID

import com.sandeep.aijobapplicationtracker.domain.repository.AiAnalyzerRepository
import com.sandeep.aijobapplicationtracker.domain.model.ExtractedJobData

@HiltViewModel
class AddApplicationViewModel @Inject constructor(
    private val repository: JobApplicationRepository,
    private val aiAnalyzerRepository: AiAnalyzerRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    val latestExtractedData: StateFlow<ExtractedJobData?> = aiAnalyzerRepository.latestExtractedData

    fun clearExtractedData() {
        aiAnalyzerRepository.clearExtractedData()
    }

    fun getApplication(id: String) = repository.getApplications().map { list -> list.find { app -> app.id == id } }

    suspend fun getApplicationDirectly(id: String): JobApplicationModel? {
        return repository.getApplications().first().find { it.id == id }
    }

    fun saveApplication(
        existingId: String? = null,
        company: String,
        jobTitle: String,
        jobUrl: String,
        location: String,
        workMode: String,
        source: String,
        status: String,
        dateApplied: String,
        salary: String,
        recruiter: String,
        noticePeriod: String,
        jobDescription: String,
        notes: String,
        matchScore: Int
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            if (company.isBlank() || jobTitle.isBlank()) {
                _uiState.value = UiState.Error("Company and Job Title are required")
                delay(2000)
                _uiState.value = UiState.Idle
                return@launch
            }
            
            val app = JobApplicationModel(
                id = existingId ?: UUID.randomUUID().toString(),
                company = company,
                jobTitle = jobTitle,
                jobUrl = jobUrl,
                location = location,
                workMode = workMode,
                source = source,
                status = status,
                dateApplied = dateApplied,
                salary = salary,
                recruiter = recruiter,
                noticePeriod = noticePeriod,
                jobDescription = jobDescription,
                notes = notes,
                matchScore = matchScore,
                timestamp = System.currentTimeMillis()
            )
            
            repository.saveApplication(app)
            
            delay(500)
            _uiState.value = UiState.Success(Unit)
        }
    }

    fun analyzeWithAI() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            // TODO: Implement actual Gemini AI call and extraction
            delay(2000) // Simulating AI thinking
            
            // In reality, this would transition to the AI Analyzer screen or return extracted data.
            // For now we'll just show an error indicating it's not fully wired yet
            _uiState.value = UiState.Error("AI Analyzer not fully implemented in MVP UI yet")
            delay(2000)
            _uiState.value = UiState.Idle
        }
    }
}
