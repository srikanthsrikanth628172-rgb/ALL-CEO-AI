package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val detail: String,
  val tag: String = "PROJECT", // PROJECT, TASK, AGENT, CHAT
  val timestamp: Long = System.currentTimeMillis()
)
