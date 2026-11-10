package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiAiService
import com.example.data.local.AppDatabase
import com.example.data.model.AgentRole
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.ProjectTemplate
import com.example.data.model.TaskEntity
import com.example.data.repository.CommandCenterRepository
import com.example.data.repository.FounderSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen(val title: String, val subtitle: String) {
  DASHBOARD("Executive Dashboard", "Command Overview & Metrics"),
  CEO_CHAT("AI CEO Chat", "Strategic Planning & Advisory"),
  AGENT_CENTER("Agent Center", "6 Specialized AI Departments"),
  PROJECT_MANAGER("Project Manager", "Projects, Tasks & Execution"),
  SETTINGS("Settings & Handbook", "Founder Profile & Configuration")
}

class CommandCenterViewModel(application: Application) : AndroidViewModel(application) {
  private val database = AppDatabase.getDatabase(application, viewModelScope)
  val repository = CommandCenterRepository(application, database)
  val aiService = GeminiAiService(application)

  // Navigation State
  private val _currentScreen = MutableStateFlow(Screen.DASHBOARD)
  val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

  // Active persona in CEO Chat
  private val _activePersona = MutableStateFlow("AI CEO")
  val activePersona: StateFlow<String> = _activePersona.asStateFlow()

  // Chat input and generation state
  private val _chatInput = MutableStateFlow("")
  val chatInput: StateFlow<String> = _chatInput.asStateFlow()

  private val _isGenerating = MutableStateFlow(false)
  val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

  // Reactive Data
  val projects: StateFlow<List<ProjectEntity>> = repository.allProjects.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val pendingTasks: StateFlow<List<TaskEntity>> = repository.pendingTasks.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val completedTasks: StateFlow<List<TaskEntity>> = repository.completedTasks.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val pendingTaskCount: StateFlow<Int> = repository.pendingTaskCount.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), 0
  )

  val completedTaskCount: StateFlow<Int> = repository.completedTaskCount.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), 0
  )

  val totalTaskCount: StateFlow<Int> = repository.totalTaskCount.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), 0
  )

  val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.chatMessages.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val recentActivities = repository.recentActivities.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val founderSettings: StateFlow<FounderSettings> = repository.founderSettings

  // Dynamic Next Best Action
  val nextBestAction: StateFlow<String> = combine(
    repository.pendingTasks,
    repository.allProjects
  ) { pendingList, projectList ->
    when {
      projectList.isEmpty() -> "Create your first project template in Project Manager to initialize company roadmap."
      pendingList.isEmpty() -> "All active sprint tasks completed! Consult the AI CEO to plan your next milestone."
      else -> {
        val highPriority = pendingList.find { it.priority == "HIGH" }
        if (highPriority != null) {
          "Execute top priority: '${highPriority.title}' (Assigned to ${highPriority.agentRole})."
        } else {
          "Tackle '${pendingList.first().title}' in ${projectList.first().title}."
        }
      }
    }
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    "Review active projects and assign pending milestone tasks."
  )

  fun navigateTo(screen: Screen) {
    _currentScreen.value = screen
  }

  fun selectPersona(persona: String) {
    _activePersona.value = persona
  }

  fun updateChatInput(text: String) {
    _chatInput.value = text
  }

  fun clearError() {
    _errorMessage.value = null
  }

  fun sendChatMessage(customPrompt: String? = null) {
    val promptToSend = (customPrompt ?: _chatInput.value).trim()
    if (promptToSend.isBlank()) return

    if (customPrompt == null) {
      _chatInput.value = ""
    }

    viewModelScope.launch {
      _isGenerating.value = true
      _errorMessage.value = null

      val persona = _activePersona.value

      // Save user message
      repository.insertChatMessage(
        ChatMessageEntity(
          sender = "USER",
          persona = persona,
          text = promptToSend,
          timestamp = System.currentTimeMillis()
        )
      )

      try {
        val projectsSummary = projects.value.joinToString("; ") { "${it.title} (${it.status})" }
        val settings = founderSettings.value

        val response = aiService.generateStrategicResponse(
          userPrompt = promptToSend,
          persona = persona,
          founderName = settings.founderName,
          founderInstructions = settings.customAiInstructions,
          projectContext = projectsSummary
        )

        val proposedJson = if (response.proposedTasks.isNotEmpty()) {
          aiService.serializeTasksToJson(response.proposedTasks)
        } else null

        repository.insertChatMessage(
          ChatMessageEntity(
            sender = "AI",
            persona = persona,
            text = response.responseText,
            proposedTasksJson = proposedJson,
            isApproved = false,
            timestamp = System.currentTimeMillis()
          )
        )

        repository.logActivity(
          title = "Consulted $persona",
          detail = "Discussed: ${promptToSend.take(45)}...",
          tag = "CHAT"
        )
      } catch (e: Exception) {
        _errorMessage.value = "AI generation notice: ${e.localizedMessage}"
      } finally {
        _isGenerating.value = false
      }
    }
  }

  fun approveProposedPlan(message: ChatMessageEntity, targetProjectId: Long? = null) {
    viewModelScope.launch {
      val json = message.proposedTasksJson ?: return@launch
      val tasks = aiService.deserializeTasksFromJson(json)
      if (tasks.isEmpty()) return@launch

      val effectiveProjectId = targetProjectId ?: projects.value.firstOrNull()?.id ?: run {
        // Create an umbrella project if none exists
        repository.insertProject(
          ProjectEntity(
            title = "Founder Strategic Roadmap",
            description = "Generated from AI CEO strategic action plan.",
            category = "Strategic Plan",
            status = "ACTIVE"
          )
        )
      }

      val taskEntities = tasks.map { proposed ->
        TaskEntity(
          projectId = effectiveProjectId,
          title = proposed.title,
          agentRole = proposed.agentRole,
          status = "TODO",
          priority = proposed.priority,
          createdAt = System.currentTimeMillis()
        )
      }

      repository.insertTasks(taskEntities)
      repository.markChatMessageApproved(message)
    }
  }

  fun createProject(title: String, description: String, category: String, targetDate: String) {
    if (title.isBlank()) return
    viewModelScope.launch {
      repository.insertProject(
        ProjectEntity(
          title = title.trim(),
          description = description.trim(),
          category = category.trim().ifBlank { "General" },
          targetDate = targetDate.trim().ifBlank { "Next Milestone" },
          status = "ACTIVE"
        )
      )
    }
  }

  fun bootstrapFromTemplate(template: ProjectTemplate) {
    viewModelScope.launch {
      val projectId = repository.insertProject(
        ProjectEntity(
          title = template.name,
          description = template.description,
          category = template.category,
          targetDate = template.targetTimeline,
          status = "ACTIVE"
        )
      )

      val taskEntities = template.tasks.map { t ->
        TaskEntity(
          projectId = projectId,
          title = t.title,
          agentRole = t.agentRole,
          status = "TODO",
          priority = t.priority,
          createdAt = System.currentTimeMillis()
        )
      }

      repository.insertTasks(taskEntities)
      repository.logActivity(
        title = "Bootstrapped from Template",
        detail = "Initialized '${template.name}' with ${template.tasks.size} milestone tasks.",
        tag = "PROJECT"
      )
    }
  }

  fun updateProjectStatus(project: ProjectEntity, newStatus: String) {
    viewModelScope.launch {
      repository.updateProject(project.copy(status = newStatus))
    }
  }

  fun editProject(project: ProjectEntity, title: String, description: String, category: String, targetDate: String) {
    if (title.isBlank()) return
    viewModelScope.launch {
      val updated = project.copy(
        title = title.trim(),
        description = description.trim(),
        category = category.trim(),
        targetDate = targetDate.trim()
      )
      repository.updateProject(updated)
      repository.logActivity(
        title = "Edited Project",
        detail = "Updated '${updated.title}' roadmap specifications.",
        tag = "PROJECT"
      )
    }
  }

  fun editTask(task: TaskEntity, title: String, agentRole: String, priority: String) {
    if (title.isBlank()) return
    viewModelScope.launch {
      val updated = task.copy(
        title = title.trim(),
        agentRole = agentRole,
        priority = priority
      )
      repository.updateTask(updated)
      repository.logActivity(
        title = "Edited Task",
        detail = "Updated '${updated.title}' assigned to ${updated.agentRole}.",
        tag = "TASK"
      )
    }
  }

  fun deleteProject(project: ProjectEntity) {
    viewModelScope.launch {
      repository.deleteProject(project)
    }
  }

  fun addTask(projectId: Long, title: String, agentRole: String, priority: String) {
    if (title.isBlank()) return
    viewModelScope.launch {
      repository.insertTask(
        TaskEntity(
          projectId = projectId,
          title = title.trim(),
          agentRole = agentRole,
          status = "TODO",
          priority = priority
        )
      )
    }
  }

  fun toggleTaskStatus(task: TaskEntity) {
    viewModelScope.launch {
      repository.toggleTaskStatus(task)
    }
  }

  fun deleteTask(task: TaskEntity) {
    viewModelScope.launch {
      repository.deleteTask(task)
    }
  }

  fun consultAgentInChat(agent: AgentRole) {
    _activePersona.value = agent.displayName
    _chatInput.value = agent.systemPrompt
    _currentScreen.value = Screen.CEO_CHAT

    viewModelScope.launch {
      try {
        val existing = chatMessages.value.any { it.persona == agent.displayName }
        if (!existing) {
          repository.insertChatMessage(
            ChatMessageEntity(
              sender = "AI",
              persona = agent.displayName,
              text = "⚡ **Dedicated Instance Initialized: ${agent.displayName}** (${agent.title})\n\n**System Prompt Loaded:**\n${agent.systemPrompt}\n\n**Specializations:** ${agent.capabilities.joinToString(" • ")}\n\nReady for your founder instructions.",
              proposedTasksJson = null,
              isApproved = false,
              timestamp = System.currentTimeMillis()
            )
          )
        }
      } catch (e: Exception) {
        // Safe fallback
      }
    }
  }

  fun saveFounderSettings(newSettings: FounderSettings) {
    repository.updateSettings(newSettings)
    viewModelScope.launch {
      repository.logActivity(
        title = "Settings Updated",
        detail = "Updated founder profile & system instructions.",
        tag = "SETTINGS"
      )
    }
  }

  fun resetWorkspace() {
    viewModelScope.launch {
      repository.clearAllData()
    }
  }
}
