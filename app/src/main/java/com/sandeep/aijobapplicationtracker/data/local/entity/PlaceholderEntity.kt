package com.sandeep.aijobapplicationtracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Placeholder Room Entity.
 */
@Entity(tableName = "placeholder_table")
data class PlaceholderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)
