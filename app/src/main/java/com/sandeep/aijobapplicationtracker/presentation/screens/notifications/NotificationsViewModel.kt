package com.sandeep.aijobapplicationtracker.presentation.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository
import com.sandeep.aijobapplicationtracker.presentation.screens.home.AttentionItem
import com.sandeep.aijobapplicationtracker.presentation.screens.home.AttentionType
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val jobApplicationRepository: JobApplicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<AttentionItem>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<AttentionItem>>> = _uiState

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            jobApplicationRepository.getApplications().collect { applications ->
                val attentionItems = mutableListOf<AttentionItem>()

                applications.forEach { app ->
                    when (app.status.lowercase()) {
                        "saved" -> {
                            attentionItems.add(
                                AttentionItem(
                                    id = app.id,
                                    title = "Apply to ${app.company}",
                                    subtitle = "Application saved. Don't forget to apply to ${app.jobTitle}!",
                                    type = AttentionType.INFO
                                )
                            )
                        }
                        "applied" -> {
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
                    }
                }
                
                if (attentionItems.isEmpty()) {
                    _uiState.value = UiState.Empty
                } else {
                    _uiState.value = UiState.Success(attentionItems)
                }
            }
        }
    }
}
