package com.sandeep.aijobapplicationtracker.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sandeep.aijobapplicationtracker.domain.model.ResumeModel
import com.sandeep.aijobapplicationtracker.domain.repository.ResumeRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

import com.google.firebase.storage.FirebaseStorage
// ... existing imports

class FirestoreResumeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage
) : ResumeRepository {

    private fun requireUserId(): String =
        firebaseAuth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    private fun resumesCollection() =
        firestore.collection("users").document(requireUserId()).collection("resumes")

    override fun getResumes(): Flow<List<ResumeModel>> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = resumesCollection()
            .orderBy("uploadedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Firestore getResumes error")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                
                val list = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        ResumeModel(
                            id = doc.id,
                            fileName = doc.getString("fileName") ?: "",
                            isPrimary = doc.getBoolean("isPrimary") ?: false,
                            uploadedAt = doc.getString("uploadedAt") ?: "",
                            storageUrl = doc.getString("storageUrl") ?: "",
                            sizeBytes = doc.getLong("sizeBytes") ?: 0L
                        )
                    } catch (e: Exception) {
                        Timber.e(e, "Error parsing resume doc: ${doc.id}")
                        null
                    }
                } ?: emptyList()
                
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun addResume(resume: ResumeModel) {
        try {
            resumesCollection().document(resume.id).set(
                mapOf(
                    "fileName" to resume.fileName,
                    "isPrimary" to resume.isPrimary,
                    "uploadedAt" to resume.uploadedAt,
                    "storageUrl" to resume.storageUrl,
                    "sizeBytes" to resume.sizeBytes
                )
            ).await()
            Timber.d("Resume added: ${resume.fileName}")
        } catch (e: Exception) {
            Timber.e(e, "Failed to add resume")
        }
    }

    override suspend fun uploadResume(fileUriString: String, fileName: String, sizeBytes: Long) {
        val uid = requireUserId()
        val resumeId = java.util.UUID.randomUUID().toString()
        val storageRef = firebaseStorage.reference.child("users/$uid/resumes/$resumeId/$fileName")
        
        try {
            val fileUri = android.net.Uri.parse(fileUriString)
            // Upload to Storage
            storageRef.putFile(fileUri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            
            // Format date
            val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
            val dateStr = sdf.format(java.util.Date())
            
            // Create ResumeModel and save to Firestore
            val resume = ResumeModel(
                id = resumeId,
                fileName = fileName,
                isPrimary = false, // defaults to false
                uploadedAt = dateStr,
                storageUrl = downloadUrl,
                sizeBytes = sizeBytes
            )
            addResume(resume)
        } catch (e: Exception) {
            Timber.e(e, "Failed to upload resume to Storage")
            throw e
        }
    }

    override suspend fun setPrimaryResume(resumeId: String) {
        try {
            // Unset all other primary resumes
            val currentPrimary = resumesCollection()
                .whereEqualTo("isPrimary", true)
                .get()
                .await()
            
            firestore.runBatch { batch ->
                for (doc in currentPrimary.documents) {
                    if (doc.id != resumeId) {
                        batch.update(doc.reference, "isPrimary", false)
                    }
                }
                batch.update(resumesCollection().document(resumeId), "isPrimary", true)
            }.await()
            Timber.d("Primary resume set to: $resumeId")
        } catch (e: Exception) {
            Timber.e(e, "Failed to set primary resume")
        }
    }

    override suspend fun deleteResume(resumeId: String) {
        try {
            resumesCollection().document(resumeId).delete().await()
            Timber.d("Resume deleted: $resumeId")
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete resume")
        }
    }
}
