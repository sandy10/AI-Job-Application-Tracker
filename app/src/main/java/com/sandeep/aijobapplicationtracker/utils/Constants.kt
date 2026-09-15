package com.sandeep.aijobapplicationtracker.utils

/**
 * App-wide constants.
 */
object Constants {
    const val DATABASE_NAME = "aijob_database"
    const val PREFERENCES_NAME = "aijob_preferences"
    
    object Firestore {
        const val USERS = "users"
        const val APPLICATIONS = "applications"
        const val RESUMES = "resumes"
        const val PROFILE = "profile"
        
        // Application Fields
        const val FIELD_ID = "id"
        const val FIELD_COMPANY = "company"
        const val FIELD_JOB_TITLE = "jobTitle"
        const val FIELD_JOB_URL = "jobUrl"
        const val FIELD_LOCATION = "location"
        const val FIELD_WORK_MODE = "workMode"
        const val FIELD_SOURCE = "source"
        const val FIELD_STATUS = "status"
        const val FIELD_DATE_APPLIED = "dateApplied"
        const val FIELD_SALARY = "salary"
        const val FIELD_RECRUITER = "recruiter"
        const val FIELD_NOTICE_PERIOD = "noticePeriod"
        const val FIELD_JOB_DESCRIPTION = "jobDescription"
        const val FIELD_NOTES = "notes"
        const val FIELD_MATCH_SCORE = "matchScore"
        const val FIELD_SELECTED_RESUME_ID = "selectedResumeId"
        const val FIELD_AI_INTERVIEW_PLAN = "aiInterviewPlanJson"
        const val FIELD_TIMESTAMP = "timestamp"
        const val FIELD_INTERVIEWS = "interviews"
        
        // Interview Fields
        const val FIELD_ROUND_NUMBER = "roundNumber"
        const val FIELD_TYPE = "type"
        const val FIELD_DATE_TIME = "dateTime"
        const val FIELD_MEETING_URL = "meetingUrl"
        const val FIELD_INTERVIEWER = "interviewer"
    }

    object Preferences {
        const val PROFILE_NAME = "profile_name"
        const val PROFILE_ROLE = "profile_role"
        const val PROFILE_EXP_LEVEL = "profile_exp_level"
        const val PROFILE_YEARS_EXP = "profile_years_exp"
        const val PROFILE_LOCATION = "profile_location"
        const val PROFILE_WORK_PREF = "profile_work_pref"
        const val PROFILE_CURRENT_CTC = "profile_current_ctc"
        const val PROFILE_EXPECTED_CTC = "profile_expected_ctc"
        const val PROFILE_NOTICE_PERIOD = "profile_notice_period"
    }

    /**
     * Firebase Analytics event names and parameter keys.
     * Centralized here to prevent typos across all ViewModels.
     */
    object Analytics {

        // ---- Event Names ----
        // Fires when user logs in
        const val EVENT_LOGIN = "user_login"
        // Fires when user logs out
        const val EVENT_LOGOUT = "user_logout"
        // Fires when a new application is created
        const val EVENT_APPLICATION_CREATED = "application_created"
        // Fires when an application status changes (e.g. Applied → Interview)
        const val EVENT_STATUS_CHANGED = "application_status_changed"
        // Fires when any AI feature is used
        const val EVENT_AI_FEATURE_USED = "ai_feature_used"
        // Fires when Video AI Interview is started
        const val EVENT_VIDEO_INTERVIEW_STARTED = "video_interview_started"
        // Fires when Video AI Interview is completed
        const val EVENT_VIDEO_INTERVIEW_COMPLETED = "video_interview_completed"
        // Fires when user views any screen (manual tracking supplement)
        const val EVENT_SCREEN_VIEW = "screen_view"

        // ---- Parameter Keys ----
        const val PARAM_METHOD = "method"
        const val PARAM_SOURCE = "source"
        const val PARAM_EXPERIENCE_LEVEL = "experience_level"
        const val PARAM_JOB_ID = "job_id"
        const val PARAM_FROM_STATUS = "from_status"
        const val PARAM_TO_STATUS = "to_status"
        const val PARAM_FEATURE_NAME = "feature_name"
        const val PARAM_SCORE = "score"
        const val PARAM_SCREEN_NAME = "screen_name"

        // ---- AI Feature Names (values for PARAM_FEATURE_NAME) ----
        const val FEATURE_JOB_ANALYZER = "job_analyzer"
        const val FEATURE_RESUME_MATCH = "resume_match"
        const val FEATURE_INTERVIEW_PREP = "interview_prep"
        const val FEATURE_FOLLOW_UP = "follow_up"
        const val FEATURE_AI_CHAT = "ai_chat"
        const val FEATURE_VIDEO_INTERVIEW = "video_interview"
    }
}
