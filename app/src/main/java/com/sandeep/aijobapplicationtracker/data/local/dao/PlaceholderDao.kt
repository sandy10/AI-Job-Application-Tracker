package com.sandeep.aijobapplicationtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sandeep.aijobapplicationtracker.data.local.entity.PlaceholderEntity
import kotlinx.coroutines.flow.Flow

/**
 * Placeholder Room DAO.
 */
@Dao
interface PlaceholderDao {
    @Query("SELECT * FROM placeholder_table")
    fun getAll(): Flow<List<PlaceholderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PlaceholderEntity)
}
