package com.sandeep.aijobapplicationtracker.domain.model

data class DraftModel(
    val id: String = "",
    val jobId: String = "",
    val type: String = "", // "cover_letter" or "follow_up_email"
    val contents: String = "",
    val status: String = "draft" // e.g., "draft", "sent"
)
