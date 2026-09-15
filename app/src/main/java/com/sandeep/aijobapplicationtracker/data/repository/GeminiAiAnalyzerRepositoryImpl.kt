package com.sandeep.aijobapplicationtracker.data.repository

import com.google.firebase.Firebase
import com.google.firebase.vertexai.vertexAI
import com.google.firebase.vertexai.type.content
import com.sandeep.aijobapplicationtracker.domain.model.ExtractedJobData
import com.sandeep.aijobapplicationtracker.domain.repository.AiAnalyzerRepository
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GeminiAiAnalyzerRepositoryImpl @Inject constructor() : AiAnalyzerRepository {

    private val _latestExtractedData = MutableStateFlow<ExtractedJobData?>(null)
    override val latestExtractedData: StateFlow<ExtractedJobData?> = _latestExtractedData.asStateFlow()

    private val generativeModel = Firebase.vertexAI.generativeModel(
        modelName = "gemini-2.5-flash"
    )

    override suspend fun analyzeJobDescription(text: String, candidateProfile: String?): Result<ExtractedJobData> {
        return try {
            val profileContext = if (!candidateProfile.isNullOrBlank()) {
                "\n\nCandidate Profile (use this to calculate matchScore):\n$candidateProfile\nCalculate a realistic match score (0-100) based on how well this candidate matches the job requirements."
            } else {
                "\n\nSince no candidate profile is provided, return matchScore as 0."
            }

            val prompt = """
                Analyze the following job description and extract the exact details requested.
                $profileContext
                
                Return ONLY a valid JSON object matching this structure, with no markdown formatting or backticks:
                {
                  "company": "Company Name (or Unknown)",
                  "role": "Job Title",
                  "location": "Location (or Remote/Unknown)",
                  "experience": "Years of experience required",
                  "seniority": "Entry-Level, Mid-Level, Senior, etc.",
                  "workMode": "Remote, Hybrid, Onsite, or Unknown",
                  "salaryRange": "Salary/CTC range or Unknown",
                  "noticePeriod": "Notice period or Unknown",
                  "jobDescription": "A concise 2-3 sentence summary of the role",
                  "jobUrl": "Job URL if present, otherwise empty string",
                  "source": "Platform (e.g. LinkedIn, Naukri) if present, otherwise empty string",
                  "recruiter": "Recruiter Name/Email if present, otherwise empty string",
                  "dateApplied": "Date if explicitly mentioned, otherwise empty string",
                  "skills": ["Skill 1", "Skill 2"],
                  "matchScore": 72
                }
                
                IMPORTANT INSTRUCTION FOR matchScore:
                The matchScore MUST be an integer between 0 and 100. Critically evaluate how well the Job Description matches the User Profile Context (skills, experience, target role). Be realistic and vary the score based on actual keyword matches and experience alignment. Do not just return 95.
                
                Job Description:
                $text
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            val responseText = response.text?.trim()?.removePrefix("```json")?.removeSuffix("```")?.trim() 
                ?: throw Exception("Empty response from AI")

            Timber.d("RAW AI RESPONSE: $responseText")

            val json = JSONObject(responseText)
            
            val skillsArray = json.optJSONArray("skills")
            val skillsList = mutableListOf<String>()
            if (skillsArray != null) {
                for (i in 0 until skillsArray.length()) {
                    skillsList.add(skillsArray.getString(i))
                }
            }

            val extractedData = ExtractedJobData(
                company = json.optString("company", "Unknown"),
                role = json.optString("role", "Unknown"),
                location = json.optString("location", "Unknown"),
                experience = json.optString("experience", "Unknown"),
                seniority = json.optString("seniority", "Unknown"),
                workMode = json.optString("workMode", "Unknown"),
                salaryRange = json.optString("salaryRange", ""),
                noticePeriod = json.optString("noticePeriod", ""),
                jobDescription = json.optString("jobDescription", ""),
                jobUrl = json.optString("jobUrl", ""),
                source = json.optString("source", ""),
                recruiter = json.optString("recruiter", ""),
                dateApplied = json.optString("dateApplied", ""),
                skills = skillsList,
                matchScore = json.optInt("matchScore", 0)
            )

            _latestExtractedData.value = extractedData

            Result.success(extractedData)
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Unknown Error"
            Timber.e(e, "AI Analysis Failed: $errorMessage")
            
            if (errorMessage.contains("503")) {
                Result.failure(Exception("Google AI Servers are currently overloaded. Please try again in a few minutes."))
            } else {
                Result.failure(Exception("AI Analysis Failed: $errorMessage"))
            }
        }
    }

    override suspend fun generateInterviewPlan(jobDescription: String, role: String, company: String): Result<com.sandeep.aijobapplicationtracker.presentation.screens.aiinterviewprep.InterviewPlan> {
        return try {
            val prompt = """
                Generate an interview preparation plan for the following job.
                Company: $company
                Role: $role
                
                Job Description:
                $jobDescription
                
                Return ONLY a valid JSON object matching this structure, with no markdown formatting or backticks:
                {
                  "strategyTip": "A single impactful tip on how to approach this specific interview.",
                  "focusAreas": [
                    { "topic": "Kotlin Coroutines", "priority": "High priority" }
                  ],
                  "likelyQuestions": [
                    { "question": "Explain structured concurrency.", "answerHint": "A highly accurate, detailed, and technically proper answer. Use structured bullet points. If applicable, include a simple ASCII diagram to explain the concept." }
                  ],
                  "behavioralQuestions": [
                    { "question": "Tell me about a time...", "answerHint": "Detailed advice using the STAR method..." }
                  ]
                }
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            val responseText = response.text?.trim()?.removePrefix("```json")?.removeSuffix("```")?.trim() 
                ?: throw Exception("Empty response from AI")

            Timber.d("RAW AI PLAN RESPONSE: $responseText")

            val json = JSONObject(responseText)
            
            val strategyTip = json.optString("strategyTip", "Research the company and align your experience with their goals.")

            val focusAreasArray = json.optJSONArray("focusAreas")
            val focusAreasList = mutableListOf<com.sandeep.aijobapplicationtracker.presentation.screens.aiinterviewprep.FocusArea>()
            if (focusAreasArray != null) {
                for (i in 0 until focusAreasArray.length()) {
                    val obj = focusAreasArray.getJSONObject(i)
                    focusAreasList.add(
                        com.sandeep.aijobapplicationtracker.presentation.screens.aiinterviewprep.FocusArea(
                            topic = obj.optString("topic", ""),
                            priority = obj.optString("priority", "")
                        )
                    )
                }
            }

            val questionsArray = json.optJSONArray("likelyQuestions")
            val questionsList = mutableListOf<com.sandeep.aijobapplicationtracker.presentation.screens.aiinterviewprep.QuestionAnswer>()
            if (questionsArray != null) {
                for (i in 0 until questionsArray.length()) {
                    val obj = questionsArray.getJSONObject(i)
                    questionsList.add(
                        com.sandeep.aijobapplicationtracker.presentation.screens.aiinterviewprep.QuestionAnswer(
                            question = obj.optString("question", ""),
                            answerHint = obj.optString("answerHint", "")
                        )
                    )
                }
            }

            val behavioralArray = json.optJSONArray("behavioralQuestions")
            val behavioralList = mutableListOf<com.sandeep.aijobapplicationtracker.presentation.screens.aiinterviewprep.QuestionAnswer>()
            if (behavioralArray != null) {
                for (i in 0 until behavioralArray.length()) {
                    val obj = behavioralArray.getJSONObject(i)
                    behavioralList.add(
                        com.sandeep.aijobapplicationtracker.presentation.screens.aiinterviewprep.QuestionAnswer(
                            question = obj.optString("question", ""),
                            answerHint = obj.optString("answerHint", "")
                        )
                    )
                }
            }

            Result.success(
                com.sandeep.aijobapplicationtracker.presentation.screens.aiinterviewprep.InterviewPlan(
                    company = company,
                    role = role,
                    strategyTip = strategyTip,
                    focusAreas = focusAreasList,
                    likelyQuestions = questionsList,
                    behavioralQuestions = behavioralList
                )
            )
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Unknown Error"
            Timber.e(e, "AI Plan Generation Failed: $errorMessage")
            Result.failure(Exception("AI Plan Generation Failed: $errorMessage"))
        }
    }

    override suspend fun generateMoreQuestions(role: String, existingQuestions: List<String>): Result<List<com.sandeep.aijobapplicationtracker.presentation.screens.aiinterviewprep.QuestionAnswer>> {
        return try {
            val prompt = """
                Generate 5 more likely interview questions for the role of $role.
                Do NOT include the following questions:
                ${existingQuestions.joinToString("\n- ")}
                
                Return ONLY a valid JSON array of objects, with no markdown formatting or backticks:
                [
                  { "question": "Question 1?", "answerHint": "A highly accurate, detailed, and technically proper answer. Use structured bullet points. If applicable, include a simple ASCII diagram." },
                  { "question": "Question 2?", "answerHint": "A highly accurate, detailed, and technically proper answer. Use structured bullet points. If applicable, include a simple ASCII diagram." }
                ]
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            val responseText = response.text?.trim()?.removePrefix("```json")?.removeSuffix("```")?.trim() 
                ?: throw Exception("Empty response from AI")

            Timber.d("RAW AI QUESTIONS RESPONSE: $responseText")

            val questionsArray = org.json.JSONArray(responseText)
            val questionsList = mutableListOf<com.sandeep.aijobapplicationtracker.presentation.screens.aiinterviewprep.QuestionAnswer>()
            for (i in 0 until questionsArray.length()) {
                val obj = questionsArray.getJSONObject(i)
                questionsList.add(
                    com.sandeep.aijobapplicationtracker.presentation.screens.aiinterviewprep.QuestionAnswer(
                        question = obj.optString("question", ""),
                        answerHint = obj.optString("answerHint", "")
                    )
                )
            }

            Result.success(questionsList)
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Unknown Error"
            Timber.e(e, "AI Questions Generation Failed: $errorMessage")
            Result.failure(Exception("AI Questions Generation Failed: $errorMessage"))
        }
    }

    override suspend fun generateResumeMatchAnalysis(
        jobDescription: String,
        candidateProfile: String
    ): Result<com.sandeep.aijobapplicationtracker.presentation.screens.airesumematch.ResumeMatchResult> {
        return try {
            val prompt = """
                Compare the following Candidate Profile with the Job Description.
                Calculate a match score out of 100.
                Return ONLY a valid JSON object matching this exact structure:
                {
                  "score": 85,
                  "matchedSkills": ["Kotlin", "Coroutines"],
                  "missingSkills": ["CI/CD", "GraphQL"],
                  "experienceDetails": "Candidate has 4 years, role needs 5+ years.",
                  "recommendation": "Highlight architecture experience and mention any CI/CD exposure."
                }
                
                Candidate Profile:
                $candidateProfile
                
                Job Description:
                $jobDescription
            """.trimIndent()
            
            val response = generativeModel.generateContent(prompt)
            val text = response.text?.trim()?.removePrefix("```json")?.removeSuffix("```")?.trim() 
                ?: throw Exception("Empty AI response")
                
            val json = JSONObject(text)
            val score = json.optInt("score", 0)
            
            val matchedArray = json.optJSONArray("matchedSkills")
            val matchedList = mutableListOf<String>()
            if (matchedArray != null) {
                for (i in 0 until matchedArray.length()) {
                    matchedList.add(matchedArray.getString(i))
                }
            }
            
            val missingArray = json.optJSONArray("missingSkills")
            val missingList = mutableListOf<String>()
            if (missingArray != null) {
                for (i in 0 until missingArray.length()) {
                    missingList.add(missingArray.getString(i))
                }
            }
            
            val scoreLabel = when {
                score >= 80 -> "Strong Match"
                score >= 60 -> "Good Match"
                score >= 40 -> "Fair Match"
                else -> "Weak Match"
            }
            
            Result.success(
                com.sandeep.aijobapplicationtracker.presentation.screens.airesumematch.ResumeMatchResult(
                    company = "",
                    jobTitle = "",
                    score = score,
                    scoreLabel = scoreLabel,
                    matchedSkills = matchedList,
                    missingSkills = missingList,
                    experienceDetails = json.optString("experienceDetails", "Experience aligns with requirements."),
                    recommendation = json.optString("recommendation", "Consider updating your resume to better match.")
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendChatMessage(prompt: String): Result<String> {
        return try {
            val response = generativeModel.generateContent(
                "You are an expert technical interviewer and AI career assistant. Provide accurate, logical, and concise answers to the user's interview questions. Be encouraging but highly technical.\n\n$prompt"
            )
            val answer = response.text ?: throw Exception("Empty response from AI")
            Result.success(answer)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun clearExtractedData() {
        _latestExtractedData.value = null
    }
    override suspend fun generateFollowUpEmail(company: String, role: String, recruiterName: String?, daysSinceApplied: Int): Result<String> {
        return try {
            val prompt = "Generate a professional follow-up email for a job application.\nCompany: $company\nRole: $role\nRecruiter Name: ${recruiterName ?: "Hiring Manager"}\nDays since applied: $daysSinceApplied\n\nThe email should be polite, concise, and express continued interest in the role.\nDo not include subject line or placeholders like [Your Name]. Just the email body."
            val response = generativeModel.generateContent(prompt)
            val text = response.text ?: return Result.failure(Exception("Empty response from AI"))
            Result.success(text.trim())
        } catch (e: Exception) {
            Timber.e(e, "Error generating follow up email")
            Result.failure(e)
        }
    }
}
