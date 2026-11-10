package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val projectId: Long,
  val title: String,
  val agentRole: String = "AI Developer",
  val status: String = "TODO", // TODO, IN_PROGRESS, DONE
  val priority: String = "MEDIUM", // HIGH, MEDIUM, LOW
  val createdAt: Long = System.currentTimeMillis()
)
