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
class ApplicationsListViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<DetailedJobApplication>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<DetailedJobApplication>>> = _uiState

    private val _selectedFilter = MutableStateFlow(ApplicationStatus.ALL)
    val selectedFilter: StateFlow<ApplicationStatus> = _selectedFilter

    private var allApplications = emptyList<DetailedJobApplication>()

    init {
        fetchApplications()
    }

    private fun fetchApplications() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            // TODO: Fetch from actual repository / Firestore
            delay(1000)
            
            allApplications = listOf(
                DetailedJobApplication("1", "Google", "Senior Android Developer", "Bangalore (Hybrid)", ApplicationStatus.INTERVIEW, "24 Aug 2026", 86),
                DetailedJobApplication("2", "ABC Technologies", "Android Lead", "Remote", ApplicationStatus.APPLIED, "20 Aug 2026", 78),
                DetailedJobApplication("3", "Startup Inc", "Mobile Engineer", "Onsite", ApplicationStatus.SAVED, "-", 92),
                DetailedJobApplication("4", "Tech Corp", "Android Developer", "Hybrid", ApplicationStatus.OFFER, "15 Aug 2026", 85),
                DetailedJobApplication("5", "Old Company", "Software Engineer", "Remote", ApplicationStatus.REJECTED, "10 Aug 2026", 60)
            )
            
            applyFilter(_selectedFilter.value)
        }
    }

    fun setFilter(status: ApplicationStatus) {
        _selectedFilter.value = status
        applyFilter(status)
    }

    private fun applyFilter(status: ApplicationStatus) {
        val filteredList = if (status == ApplicationStatus.ALL) {
            allApplications
        } else {
            allApplications.filter { it.status == status }
        }
        
        if (filteredList.isEmpty()) {
            _uiState.value = UiState.Empty
        } else {
            _uiState.value = UiState.Success(filteredList)
        }
    }
}
