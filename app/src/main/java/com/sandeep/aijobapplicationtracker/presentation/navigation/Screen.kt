package com.sandeep.aijobapplicationtracker.presentation.navigation

/**
 * Sealed class defining all app screens and routes.
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object SignIn : Screen("signin")
    object CareerSetup : Screen("career_setup")
    object Home : Screen("home")
    object ApplicationsList : Screen("applications_list")
    object AddApplication : Screen("add_application")
    object EditApplication : Screen("edit_application/{jobId}") { 
        fun createRoute(jobId: String) = "edit_application/$jobId" 
    }
    
    object ApplicationDetail : Screen("application_detail/{jobId}") {
        fun createRoute(jobId: String) = "application_detail/$jobId"
    }
    
    object AddInterview : Screen("add_interview/{jobId}") {
        fun createRoute(jobId: String) = "add_interview/$jobId"
    }
    
    object AiJobAnalyzer : Screen("ai_job_analyzer")
    object AiAnalysisResult : Screen("ai_analysis_result")
    object MyResumes : Screen("my_resumes")
    object AiAssistant : Screen("ai_assistant")
    
    object AiResumeMatch : Screen("ai_resume_match/{jobId}") {
        fun createRoute(jobId: String) = "ai_resume_match/$jobId"
    }
    
    object Notifications : Screen("notifications")
    
    object AiInterviewPrep : Screen("ai_interview_prep/{jobId}") {
        fun createRoute(jobId: String) = "ai_interview_prep/$jobId"
    }
    
    object AiFollowUp : Screen("ai_follow_up/{jobId}") {
        fun createRoute(jobId: String) = "ai_follow_up/$jobId"
    }
    
    object AiChat : Screen("ai_chat")
    
    object ProfileSettings : Screen("profile_settings")
    
    object Analytics : Screen("analytics")
    // TODO: Add your screens here
}
