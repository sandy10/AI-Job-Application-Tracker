package com.sandeep.aijobapplicationtracker.data.repository

import androidx.datastore.core.DataStore
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
                val interviewsJson = obj.optJSONArray("interviews")
                val interviewsList = mutableListOf<com.sandeep.aijobapplicationtracker.domain.model.InterviewModel>()
                if (interviewsJson != null) {
                    for (j in 0 until interviewsJson.length()) {
                        val intObj = interviewsJson.getJSONObject(j)
                        interviewsList.add(
                            com.sandeep.aijobapplicationtracker.domain.model.InterviewModel(
                                roundNumber = intObj.optString("roundNumber", ""),
                                type = intObj.optString("type", ""),
                                dateTime = intObj.optString("dateTime", ""),
                                meetingUrl = intObj.optString("meetingUrl", ""),
                                interviewer = intObj.optString("interviewer", "")
                            )
                        )
                    }
                }

                list.add(
                    JobApplicationModel(
                        id = obj.optString("id", ""),
                        company = obj.optString("company", ""),
                        jobTitle = obj.optString("jobTitle", ""),
                        jobUrl = obj.optString("jobUrl", ""),
                        location = obj.optString("location", ""),
                        workMode = obj.optString("workMode", ""),
                        source = obj.optString("source", ""),
                        status = obj.optString("status", ""),
                        dateApplied = obj.optString("dateApplied", ""),
                        salary = obj.optString("salary", ""),
                        recruiter = obj.optString("recruiter", ""),
                        noticePeriod = obj.optString("noticePeriod", ""),
                        jobDescription = obj.optString("jobDescription", ""),
                        notes = obj.optString("notes", ""),
                        matchScore = obj.optInt("matchScore", 0),
                        timestamp = obj.optLong("timestamp", 0L),
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
                    put("id", app.id)
                    put("company", app.company)
                    put("jobTitle", app.jobTitle)
                    put("jobUrl", app.jobUrl)
                    put("location", app.location)
                    put("workMode", app.workMode)
                    put("source", app.source)
                    put("status", app.status)
                    put("dateApplied", app.dateApplied)
                    put("salary", app.salary)
                    put("recruiter", app.recruiter)
                    put("noticePeriod", app.noticePeriod)
                    put("jobDescription", app.jobDescription)
                    put("notes", app.notes)
                    put("matchScore", app.matchScore)
                    put("timestamp", app.timestamp)

                    val interviewsArray = JSONArray()
                    for (interview in app.interviews) {
                        val intObj = JSONObject().apply {
                            put("roundNumber", interview.roundNumber)
                            put("type", interview.type)
                            put("dateTime", interview.dateTime)
                            put("meetingUrl", interview.meetingUrl)
                            put("interviewer", interview.interviewer)
                        }
                        interviewsArray.put(intObj)
                    }
                    put("interviews", interviewsArray)
                }
                jsonArray.put(obj)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonArray.toString()
    }
}
