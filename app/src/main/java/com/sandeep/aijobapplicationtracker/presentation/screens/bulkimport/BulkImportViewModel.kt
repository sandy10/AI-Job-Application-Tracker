package com.sandeep.aijobapplicationtracker.presentation.screens.bulkimport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.domain.model.DraftModel
import com.sandeep.aijobapplicationtracker.domain.model.JobApplicationModel
import com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BulkImportViewModel @Inject constructor(
    private val repository: JobApplicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()

    fun ingestData(jobsCsv: String, draftsCsv: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val jobsList = mutableListOf<JobApplicationModel>()
                if (jobsCsv.isNotBlank()) {
                    val jobLines = jobsCsv.lines().filter { it.isNotBlank() }
                    for (line in jobLines) {
                        // Example: 1, 2026-06-01, 2026-06-30, full-time, Senior Backend Engineer - Python, Bengaluru
                        val parts = line.split(",").map { it.trim() }
                        if (parts.size >= 5) {
                            val job = JobApplicationModel(
                                id = parts[0],
                                company = parts[4].split("-").firstOrNull()?.trim() ?: "Unknown",
                                jobTitle = parts[4],
                                jobUrl = "",
                                location = if (parts.size >= 6) parts[5] else "Unknown",
                                workMode = parts[3],
                                source = "Import",
                                status = "Saved",
                                dateApplied = parts[1],
                                salary = "",
                                recruiter = "",
                                noticePeriod = "",
                                jobDescription = "",
                                notes = "",
                                timestamp = System.currentTimeMillis()
                            )
                            jobsList.add(job)
                        }
                    }
                }

                val draftsList = mutableListOf<DraftModel>()
                if (draftsCsv.isNotBlank()) {
                    val draftLines = draftsCsv.lines().filter { it.isNotBlank() }
                    for (line in draftLines) {
                        // Example: 1, 1, cover_letter, "Dear Hiring Manager...", draft
                        // Simple parser assuming quotes might enclose contents with commas
                        val parts = line.split(",").map { it.trim() }
                        if (parts.size >= 5) {
                            val draft = DraftModel(
                                id = parts[0],
                                jobId = parts[1],
                                type = parts[2],
                                contents = parts[3].removePrefix("\"").removeSuffix("\""),
                                status = parts[4]
                            )
                            draftsList.add(draft)
                        }
                    }
                }

                if (jobsList.isEmpty() && draftsList.isEmpty()) {
                    _uiState.value = UiState.Error("No valid data found to import.")
                    return@launch
                }

                val result = repository.batchIngestJobsAndDrafts(jobsList, draftsList)
                if (result.isSuccess) {
                    _uiState.value = UiState.Success(Unit)
                } else {
                    _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Import failed")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "An error occurred during parsing")
            }
        }
    }
}
