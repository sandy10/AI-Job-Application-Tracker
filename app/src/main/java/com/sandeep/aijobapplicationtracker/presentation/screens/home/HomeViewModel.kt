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
import com.sandeep.aijobapplicationtracker.domain.repository.ProfileRepository
import com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository
import kotlinx.coroutines.flow.combine

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
    val matchScore: Int,
    val location: String,
    val workMode: String,
    val timestamp: Long
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
class HomeViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val jobApplicationRepository: JobApplicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeData>> = _uiState

    init {
        fetchDashboardData()
    }

    private fun fetchDashboardData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            combine(
                profileRepository.getProfile(),
                jobApplicationRepository.getApplications()
            ) { profile, applications ->
                val fullName = profile?.name?.takeIf { it.isNotBlank() } ?: "User"
                val name = fullName.split(" ").firstOrNull() ?: "User"
                
                val totalApps = applications.size
                val totalInterviewsCount = applications.sumOf { it.interviews.size }
                val totalOffers = applications.count { it.status.contains("Offer", ignoreCase = true) }
                
                val sortedApps = applications.sortedByDescending { it.timestamp }
                val recentApps = sortedApps.take(3).map { app ->
                    JobApplicationItem(
                        id = app.id,
                        role = app.jobTitle,
                        company = app.company,
                        status = app.status,
                        matchScore = app.matchScore,
                        location = app.location,
                        workMode = app.workMode,
                        timestamp = app.timestamp
                    )
                }

                // Generate Needs Attention items based on business logic
                val attentionItems = mutableListOf<AttentionItem>()
                var followUpCount = 0

                applications.forEach { app ->
                    when (app.status.lowercase()) {
                        "saved" -> {
                            attentionItems.add(
                                AttentionItem(
                                    id = app.id,
                                    title = "Apply to ${app.company}",
                                    subtitle = "Application saved. Don't forget to apply!",
                                    type = AttentionType.INFO
                                )
                            )
                        }
                        "applied" -> {
                            followUpCount++
                            attentionItems.add(
                                AttentionItem(
                                    id = app.id,
                                    title = "Follow up with ${app.company}",
                                    subtitle = "You applied recently. Check if you need to follow up.",
                                    type = AttentionType.URGENT
                                )
                            )
                        }
                        "interview" -> {
                            attentionItems.add(
                                AttentionItem(
                                    id = app.id,
                                    title = "Interview prep for ${app.company}",
                                    subtitle = "Prepare for your upcoming ${app.jobTitle} interview.",
                                    type = AttentionType.UPCOMING
                                )
                            )
                        }
                        "offer" -> {
                            attentionItems.add(
                                AttentionItem(
                                    id = app.id,
                                    title = "Review offer from ${app.company}",
                                    subtitle = "Congratulations! Review your offer details.",
                                    type = AttentionType.INFO
                                )
                            )
                        }
                    }
                }
                
                HomeData(
                    userName = name,
                    totalApplications = totalApps,
                    totalInterviews = totalInterviewsCount,
                    totalFollowUps = followUpCount,
                    totalOffers = totalOffers,
                    needsAttentionItems = attentionItems.take(5), // Show top 5
                    recentApplications = recentApps
                )
            }.collect { data ->
                _uiState.value = UiState.Success(data)
            }
        }
    }

    fun deleteApplication(id: String) {
        viewModelScope.launch {
            jobApplicationRepository.deleteApplication(id)
        }
    }
}
