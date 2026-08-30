package com.sandeep.aijobapplicationtracker.presentation.screens.myresumes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Resume(
    val id: String,
    val fileName: String,
    val isPrimary: Boolean,
    val uploadedAt: String
)

@HiltViewModel
class MyResumesViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<Resume>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Resume>>> = _uiState

    init {
        fetchResumes()
    }

    private fun fetchResumes() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(1000)
            
            val mockResumes = listOf(
                Resume("1", "Android_Senior.pdf", true, "10 Aug 2026"),
                Resume("2", "KMP_Developer.pdf", false, "15 Aug 2026"),
                Resume("3", "Android_Lead.pdf", false, "20 Aug 2026")
            )
            
            _uiState.value = UiState.Success(mockResumes)
        }
    }

    fun setPrimary(resumeId: String) {
        val currentState = _uiState.value
        if (currentState is UiState.Success) {
            val updatedList = currentState.data.map { 
                it.copy(isPrimary = it.id == resumeId)
            }
            _uiState.value = UiState.Success(updatedList)
        }
    }

    fun uploadResume() {
        // Mock upload behavior
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is UiState.Success) {
                _uiState.value = UiState.Loading
                delay(1500)
                val newList = currentState.data.toMutableList()
                newList.add(0, Resume(
                    id = System.currentTimeMillis().toString(),
                    fileName = "New_Resume.pdf",
                    isPrimary = false,
                    uploadedAt = "Just now"
                ))
                _uiState.value = UiState.Success(newList)
            }
        }
    }
}
