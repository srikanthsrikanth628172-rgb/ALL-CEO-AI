package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val sender: String, // "USER" or "AI"
  val persona: String = "AI CEO",
  val text: String,
  val proposedTasksJson: String? = null,
  val isApproved: Boolean = false,
  val timestamp: Long = System.currentTimeMillis()
)
