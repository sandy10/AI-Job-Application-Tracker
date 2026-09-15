package com.sandeep.aijobapplicationtracker.utils

/**
 * Constants and templates for all AI Prompts.
 * Kept strictly in code (not strings.xml) to prevent accidental localization
 * which would break JSON schema generation and API parsing.
 */
object PromptConstants {

    fun getJobDescriptionAnalysisPrompt(jobDescription: String): String {
        return """
            Analyze the following job description and extract key details.
            Return ONLY a valid JSON object matching this exact structure:
            {
              "company": "Company Name",
              "role": "Job Title",
              "experience": "Years of experience required",
              "location": "Location",
              "skills": ["Skill 1", "Skill 2"],
              "keywords": ["Keyword 1", "Keyword 2"],
              "responsibilities": ["Resp 1", "Resp 2"],
              "seniority": "Entry/Mid/Senior"
            }
            
            Job Description:
            $jobDescription
        """.trimIndent()
    }

    fun getNextActionPrompt(status: String, statusDate: String, role: String, company: String): String {
        return "Based on the job application status '$status' updated on '$statusDate' for a '$role' role at '$company', what is the best single next action the candidate should take? Keep it under 15 words."
    }

    fun getResumeMatchPrompt(jobDescription: String, candidateProfile: String): String {
        return """
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
    }

    fun getChatMessagePrompt(userPrompt: String): String {
        return "You are an expert technical interviewer and AI career assistant. Provide accurate, logical, and concise answers to the user's interview questions. Be encouraging but highly technical.\n\n$userPrompt"
    }

    fun getFollowUpEmailPrompt(company: String, role: String, recruiterName: String?, daysSinceApplied: Int): String {
        return "Generate a professional follow-up email for a job application.\nCompany: $company\nRole: $role\nRecruiter Name: ${recruiterName ?: "Hiring Manager"}\nDays since applied: $daysSinceApplied\n\nThe email should be polite, concise, and express continued interest in the role.\nDo not include subject line or placeholders like [Your Name]. Just the email body."
    }

    fun getVideoInterviewQuestionsPrompt(jobDescription: String, resumeContent: String): String {
        return "You are an expert technical interviewer. Generate exactly 4 additional interview questions based on the candidate's resume and the job description. The questions should be specific and probing. Format your response as a simple JSON array of strings: [\"Question 2\", \"Question 3\", \"Question 4\", \"Question 5\"]. Do not include markdown code block formatting like ```json. Just the array. Job Description: $jobDescription. Resume: $resumeContent."
    }

    fun getVideoInterviewEvaluationPrompt(transcript: String, role: String): String {
        return """
            You are an expert interviewer evaluating a candidate for a $role position. Here is the transcript of their video interview:
            
            $transcript
            
            Provide constructive feedback on their answers.
            At the very beginning, give an overall score out of 10 in the format: ### Overall Score: X/10
            Then, highlight what they did well and areas for improvement.
            Format the response beautifully using Markdown with headings (e.g. ###), bolding (**), and bullet points (-).
        """.trimIndent()
    }
}
