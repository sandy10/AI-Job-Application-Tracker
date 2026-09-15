package com.sandeep.aijobapplicationtracker.presentation.screens.aifollowup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.domain.repository.AiAnalyzerRepository
import com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository
import com.sandeep.aijobapplicationtracker.utils.NetworkMonitor
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.concurrent.TimeUnit
import timber.log.Timber

@HiltViewModel
class AiFollowUpViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val jobRepository: JobApplicationRepository,
    private val aiRepository: AiAnalyzerRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val jobId: String = checkNotNull(savedStateHandle["jobId"])

    private val _uiState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val uiState: StateFlow<UiState<String>> = _uiState

    init {
        generateEmail()
    }

    fun generateEmail() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                if (!networkMonitor.isOnline()) {
                    _uiState.value = UiState.Error("No internet connection")
                    return@launch
                }

                val apps = jobRepository.getApplications().first()
                val app = apps.find { it.id == jobId }
                
                if (app != null) {
                    var daysSinceApplied = 7 // default
                    if (app.dateApplied.isNotBlank()) {
                        try {
                            val format = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                            val date = format.parse(app.dateApplied)
                            if (date != null) {
                                val diff = System.currentTimeMillis() - date.time
                                daysSinceApplied = TimeUnit.MILLISECONDS.toDays(diff).toInt()
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Error parsing dateApplied")
                        }
                    }
                    
                    val result = aiRepository.generateFollowUpEmail(
                        company = app.company,
                        role = app.jobTitle,
                        recruiterName = app.recruiter.takeIf { it.isNotBlank() },
                        daysSinceApplied = daysSinceApplied
                    )
                    
                    if (result.isSuccess) {
                        _uiState.value = UiState.Success(result.getOrNull()!!)
                    } else {
                        _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Failed to generate email")
                    }
                } else {
                    _uiState.value = UiState.Error("Application not found.")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
