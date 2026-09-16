package com.sandeep.aijobapplicationtracker.domain.model

/**
 * Represents the result of an AI-powered resume-to-job-description match analysis.
 * Contains the compatibility score, matched/missing skills, and recommendations.
 */
data class ResumeMatchResult(
    val company: String,
    val jobTitle: String,
    val score: Int,
    val scoreLabel: String,
    val matchedSkills: List<String>,
    val missingSkills: List<String>,
    val experienceDetails: String,
    val recommendation: String
)
