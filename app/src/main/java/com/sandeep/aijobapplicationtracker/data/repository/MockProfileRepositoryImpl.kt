package com.sandeep.aijobapplicationtracker.data.repository

import androidx.datastore.core.DataStore
import com.sandeep.aijobapplicationtracker.utils.Constants
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sandeep.aijobapplicationtracker.domain.model.UserProfileModel
import com.sandeep.aijobapplicationtracker.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Mock implementation of ProfileRepository that uses DataStore.
 */
class MockProfileRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ProfileRepository {

    private object Keys {
        val NAME = stringPreferencesKey(Constants.Preferences.PROFILE_NAME)
        val TARGET_ROLE = stringPreferencesKey(Constants.Preferences.PROFILE_ROLE)
        val EXP_LEVEL = stringPreferencesKey(Constants.Preferences.PROFILE_EXP_LEVEL)
        val YEARS_EXP = stringPreferencesKey(Constants.Preferences.PROFILE_YEARS_EXP)
        val LOCATION = stringPreferencesKey(Constants.Preferences.PROFILE_LOCATION)
        val WORK_PREF = stringPreferencesKey(Constants.Preferences.PROFILE_WORK_PREF)
        val CURRENT_CTC = stringPreferencesKey(Constants.Preferences.PROFILE_CURRENT_CTC)
        val EXPECTED_CTC = stringPreferencesKey(Constants.Preferences.PROFILE_EXPECTED_CTC)
        val NOTICE_PERIOD = stringPreferencesKey(Constants.Preferences.PROFILE_NOTICE_PERIOD)
    }

    override fun getProfile(): Flow<UserProfileModel?> {
        return dataStore.data.map { prefs ->
            val name = prefs[Keys.NAME]
            if (name.isNullOrBlank()) {
                null
            } else {
                UserProfileModel(
                    name = name,
                    targetRole = prefs[Keys.TARGET_ROLE] ?: "",
                    experienceLevel = prefs[Keys.EXP_LEVEL] ?: "",
                    yearsExperience = prefs[Keys.YEARS_EXP] ?: "",
                    location = prefs[Keys.LOCATION] ?: "",
                    workPreference = prefs[Keys.WORK_PREF] ?: "remote",
                    currentCtc = prefs[Keys.CURRENT_CTC] ?: "",
                    expectedCtc = prefs[Keys.EXPECTED_CTC] ?: "",
                    noticePeriod = prefs[Keys.NOTICE_PERIOD] ?: ""
                )
            }
        }
    }

    override suspend fun saveProfile(profile: UserProfileModel) {
        dataStore.edit { prefs ->
            prefs[Keys.NAME] = profile.name
            prefs[Keys.TARGET_ROLE] = profile.targetRole
            prefs[Keys.EXP_LEVEL] = profile.experienceLevel
            prefs[Keys.YEARS_EXP] = profile.yearsExperience
            prefs[Keys.LOCATION] = profile.location
            prefs[Keys.WORK_PREF] = profile.workPreference
            prefs[Keys.CURRENT_CTC] = profile.currentCtc
            prefs[Keys.EXPECTED_CTC] = profile.expectedCtc
            prefs[Keys.NOTICE_PERIOD] = profile.noticePeriod
        }
    }
}
