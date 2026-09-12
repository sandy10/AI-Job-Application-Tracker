package com.sandeep.aijobapplicationtracker.presentation.screens.myresumes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.domain.model.ResumeModel
import com.sandeep.aijobapplicationtracker.domain.repository.ResumeRepository
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MyResumesViewModel @Inject constructor(
    private val resumeRepository: ResumeRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<ResumeModel>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<ResumeModel>>> = _uiState

    init {
        fetchResumes()
    }

    private fun fetchResumes() {
        viewModelScope.launch {
            resumeRepository.getResumes().collect { resumes ->
                _uiState.value = UiState.Success(resumes)
            }
        }
    }

    fun setPrimary(resumeId: String) {
        viewModelScope.launch {
            resumeRepository.setPrimaryResume(resumeId)
        }
    }

    fun uploadResume(fileUriString: String, fileName: String, sizeBytes: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                resumeRepository.uploadResume(fileUriString, fileName, sizeBytes)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to upload resume")
            }
        }
    }

    fun deleteResume(resumeId: String) {
        viewModelScope.launch {
            try {
                resumeRepository.deleteResume(resumeId)
            } catch (e: Exception) {
                // Ignore or show error
            }
        }
    }
}
