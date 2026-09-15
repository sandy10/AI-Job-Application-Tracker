package com.sandeep.aijobapplicationtracker.presentation.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnalyticsData(
    val totalApplications: Int,
    val totalResponses: Int,
    val totalInterviews: Int,
    val totalOffers: Int,
    val appToResponseRate: Int,
    val responseToInterviewRate: Int,
    val interviewToOfferRate: Int,
    val applicationsBySource: Map<String, Int>
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val repository: JobApplicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<AnalyticsData>>(UiState.Loading)
    val uiState: StateFlow<UiState<AnalyticsData>> = _uiState

    init {
        loadAnalytics()
    }

    private fun loadAnalytics() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val applications = repository.getApplications().first()
                if (applications.isEmpty()) {
                    _uiState.value = UiState.Empty
                    return@launch
                }

                val totalApps = applications.size
                var totalResponses = 0
                var totalInterviews = 0
                var totalOffers = 0
                val sources = mutableMapOf<String, Int>()

                for (app in applications) {
                    val status = app.status.lowercase()
                    if (status != "saved" && status != "applied") {
                        totalResponses++
                    }
                    if (status == "interview" || status == "offer" || status == "rejected") {
                        if (status != "rejected" || app.interviews.isNotEmpty()) {
                             // Simplifying assumption: if it reached interview stage
                            totalInterviews++
                        }
                    }
                    if (status == "offer") {
                        totalOffers++
                    }

                    val source = app.source.ifBlank { "Other" }
                    sources[source] = sources.getOrDefault(source, 0) + 1
                }

                val appToResponseRate = if (totalApps > 0) (totalResponses * 100) / totalApps else 0
                val responseToInterviewRate = if (totalResponses > 0) (totalInterviews * 100) / totalResponses else 0
                val interviewToOfferRate = if (totalInterviews > 0) (totalOffers * 100) / totalInterviews else 0

                val data = AnalyticsData(
                    totalApplications = totalApps,
                    totalResponses = totalResponses,
                    totalInterviews = totalInterviews,
                    totalOffers = totalOffers,
                    appToResponseRate = appToResponseRate,
                    responseToInterviewRate = responseToInterviewRate,
                    interviewToOfferRate = interviewToOfferRate,
                    applicationsBySource = sources
                )

                _uiState.value = UiState.Success(data)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
