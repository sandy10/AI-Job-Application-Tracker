package com.sandeep.aijobapplicationtracker.data.repository

import com.google.firebase.auth.FirebaseAuth
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
        firestore.collection("users").document(requireUserId()).collection("applications")

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
            .collection("users").document(uid).collection("applications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
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
        val interviewsList = (get("interviews") as? List<Map<String, Any>>)?.map { map ->
            InterviewModel(
                roundNumber = map["roundNumber"] as? String ?: "",
                type = map["type"] as? String ?: "",
                dateTime = map["dateTime"] as? String ?: "",
                meetingUrl = map["meetingUrl"] as? String ?: "",
                interviewer = map["interviewer"] as? String ?: ""
            )
        } ?: emptyList()

        JobApplicationModel(
            id = id,
            company = getString("company") ?: "",
            jobTitle = getString("jobTitle") ?: "",
            jobUrl = getString("jobUrl") ?: "",
            location = getString("location") ?: "",
            workMode = getString("workMode") ?: "",
            source = getString("source") ?: "",
            status = getString("status") ?: "Saved",
            dateApplied = getString("dateApplied") ?: "",
            salary = getString("salary") ?: "",
            recruiter = getString("recruiter") ?: "",
            noticePeriod = getString("noticePeriod") ?: "",
            jobDescription = getString("jobDescription") ?: "",
            notes = getString("notes") ?: "",
            matchScore = (getLong("matchScore") ?: 0L).toInt(),
            timestamp = getLong("timestamp") ?: System.currentTimeMillis(),
            interviews = interviewsList
        )
    } catch (e: Exception) {
        Timber.e(e, "Failed to parse application document: $id")
        null
    }
}

/** Converts a [JobApplicationModel] to a map suitable for Firestore. */
private fun JobApplicationModel.toFirestoreMap(): Map<String, Any> = mapOf(
    "company" to company,
    "jobTitle" to jobTitle,
    "jobUrl" to jobUrl,
    "location" to location,
    "workMode" to workMode,
    "source" to source,
    "status" to status,
    "dateApplied" to dateApplied,
    "salary" to salary,
    "recruiter" to recruiter,
    "noticePeriod" to noticePeriod,
    "jobDescription" to jobDescription,
    "notes" to notes,
    "matchScore" to matchScore,
    "timestamp" to timestamp,
    "interviews" to interviews.map { interview ->
        mapOf(
            "roundNumber" to interview.roundNumber,
            "type" to interview.type,
            "dateTime" to interview.dateTime,
            "meetingUrl" to interview.meetingUrl,
            "interviewer" to interview.interviewer
        )
    }
)
