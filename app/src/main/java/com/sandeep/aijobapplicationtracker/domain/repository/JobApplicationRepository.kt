package com.sandeep.aijobapplicationtracker.domain.repository

import com.sandeep.aijobapplicationtracker.domain.model.JobApplicationModel
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing job applications.
 */
interface JobApplicationRepository {
    
    /**
     * Emits the list of all saved job applications.
     */
    fun getApplications(): Flow<List<JobApplicationModel>>

    /**
     * Saves a new job application.
     */
    suspend fun saveApplication(application: JobApplicationModel)

    /**
     * Updates an existing job application.
     */
    suspend fun updateApplication(application: JobApplicationModel)
    
    /**
     * Deletes a job application by its ID.
     */
    suspend fun deleteApplication(id: String)
}
