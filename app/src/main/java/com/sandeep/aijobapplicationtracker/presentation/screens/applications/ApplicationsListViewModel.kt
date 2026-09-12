package com.sandeep.aijobapplicationtracker.presentation.screens.applications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository
import kotlinx.coroutines.flow.combine

enum class ApplicationStatus {
    ALL, SAVED, APPLIED, RECRUITER, INTERVIEW, OFFER, REJECTED
}

data class DetailedJobApplication(
    val id: String,
    val company: String,
    val role: String,
    val location: String,
    val status: ApplicationStatus,
    val dateApplied: String,
    val matchScore: Int
)

@HiltViewModel
class ApplicationsListViewModel @Inject constructor(
    private val repository: JobApplicationRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<DetailedJobApplication>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<DetailedJobApplication>>> = _uiState

    private val _selectedFilter = MutableStateFlow(ApplicationStatus.ALL)
    val selectedFilter: StateFlow<ApplicationStatus> = _selectedFilter

    init {
        fetchApplications()
    }

    private fun fetchApplications() {
        viewModelScope.launch {
            combine(
                repository.getApplications(),
                _selectedFilter
            ) { applications, filter ->
                val detailedApps = applications.map { app ->
                    DetailedJobApplication(
                        id = app.id,
                        company = app.company,
                        role = app.jobTitle,
                        location = app.location,
                        status = mapStatus(app.status),
                        dateApplied = app.dateApplied.ifBlank { "-" },
                        matchScore = app.matchScore
                    )
                }
                
                val filteredList = if (filter == ApplicationStatus.ALL) {
                    detailedApps
                } else {
                    detailedApps.filter { it.status == filter }
                }
                
                if (filteredList.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(filteredList)
                }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setFilter(status: ApplicationStatus) {
        _selectedFilter.value = status
    }

    private fun mapStatus(status: String): ApplicationStatus {
        val s = status.uppercase()
        return when {
            s.contains("SAVED") -> ApplicationStatus.SAVED
            s.contains("APPLI") -> ApplicationStatus.APPLIED
            s.contains("RECRUITER") -> ApplicationStatus.RECRUITER
            s.contains("INTERVIEW") -> ApplicationStatus.INTERVIEW
            s.contains("OFFER") -> ApplicationStatus.OFFER
            s.contains("REJECT") -> ApplicationStatus.REJECTED
            else -> ApplicationStatus.SAVED
        }
    }
}
