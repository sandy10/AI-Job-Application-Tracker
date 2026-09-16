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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository
import com.sandeep.aijobapplicationtracker.domain.model.JobApplicationModel
import java.util.UUID

import com.sandeep.aijobapplicationtracker.domain.repository.AiAnalyzerRepository
import com.sandeep.aijobapplicationtracker.domain.repository.ResumeRepository
import com.sandeep.aijobapplicationtracker.domain.model.ExtractedJobData
import kotlinx.coroutines.flow.firstOrNull

data class AddApplicationFormState(
    val company: String = "",
    val jobTitle: String = "",
    val jobUrl: String = "",
    val location: String = "",
    val workMode: String = "",
    val source: String = "",
    val status: String = "Saved for later",
    val dateApplied: String = "",
    val salary: String = "",
    val recruiter: String = "",
    val noticePeriod: String = "",
    val jobDescription: String = "",
    val notes: String = "",
    val matchScore: Int = 0
)

sealed class AddApplicationEvent {
    data class CompanyChanged(val company: String) : AddApplicationEvent()
    data class JobTitleChanged(val title: String) : AddApplicationEvent()
    data class JobUrlChanged(val url: String) : AddApplicationEvent()
    data class LocationChanged(val location: String) : AddApplicationEvent()
    data class WorkModeChanged(val mode: String) : AddApplicationEvent()
    data class SourceChanged(val source: String) : AddApplicationEvent()
    data class StatusChanged(val status: String) : AddApplicationEvent()
    data class DateAppliedChanged(val date: String) : AddApplicationEvent()
    data class SalaryChanged(val salary: String) : AddApplicationEvent()
    data class RecruiterChanged(val recruiter: String) : AddApplicationEvent()
    data class NoticePeriodChanged(val period: String) : AddApplicationEvent()
    data class JobDescriptionChanged(val desc: String) : AddApplicationEvent()
    data class NotesChanged(val notes: String) : AddApplicationEvent()
    data class MatchScoreChanged(val score: Int) : AddApplicationEvent()
    data class SaveClicked(val existingId: String?) : AddApplicationEvent()
    data class LoadApplication(val id: String) : AddApplicationEvent()
    data class ExtractedDataReceived(val data: ExtractedJobData) : AddApplicationEvent()
    object ClearError : AddApplicationEvent()
}

@HiltViewModel
class AddApplicationViewModel @Inject constructor(
    private val repository: JobApplicationRepository,
    private val aiAnalyzerRepository: AiAnalyzerRepository,
    private val resumeRepository: ResumeRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    val latestExtractedData: StateFlow<ExtractedJobData?> = aiAnalyzerRepository.latestExtractedData

    private val _formState = MutableStateFlow(AddApplicationFormState())
    val formState: StateFlow<AddApplicationFormState> = _formState

    fun onEvent(event: AddApplicationEvent) {
        when (event) {
            is AddApplicationEvent.CompanyChanged -> _formState.update { it.copy(company = event.company) }
            is AddApplicationEvent.JobTitleChanged -> _formState.update { it.copy(jobTitle = event.title) }
            is AddApplicationEvent.JobUrlChanged -> _formState.update { it.copy(jobUrl = event.url) }
            is AddApplicationEvent.LocationChanged -> _formState.update { it.copy(location = event.location) }
            is AddApplicationEvent.WorkModeChanged -> _formState.update { it.copy(workMode = event.mode) }
            is AddApplicationEvent.SourceChanged -> _formState.update { it.copy(source = event.source) }
            is AddApplicationEvent.StatusChanged -> _formState.update { it.copy(status = event.status) }
            is AddApplicationEvent.DateAppliedChanged -> _formState.update { it.copy(dateApplied = event.date) }
            is AddApplicationEvent.SalaryChanged -> _formState.update { it.copy(salary = event.salary) }
            is AddApplicationEvent.RecruiterChanged -> _formState.update { it.copy(recruiter = event.recruiter) }
            is AddApplicationEvent.NoticePeriodChanged -> _formState.update { it.copy(noticePeriod = event.period) }
            is AddApplicationEvent.JobDescriptionChanged -> _formState.update { it.copy(jobDescription = event.desc) }
            is AddApplicationEvent.NotesChanged -> _formState.update { it.copy(notes = event.notes) }
            is AddApplicationEvent.MatchScoreChanged -> _formState.update { it.copy(matchScore = event.score) }
            is AddApplicationEvent.SaveClicked -> saveApplication(event.existingId)
            is AddApplicationEvent.LoadApplication -> loadApplication(event.id)
            is AddApplicationEvent.ClearError -> clearError()
            is AddApplicationEvent.ExtractedDataReceived -> handleExtractedData(event.data)
        }
    }

    private fun loadApplication(id: String) {
        viewModelScope.launch {
            val app = repository.getApplications().first().find { it.id == id }
            if (app != null) {
                _formState.value = AddApplicationFormState(
                    company = app.company,
                    jobTitle = app.jobTitle,
                    jobUrl = app.jobUrl,
                    location = app.location,
                    workMode = app.workMode,
                    source = app.source,
                    status = app.status,
                    dateApplied = app.dateApplied,
                    salary = app.salary,
                    recruiter = app.recruiter,
                    noticePeriod = app.noticePeriod,
                    jobDescription = app.jobDescription,
                    notes = app.notes,
                    matchScore = app.matchScore
                )
            }
        }
    }

    private fun handleExtractedData(data: ExtractedJobData) {
        _formState.update { state ->
            state.copy(
                company = data.company,
                jobTitle = data.role,
                location = data.location,
                workMode = data.workMode,
                salary = data.salaryRange,
                noticePeriod = data.noticePeriod,
                jobDescription = data.jobDescription,
                jobUrl = data.jobUrl,
                source = data.source,
                recruiter = data.recruiter,
                dateApplied = if (data.dateApplied.isNotBlank()) data.dateApplied else state.dateApplied,
                matchScore = data.matchScore,
                notes = if (data.skills.isNotEmpty()) {
                    "AI Extracted Skills:\n${data.skills.joinToString(", ")}\n\n" + state.notes
                } else state.notes
            )
        }
    }

    fun clearExtractedData() {
        aiAnalyzerRepository.clearExtractedData()
    }

    private fun clearError() {
        if (_uiState.value is UiState.Error) {
            _uiState.value = UiState.Idle
        }
    }

    private fun saveApplication(existingId: String?) {
        val state = _formState.value
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            if (state.company.isBlank() || state.jobTitle.isBlank()) {
                _uiState.value = UiState.Error("Company and Job Title are required")
                return@launch
            }
            
            val existingApp = existingId?.let { repository.getApplications().first().find { app -> app.id == it } }
            val resumes = resumeRepository.getResumes().firstOrNull() ?: emptyList()
            val primaryResumeId = resumes.find { it.isPrimary }?.id ?: resumes.firstOrNull()?.id ?: ""
            val resumeIdToSave = existingApp?.selectedResumeId?.takeIf { it.isNotBlank() } ?: primaryResumeId
            
            val app = JobApplicationModel(
                id = existingId ?: UUID.randomUUID().toString(),
                company = state.company,
                jobTitle = state.jobTitle,
                jobUrl = state.jobUrl,
                location = state.location,
                workMode = state.workMode,
                source = state.source,
                status = state.status,
                dateApplied = state.dateApplied,
                salary = state.salary,
                recruiter = state.recruiter,
                noticePeriod = state.noticePeriod,
                jobDescription = state.jobDescription,
                notes = state.notes,
                matchScore = state.matchScore,
                selectedResumeId = resumeIdToSave,
                timestamp = existingApp?.timestamp ?: System.currentTimeMillis(),
                interviews = existingApp?.interviews ?: emptyList()
            )
            
            val result = repository.saveApplication(app)
            
            if (result.isSuccess) {
                _uiState.value = UiState.Success(Unit)
            } else {
                _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Failed to save application")
            }
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
