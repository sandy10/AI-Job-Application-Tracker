package com.sandeep.aijobapplicationtracker.presentation.screens.applications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository
import kotlinx.coroutines.flow.combine

enum class ApplicationStatus {
    ALL, SAVED, APPLIED, RECRUITER, INTERVIEW, OFFER, REJECTED
}

enum class SortOption { RECENTLY_ADDED, MATCH_SCORE }

data class DetailedJobApplication(
    val id: String,
    val company: String,
    val role: String,
    val location: String,
    val status: ApplicationStatus,
    val dateApplied: String,
    val matchScore: Int,
    val timestamp: Long,
    val workMode: String
)

@HiltViewModel
class ApplicationsListViewModel @Inject constructor(
    private val repository: JobApplicationRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<ImmutableList<DetailedJobApplication>>>(UiState.Loading)
    val uiState: StateFlow<UiState<ImmutableList<DetailedJobApplication>>> = _uiState

    private val _selectedFilter = MutableStateFlow(ApplicationStatus.ALL)
    val selectedFilter: StateFlow<ApplicationStatus> = _selectedFilter

    private val _selectedSortOption = MutableStateFlow(SortOption.RECENTLY_ADDED)
    val selectedSortOption: StateFlow<SortOption> = _selectedSortOption

    private val _selectedWorkMode = MutableStateFlow("All")
    val selectedWorkMode: StateFlow<String> = _selectedWorkMode

    init {
        fetchApplications()
    }

    private fun fetchApplications() {
        viewModelScope.launch {
            combine(
                repository.getApplications(),
                _selectedFilter,
                _selectedSortOption,
                _selectedWorkMode
            ) { applications, filter, sort, workMode ->
                val detailedApps = applications.map { app ->
                    DetailedJobApplication(
                        id = app.id,
                        company = app.company,
                        role = app.jobTitle,
                        location = app.location,
                        status = mapStatus(app.status),
                        dateApplied = app.dateApplied.ifBlank { "-" },
                        matchScore = app.matchScore,
                        timestamp = app.timestamp,
                        workMode = app.workMode
                    )
                }
                
                var filteredList = if (filter == ApplicationStatus.ALL) {
                    detailedApps
                } else {
                    detailedApps.filter { it.status == filter }
                }
                
                if (workMode != "All") {
                    filteredList = filteredList.filter { it.workMode.contains(workMode, ignoreCase = true) }
                }
                
                filteredList = when(sort) {
                    SortOption.RECENTLY_ADDED -> filteredList.sortedByDescending { it.timestamp }
                    SortOption.MATCH_SCORE -> filteredList.sortedByDescending { it.matchScore }
                }
                
                if (filteredList.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(filteredList.toImmutableList())
                }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setFilter(status: ApplicationStatus) {
        _selectedFilter.value = status
    }
    
    fun setSortOption(option: SortOption) {
        _selectedSortOption.value = option
    }
    
    fun setWorkMode(mode: String) {
        _selectedWorkMode.value = mode
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

