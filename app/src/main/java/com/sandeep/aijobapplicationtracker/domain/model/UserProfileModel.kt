package com.sandeep.aijobapplicationtracker.domain.model

/**
 * User Profile Domain Model.
 */
data class UserProfileModel(
    val name: String,
    val targetRole: String,
    val experienceLevel: String,
    val yearsExperience: String,
    val location: String,
    val workPreference: String,
    val currentCtc: String = "",
    val expectedCtc: String = "",
    val noticePeriod: String = ""
)
