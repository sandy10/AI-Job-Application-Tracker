package com.sandeep.aijobapplicationtracker.domain.model

data class ResumeModel(
    val id: String,
    val fileName: String,
    val isPrimary: Boolean,
    val uploadedAt: String,
    val storageUrl: String = "",
    val sizeBytes: Long = 0
)
