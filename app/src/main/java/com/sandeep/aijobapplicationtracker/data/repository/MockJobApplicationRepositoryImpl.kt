package com.sandeep.aijobapplicationtracker.data.repository

import androidx.datastore.core.DataStore
import com.sandeep.aijobapplicationtracker.utils.Constants
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sandeep.aijobapplicationtracker.domain.model.JobApplicationModel
import com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

/**
 * Mock implementation of JobApplicationRepository using DataStore and org.json.
 */
class MockJobApplicationRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : JobApplicationRepository {

    private val APPLICATIONS_KEY = stringPreferencesKey("mock_job_applications")

    override fun getApplications(): Flow<List<JobApplicationModel>> {
        return dataStore.data.map { prefs ->
            val jsonString = prefs[APPLICATIONS_KEY] ?: "[]"
            parseApplications(jsonString)
        }
    }

    override suspend fun saveApplication(application: JobApplicationModel) {
        dataStore.edit { prefs ->
            val jsonString = prefs[APPLICATIONS_KEY] ?: "[]"
            val currentList = parseApplications(jsonString).toMutableList()
            currentList.add(application)
            prefs[APPLICATIONS_KEY] = serializeApplications(currentList)
        }
    }

    override suspend fun deleteApplication(id: String) {
        dataStore.edit { prefs ->
            val jsonString = prefs[APPLICATIONS_KEY] ?: "[]"
            val currentList = parseApplications(jsonString).toMutableList()
            currentList.removeAll { it.id == id }
            prefs[APPLICATIONS_KEY] = serializeApplications(currentList)
        }
    }

    override suspend fun updateApplication(application: JobApplicationModel) {
        dataStore.edit { prefs ->
            val jsonString = prefs[APPLICATIONS_KEY] ?: "[]"
            val currentList = parseApplications(jsonString).toMutableList()
            val index = currentList.indexOfFirst { it.id == application.id }
            if (index != -1) {
                currentList[index] = application
                prefs[APPLICATIONS_KEY] = serializeApplications(currentList)
            }
        }
    }

    private fun parseApplications(jsonString: String): List<JobApplicationModel> {
        val list = mutableListOf<JobApplicationModel>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val interviewsJson = obj.optJSONArray(Constants.Firestore.FIELD_INTERVIEWS)
                val interviewsList = mutableListOf<com.sandeep.aijobapplicationtracker.domain.model.InterviewModel>()
                if (interviewsJson != null) {
                    for (j in 0 until interviewsJson.length()) {
                        val intObj = interviewsJson.getJSONObject(j)
                        interviewsList.add(
                            com.sandeep.aijobapplicationtracker.domain.model.InterviewModel(
                                roundNumber = intObj.optString(Constants.Firestore.FIELD_ROUND_NUMBER, ""),
                                type = intObj.optString(Constants.Firestore.FIELD_TYPE, ""),
                                dateTime = intObj.optString(Constants.Firestore.FIELD_DATE_TIME, ""),
                                meetingUrl = intObj.optString(Constants.Firestore.FIELD_MEETING_URL, ""),
                                interviewer = intObj.optString(Constants.Firestore.FIELD_INTERVIEWER, "")
                            )
                        )
                    }
                }

                list.add(
                    JobApplicationModel(
                        id = obj.optString(Constants.Firestore.FIELD_ID, ""),
                        company = obj.optString(Constants.Firestore.FIELD_COMPANY, ""),
                        jobTitle = obj.optString(Constants.Firestore.FIELD_JOB_TITLE, ""),
                        jobUrl = obj.optString(Constants.Firestore.FIELD_JOB_URL, ""),
                        location = obj.optString(Constants.Firestore.FIELD_LOCATION, ""),
                        workMode = obj.optString(Constants.Firestore.FIELD_WORK_MODE, ""),
                        source = obj.optString(Constants.Firestore.FIELD_SOURCE, ""),
                        status = obj.optString(Constants.Firestore.FIELD_STATUS, ""),
                        dateApplied = obj.optString(Constants.Firestore.FIELD_DATE_APPLIED, ""),
                        salary = obj.optString(Constants.Firestore.FIELD_SALARY, ""),
                        recruiter = obj.optString(Constants.Firestore.FIELD_RECRUITER, ""),
                        noticePeriod = obj.optString(Constants.Firestore.FIELD_NOTICE_PERIOD, ""),
                        jobDescription = obj.optString(Constants.Firestore.FIELD_JOB_DESCRIPTION, ""),
                        notes = obj.optString(Constants.Firestore.FIELD_NOTES, ""),
                        matchScore = obj.optInt(Constants.Firestore.FIELD_MATCH_SCORE, 0),
                        selectedResumeId = obj.optString(Constants.Firestore.FIELD_SELECTED_RESUME_ID, ""),
                        aiInterviewPlanJson = obj.optString(Constants.Firestore.FIELD_AI_INTERVIEW_PLAN, ""),
                        timestamp = obj.optLong(Constants.Firestore.FIELD_TIMESTAMP, System.currentTimeMillis()),
                        interviews = interviewsList
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun serializeApplications(list: List<JobApplicationModel>): String {
        val jsonArray = JSONArray()
        try {
            for (app in list) {
                val obj = JSONObject().apply {
                    put(Constants.Firestore.FIELD_ID, app.id)
                    put(Constants.Firestore.FIELD_COMPANY, app.company)
                    put(Constants.Firestore.FIELD_JOB_TITLE, app.jobTitle)
                    put(Constants.Firestore.FIELD_JOB_URL, app.jobUrl)
                    put(Constants.Firestore.FIELD_LOCATION, app.location)
                    put(Constants.Firestore.FIELD_WORK_MODE, app.workMode)
                    put(Constants.Firestore.FIELD_SOURCE, app.source)
                    put(Constants.Firestore.FIELD_STATUS, app.status)
                    put(Constants.Firestore.FIELD_DATE_APPLIED, app.dateApplied)
                    put(Constants.Firestore.FIELD_SALARY, app.salary)
                    put(Constants.Firestore.FIELD_RECRUITER, app.recruiter)
                    put(Constants.Firestore.FIELD_NOTICE_PERIOD, app.noticePeriod)
                    put(Constants.Firestore.FIELD_JOB_DESCRIPTION, app.jobDescription)
                    put(Constants.Firestore.FIELD_NOTES, app.notes)
                    put(Constants.Firestore.FIELD_MATCH_SCORE, app.matchScore)
                    put(Constants.Firestore.FIELD_SELECTED_RESUME_ID, app.selectedResumeId)
                    put(Constants.Firestore.FIELD_AI_INTERVIEW_PLAN, app.aiInterviewPlanJson)
                    put(Constants.Firestore.FIELD_TIMESTAMP, app.timestamp)

                    val interviewsArray = JSONArray()
                    for (interview in app.interviews) {
                        val intObj = JSONObject().apply {
                            put(Constants.Firestore.FIELD_ROUND_NUMBER, interview.roundNumber)
                            put(Constants.Firestore.FIELD_TYPE, interview.type)
                            put(Constants.Firestore.FIELD_DATE_TIME, interview.dateTime)
                            put(Constants.Firestore.FIELD_MEETING_URL, interview.meetingUrl)
                            put(Constants.Firestore.FIELD_INTERVIEWER, interview.interviewer)
                        }
                        interviewsArray.put(intObj)
                    }
                    put(Constants.Firestore.FIELD_INTERVIEWS, interviewsArray)
                }
                jsonArray.put(obj)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonArray.toString()
    }
}
