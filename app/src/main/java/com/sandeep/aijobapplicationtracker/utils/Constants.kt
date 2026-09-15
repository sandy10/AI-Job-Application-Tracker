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
}
