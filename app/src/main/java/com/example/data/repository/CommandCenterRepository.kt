package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.model.ActivityEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FounderSettings(
  val founderName: String = "Solo Founder",
  val ventureName: String = "MY AI SUPERSTAR",
  val missionStatement: String = "Building high-leverage software with an autonomous AI executive team.",
  val customAiInstructions: String = "Prioritize clean architecture, small atomic tasks, and clear plain-English summaries.",
  val requireApprovalBeforeAction: Boolean = true,
  val localPersistenceOnly: Boolean = true
)

class CommandCenterRepository(
  private val context: Context,
  private val database: AppDatabase
) {
  private val prefs = context.getSharedPreferences("ai_ceo_founder_settings", Context.MODE_PRIVATE)

  private val _founderSettings = MutableStateFlow(loadSettings())
  val founderSettings: StateFlow<FounderSettings> = _founderSettings.asStateFlow()

  val allProjects: Flow<List<ProjectEntity>> = database.projectDao().getAllProjects()
  val allTasks: Flow<List<TaskEntity>> = database.taskDao().getAllTasks()
  val pendingTasks: Flow<List<TaskEntity>> = database.taskDao().getPendingTasks()
  val completedTasks: Flow<List<TaskEntity>> = database.taskDao().getCompletedTasks()
  val pendingTaskCount: Flow<Int> = database.taskDao().getPendingTaskCount()
  val completedTaskCount: Flow<Int> = database.taskDao().getCompletedTaskCount()
  val totalTaskCount: Flow<Int> = database.taskDao().getTotalTaskCount()
  val chatMessages: Flow<List<ChatMessageEntity>> = database.chatMessageDao().getAllMessages()
  val recentActivities: Flow<List<ActivityEntity>> = database.activityDao().getRecentActivities()

  private fun loadSettings(): FounderSettings {
    return FounderSettings(
      founderName = prefs.getString("founder_name", "Solo Founder") ?: "Solo Founder",
      ventureName = prefs.getString("venture_name", "MY AI SUPERSTAR") ?: "MY AI SUPERSTAR",
      missionStatement = prefs.getString("mission_statement", "Building high-leverage software with an autonomous AI executive team.")
        ?: "Building high-leverage software with an autonomous AI executive team.",
      customAiInstructions = prefs.getString("custom_instructions", "Prioritize clean architecture, small atomic tasks, and clear plain-English summaries.")
        ?: "Prioritize clean architecture, small atomic tasks, and clear plain-English summaries.",
      requireApprovalBeforeAction = prefs.getBoolean("require_approval", true),
      localPersistenceOnly = prefs.getBoolean("local_persistence", true)
    )
  }

  fun updateSettings(newSettings: FounderSettings) {
    prefs.edit()
      .putString("founder_name", newSettings.founderName)
      .putString("venture_name", newSettings.ventureName)
      .putString("mission_statement", newSettings.missionStatement)
      .putString("custom_instructions", newSettings.customAiInstructions)
      .putBoolean("require_approval", newSettings.requireApprovalBeforeAction)
      .putBoolean("local_persistence", newSettings.localPersistenceOnly)
      .apply()
    _founderSettings.value = newSettings
  }

  suspend fun insertProject(project: ProjectEntity): Long {
    val id = database.projectDao().insertProject(project)
    logActivity(
      title = "Created Project",
      detail = "Started project: ${project.title}",
      tag = "PROJECT"
    )
    return id
  }

  suspend fun updateProject(project: ProjectEntity) {
    database.projectDao().updateProject(project)
    logActivity(
      title = "Updated Project",
      detail = "Project ${project.title} status: ${project.status}",
      tag = "PROJECT"
    )
  }

  suspend fun deleteProject(project: ProjectEntity) {
    database.taskDao().deleteTasksForProject(project.id)
    database.projectDao().deleteProject(project)
    logActivity(
      title = "Deleted Project",
      detail = "Removed ${project.title} and associated tasks.",
      tag = "PROJECT"
    )
  }

  suspend fun insertTask(task: TaskEntity): Long {
    val id = database.taskDao().insertTask(task)
    logActivity(
      title = "Added Task",
      detail = "Assigned '${task.title}' to ${task.agentRole}",
      tag = "TASK"
    )
    return id
  }

  suspend fun insertTasks(tasks: List<TaskEntity>) {
    database.taskDao().insertTasks(tasks)
    logActivity(
      title = "Batch Tasks Added",
      detail = "Committed ${tasks.size} milestone tasks to backlog.",
      tag = "TASK"
    )
  }

  suspend fun updateTask(task: TaskEntity) {
    database.taskDao().updateTask(task)
  }

  suspend fun toggleTaskStatus(task: TaskEntity) {
    val newStatus = when (task.status) {
      "TODO" -> "IN_PROGRESS"
      "IN_PROGRESS" -> "DONE"
      else -> "TODO"
    }
    val updated = task.copy(status = newStatus)
    database.taskDao().updateTask(updated)
    if (newStatus == "DONE") {
      logActivity(
        title = "Completed Task",
        detail = "'${task.title}' verified by ${task.agentRole}.",
        tag = "TASK"
      )
    }
  }

  suspend fun deleteTask(task: TaskEntity) {
    database.taskDao().deleteTask(task)
  }

  suspend fun insertChatMessage(message: ChatMessageEntity): Long {
    return database.chatMessageDao().insertMessage(message)
  }

  suspend fun markChatMessageApproved(message: ChatMessageEntity) {
    val updated = message.copy(isApproved = true)
    database.chatMessageDao().updateMessage(updated)
    logActivity(
      title = "Plan Approved",
      detail = "Founder approved AI plan proposed by ${message.persona}.",
      tag = "CHAT"
    )
  }

  suspend fun logActivity(title: String, detail: String, tag: String) {
    database.activityDao().insertActivity(
      ActivityEntity(title = title, detail = detail, tag = tag)
    )
  }

  suspend fun clearAllData() {
    database.taskDao().clearAll()
    database.projectDao().clearAll()
    database.chatMessageDao().clearAll()
    database.activityDao().clearAll()
    logActivity(
      title = "System Reset",
      detail = "Reset command center workspace to clean slate.",
      tag = "SYSTEM"
    )
  }
}
