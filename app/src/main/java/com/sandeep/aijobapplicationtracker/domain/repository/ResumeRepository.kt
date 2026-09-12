package com.sandeep.aijobapplicationtracker.domain.repository

import com.sandeep.aijobapplicationtracker.domain.model.ResumeModel
import kotlinx.coroutines.flow.Flow

interface ResumeRepository {
    fun getResumes(): Flow<List<ResumeModel>>
    suspend fun addResume(resume: ResumeModel)
    suspend fun uploadResume(fileUriString: String, fileName: String, sizeBytes: Long)
    suspend fun setPrimaryResume(resumeId: String)
    suspend fun deleteResume(resumeId: String)
}
