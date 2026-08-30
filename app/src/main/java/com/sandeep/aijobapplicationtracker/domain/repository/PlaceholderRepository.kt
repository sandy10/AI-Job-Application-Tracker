package com.sandeep.aijobapplicationtracker.domain.repository

import com.sandeep.aijobapplicationtracker.domain.model.PlaceholderModel
import kotlinx.coroutines.flow.Flow

/**
 * Placeholder Repository Interface.
 */
interface PlaceholderRepository {
    fun getPlaceholders(): Flow<List<PlaceholderModel>>
    suspend fun addPlaceholder(name: String)
}
