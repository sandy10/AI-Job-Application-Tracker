package com.sandeep.aijobapplicationtracker.domain.model

data class ExtractedJobData(
    val company: String = "",
    val role: String = "",
    val location: String = "",
    val experience: String = "",
    val seniority: String = "",
    val workMode: String = "",
    val salaryRange: String = "",
    val noticePeriod: String = "",
    val jobDescription: String = "",
    val jobUrl: String = "",
    val source: String = "",
    val recruiter: String = "",
    val dateApplied: String = "",
    val skills: List<String> = emptyList(),
    val matchScore: Int = 0
)
