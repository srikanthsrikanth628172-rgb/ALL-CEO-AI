package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val description: String,
  val category: String = "Software",
  val status: String = "ACTIVE", // ACTIVE, COMPLETED, ON_HOLD
  val targetDate: String = "Q4 2026",
  val createdAt: Long = System.currentTimeMillis()
)
