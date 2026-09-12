package com.sandeep.aijobapplicationtracker.domain.model

data class InterviewModel(
    val roundNumber: String,
    val type: String,
    val dateTime: String,
    val meetingUrl: String,
    val interviewer: String,
    val notes: String = ""
)

data class JobApplicationModel(
    val id: String,
    val company: String,
    val jobTitle: String,
    val jobUrl: String,
    val location: String,
    val workMode: String,
    val source: String,
    val status: String,
    val dateApplied: String,
    val salary: String,
    val recruiter: String,
    val noticePeriod: String,
    val jobDescription: String,
    val notes: String,
    val matchScore: Int = 0,
    val timestamp: Long,
    val interviews: List<InterviewModel> = emptyList()
)
