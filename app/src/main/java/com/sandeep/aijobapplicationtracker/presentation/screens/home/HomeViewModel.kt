package com.sandeep.aijobapplicationtracker.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AttentionType {
    URGENT, UPCOMING, INFO
}

data class AttentionItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val type: AttentionType
)

data class JobApplicationItem(
    val id: String,
    val role: String,
    val company: String,
    val status: String,
    val matchScore: Int
)

data class HomeData(
    val userName: String,
    val totalApplications: Int,
    val totalInterviews: Int,
    val totalFollowUps: Int,
    val totalOffers: Int,
    val needsAttentionItems: List<AttentionItem>,
    val recentApplications: List<JobApplicationItem>
)

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeData>> = _uiState

    init {
        fetchDashboardData()
    }

    private fun fetchDashboardData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            // TODO: Fetch from actual repository / Firestore
            delay(1000)
            
            val mockData = HomeData(
                userName = "Sandeep",
                totalApplications = 42,
                totalInterviews = 8,
                totalFollowUps = 3,
                totalOffers = 1,
                needsAttentionItems = listOf(
                    AttentionItem("1", "Follow up with ABC Technologies", "Applied 6 days ago", AttentionType.URGENT),
                    AttentionItem("2", "Google interview tomorrow", "10:30 AM", AttentionType.UPCOMING),
                    AttentionItem("3", "Complete resume analysis", "Match score: 72%", AttentionType.INFO)
                ),
                recentApplications = listOf(
                    JobApplicationItem("1", "Senior Android Developer", "Google", "Interview", 86),
                    JobApplicationItem("2", "Android Lead", "ABC Technologies", "Applied", 78)
                )
            )
            
            _uiState.value = UiState.Success(mockData)
        }
    }
}
