package com.sandeep.aijobapplicationtracker.domain.repository

import com.sandeep.aijobapplicationtracker.domain.model.ExtractedJobData
import com.sandeep.aijobapplicationtracker.domain.model.InterviewPlan
import com.sandeep.aijobapplicationtracker.domain.model.QuestionAnswer
import com.sandeep.aijobapplicationtracker.domain.model.ResumeMatchResult

import kotlinx.coroutines.flow.StateFlow

interface AiAnalyzerRepository {
    val latestExtractedData: StateFlow<ExtractedJobData?>
    suspend fun analyzeJobDescription(text: String, candidateProfile: String? = null): Result<ExtractedJobData>
    suspend fun generateInterviewPlan(jobDescription: String, role: String, company: String): Result<InterviewPlan>
    suspend fun generateMoreQuestions(role: String, existingQuestions: List<String>): Result<List<QuestionAnswer>>
    suspend fun generateResumeMatchAnalysis(jobDescription: String, candidateProfile: String): Result<ResumeMatchResult>
    suspend fun generateFollowUpEmail(company: String, role: String, recruiterName: String?, daysSinceApplied: Int): Result<String>
    suspend fun sendChatMessage(prompt: String): Result<String>
    suspend fun generateVideoInterviewQuestions(jobDescription: String, resumeContent: String): Result<List<String>>
    suspend fun evaluateVideoInterview(qaPairs: List<Pair<String, String>>, role: String): Result<String>
    fun clearExtractedData()
}
