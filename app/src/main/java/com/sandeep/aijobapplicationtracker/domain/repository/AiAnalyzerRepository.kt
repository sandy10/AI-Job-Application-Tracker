package com.sandeep.aijobapplicationtracker.domain.repository

import com.sandeep.aijobapplicationtracker.domain.model.ExtractedJobData

import kotlinx.coroutines.flow.StateFlow

interface AiAnalyzerRepository {
    val latestExtractedData: StateFlow<ExtractedJobData?>
    suspend fun analyzeJobDescription(text: String, candidateProfile: String? = null): Result<ExtractedJobData>
    suspend fun generateInterviewPlan(jobDescription: String, role: String, company: String): Result<com.sandeep.aijobapplicationtracker.presentation.screens.aiinterviewprep.InterviewPlan>
    suspend fun generateMoreQuestions(role: String, existingQuestions: List<String>): Result<List<com.sandeep.aijobapplicationtracker.presentation.screens.aiinterviewprep.QuestionAnswer>>
    suspend fun generateResumeMatchAnalysis(jobDescription: String, candidateProfile: String): Result<com.sandeep.aijobapplicationtracker.presentation.screens.airesumematch.ResumeMatchResult>
    suspend fun generateFollowUpEmail(company: String, role: String, recruiterName: String?, daysSinceApplied: Int): Result<String>
    suspend fun sendChatMessage(prompt: String): Result<String>
    fun clearExtractedData()
}
