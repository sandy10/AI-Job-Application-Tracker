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
     * Returns Result.failure if the save operation fails.
     */
    suspend fun saveApplication(application: JobApplicationModel): Result<Unit>

    /**
     * Updates an existing job application.
     * Returns Result.failure if the update operation fails.
     */
    suspend fun updateApplication(application: JobApplicationModel): Result<Unit>
    
    /**
     * Deletes a job application by its ID.
     * Returns Result.failure if the delete operation fails.
     */
    suspend fun deleteApplication(id: String): Result<Unit>
}
