package com.sandeep.aijobapplicationtracker.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.sandeep.aijobapplicationtracker.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sandeep.aijobapplicationtracker.domain.model.InterviewModel
import com.sandeep.aijobapplicationtracker.domain.model.JobApplicationModel
import com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

/**
 * Firebase Firestore implementation of [JobApplicationRepository].
 * All job applications are stored per-user at:
 *   users/{uid}/applications/{applicationId}
 */
class FirestoreJobApplicationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : JobApplicationRepository {

    /** Returns the current user's UID. Throws if not logged in. */
    private fun requireUserId(): String =
        firebaseAuth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    /** Reference to the current user's applications collection in Firestore. */
    private fun applicationsCollection() =
        firestore.collection(Constants.Firestore.USERS).document(requireUserId()).collection(Constants.Firestore.APPLICATIONS)

    /**
     * Emits a real-time list of job applications from Firestore,
     * ordered by timestamp descending (newest first).
     */
    override fun getApplications(): Flow<List<JobApplicationModel>> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore
            .collection(Constants.Firestore.USERS).document(uid).collection(Constants.Firestore.APPLICATIONS)
            .orderBy(Constants.Firestore.FIELD_TIMESTAMP, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Firestore getApplications error")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toJobApplicationModel()
                } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Saves a new job application document in Firestore under the user's collection.
     */
    override suspend fun saveApplication(application: JobApplicationModel) {
        try {
            applicationsCollection()
                .document(application.id)
                .set(application.toFirestoreMap())
                .await()
            Timber.d("Application saved: ${application.id}")
        } catch (e: Exception) {
            Timber.e(e, "Failed to save application")
        }
    }

    /**
     * Updates an existing job application in Firestore.
     */
    override suspend fun updateApplication(application: JobApplicationModel) {
        try {
            applicationsCollection()
                .document(application.id)
                .set(application.toFirestoreMap())
                .await()
            Timber.d("Application updated: ${application.id}")
        } catch (e: Exception) {
            Timber.e(e, "Failed to update application")
        }
    }

    /**
     * Deletes a job application document from Firestore by its ID.
     */
    override suspend fun deleteApplication(id: String) {
        try {
            applicationsCollection().document(id).delete().await()
            Timber.d("Application deleted: $id")
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete application")
        }
    }
}

// --- Extension functions for Firestore serialization ---

/** Converts a Firestore document map to a [JobApplicationModel]. */
private fun com.google.firebase.firestore.DocumentSnapshot.toJobApplicationModel(): JobApplicationModel? {
    return try {
        val interviewsList = (get(Constants.Firestore.FIELD_INTERVIEWS) as? List<Map<String, Any>>)?.map { map ->
            InterviewModel(
                roundNumber = map[Constants.Firestore.FIELD_ROUND_NUMBER] as? String ?: "",
                type = map[Constants.Firestore.FIELD_TYPE] as? String ?: "",
                dateTime = map[Constants.Firestore.FIELD_DATE_TIME] as? String ?: "",
                meetingUrl = map[Constants.Firestore.FIELD_MEETING_URL] as? String ?: "",
                interviewer = map[Constants.Firestore.FIELD_INTERVIEWER] as? String ?: ""
            )
        } ?: emptyList()

        JobApplicationModel(
            id = id,
            company = getString(Constants.Firestore.FIELD_COMPANY) ?: "",
            jobTitle = getString(Constants.Firestore.FIELD_JOB_TITLE) ?: "",
            jobUrl = getString(Constants.Firestore.FIELD_JOB_URL) ?: "",
            location = getString(Constants.Firestore.FIELD_LOCATION) ?: "",
            workMode = getString(Constants.Firestore.FIELD_WORK_MODE) ?: "",
            source = getString(Constants.Firestore.FIELD_SOURCE) ?: "",
            status = getString(Constants.Firestore.FIELD_STATUS) ?: "Saved",
            dateApplied = getString(Constants.Firestore.FIELD_DATE_APPLIED) ?: "",
            salary = getString(Constants.Firestore.FIELD_SALARY) ?: "",
            recruiter = getString(Constants.Firestore.FIELD_RECRUITER) ?: "",
            noticePeriod = getString(Constants.Firestore.FIELD_NOTICE_PERIOD) ?: "",
            jobDescription = getString(Constants.Firestore.FIELD_JOB_DESCRIPTION) ?: "",
            notes = getString(Constants.Firestore.FIELD_NOTES) ?: "",
            matchScore = (getLong(Constants.Firestore.FIELD_MATCH_SCORE) ?: 0L).toInt(),
            selectedResumeId = getString(Constants.Firestore.FIELD_SELECTED_RESUME_ID) ?: "",
            aiInterviewPlanJson = getString(Constants.Firestore.FIELD_AI_INTERVIEW_PLAN) ?: "",
            timestamp = getLong(Constants.Firestore.FIELD_TIMESTAMP) ?: System.currentTimeMillis(),
            interviews = interviewsList
        )
    } catch (e: Exception) {
        Timber.e(e, "Failed to parse application document: $id")
        null
    }
}

/** Converts a [JobApplicationModel] to a map suitable for Firestore. */
private fun JobApplicationModel.toFirestoreMap(): Map<String, Any> = mapOf(
    Constants.Firestore.FIELD_COMPANY to company,
    Constants.Firestore.FIELD_JOB_TITLE to jobTitle,
    Constants.Firestore.FIELD_JOB_URL to jobUrl,
    Constants.Firestore.FIELD_LOCATION to location,
    Constants.Firestore.FIELD_WORK_MODE to workMode,
    Constants.Firestore.FIELD_SOURCE to source,
    Constants.Firestore.FIELD_STATUS to status,
    Constants.Firestore.FIELD_DATE_APPLIED to dateApplied,
    Constants.Firestore.FIELD_SALARY to salary,
    Constants.Firestore.FIELD_RECRUITER to recruiter,
    Constants.Firestore.FIELD_NOTICE_PERIOD to noticePeriod,
    Constants.Firestore.FIELD_JOB_DESCRIPTION to jobDescription,
    Constants.Firestore.FIELD_NOTES to notes,
    Constants.Firestore.FIELD_MATCH_SCORE to matchScore,
    Constants.Firestore.FIELD_SELECTED_RESUME_ID to selectedResumeId,
    Constants.Firestore.FIELD_AI_INTERVIEW_PLAN to aiInterviewPlanJson,
    Constants.Firestore.FIELD_TIMESTAMP to timestamp,
    Constants.Firestore.FIELD_INTERVIEWS to interviews.map { interview ->
        mapOf(
            Constants.Firestore.FIELD_ROUND_NUMBER to interview.roundNumber,
            Constants.Firestore.FIELD_TYPE to interview.type,
            Constants.Firestore.FIELD_DATE_TIME to interview.dateTime,
            Constants.Firestore.FIELD_MEETING_URL to interview.meetingUrl,
            Constants.Firestore.FIELD_INTERVIEWER to interview.interviewer
        )
    }
)
