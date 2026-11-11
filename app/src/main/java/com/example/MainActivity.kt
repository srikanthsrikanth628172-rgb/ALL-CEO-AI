package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.ui.CommandCenterViewModel
import com.example.ui.Screen
import com.example.ui.components.CommandCenterDrawer
import com.example.ui.components.CommandCenterTopBar
import com.example.ui.screens.AgentCenterScreen
import com.example.ui.screens.CeoChatScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ProjectManagerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyAiSuperstarTheme
import com.example.ui.theme.ObsidianBackground
import com.example.util.configureAppCheck
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  private val viewModel: CommandCenterViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Mandated: Configure App Check with launch intent before any UI or Firebase operations
    configureAppCheck(this, intent)

    enableEdgeToEdge()

    setContent {
      MyAiSuperstarTheme {
        CommandCenterApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun CommandCenterApp(viewModel: CommandCenterViewModel) {
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  val currentScreen by viewModel.currentScreen.collectAsState()
  val founderSettings by viewModel.founderSettings.collectAsState()
  val totalProjects by viewModel.projects.collectAsState()
  val pendingTasksCount by viewModel.pendingTaskCount.collectAsState()
  val completedTasksCount by viewModel.completedTaskCount.collectAsState()
  val nextBestAction by viewModel.nextBestAction.collectAsState()
  val recentActivities by viewModel.recentActivities.collectAsState()
  val allTasks by viewModel.allTasks.collectAsState()
  val chatMessages by viewModel.chatMessages.collectAsState()
  val activePersona by viewModel.activePersona.collectAsState()
  val selectedModel by viewModel.selectedModel.collectAsState()
  val chatInput by viewModel.chatInput.collectAsState()
  val isGenerating by viewModel.isGenerating.collectAsState()
  val errorMessage by viewModel.errorMessage.collectAsState()

  // Surface errors via snackbar
  LaunchedEffect(errorMessage) {
    errorMessage?.let { error ->
      snackbarHostState.showSnackbar(error)
      viewModel.clearError()
    }
  }

  // Handle system back button gracefully
  BackHandler(enabled = drawerState.isOpen || currentScreen != Screen.DASHBOARD) {
    if (drawerState.isOpen) {
      scope.launch { drawerState.close() }
    } else {
      viewModel.navigateTo(Screen.DASHBOARD)
    }
  }

  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      CommandCenterDrawer(
        currentScreen = currentScreen,
        founderSettings = founderSettings,
        onNavigate = { screen -> viewModel.navigateTo(screen) },
        onCloseDrawer = { scope.launch { drawerState.close() } }
      )
    }
  ) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      containerColor = ObsidianBackground,
      snackbarHost = { SnackbarHost(snackbarHostState) },
      topBar = {
        CommandCenterTopBar(
          currentScreen = currentScreen,
          onOpenDrawer = { scope.launch { drawerState.open() } },
          onAgentStatusClick = { viewModel.navigateTo(Screen.AGENT_CENTER) }
        )
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(ObsidianBackground)
          .padding(innerPadding)
      ) {
        Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
          when (screen) {
            Screen.DASHBOARD -> {
              DashboardScreen(
                founderSettings = founderSettings,
                totalProjects = totalProjects.size,
                pendingTasksCount = pendingTasksCount,
                completedTasksCount = completedTasksCount,
                nextBestAction = nextBestAction,
                recentActivities = recentActivities,
                onNavigate = { dest -> viewModel.navigateTo(dest) },
                onConsultCeo = { prompt ->
                  viewModel.updateChatInput(prompt)
                  viewModel.selectPersona("AI CEO")
                }
              )
            }

            Screen.CEO_CHAT -> {
              CeoChatScreen(
                chatMessages = chatMessages,
                activePersona = activePersona,
                selectedModel = selectedModel,
                chatInput = chatInput,
                isGenerating = isGenerating,
                errorMessage = errorMessage,
                aiService = viewModel.aiService,
                onPersonaSelect = { persona -> viewModel.selectPersona(persona) },
                onModelSelect = { model -> viewModel.selectModel(model) },
                onInputChange = { text -> viewModel.updateChatInput(text) },
                onSendMessage = { customPrompt -> viewModel.sendChatMessage(customPrompt) },
                onApprovePlan = { message -> viewModel.approveProposedPlan(message) }
              )
            }

            Screen.AGENT_CENTER -> {
              AgentCenterScreen(
                projects = totalProjects,
                onConsultAgent = { agent -> viewModel.consultAgentInChat(agent) },
                onAssignTask = { projId, title, role, prio ->
                  viewModel.addTask(projId, title, role, prio)
                }
              )
            }

            Screen.PROJECT_MANAGER -> {
              ProjectManagerScreen(
                projects = totalProjects,
                allTasks = allTasks,
                onCreateProject = { title, desc, cat, date ->
                  viewModel.createProject(title, desc, cat, date)
                },
                onBootstrapTemplate = { template ->
                  viewModel.bootstrapFromTemplate(template)
                },
                onUpdateProjectStatus = { project, newStatus ->
                  viewModel.updateProjectStatus(project, newStatus)
                },
                onEditProject = { project, title, desc, cat, date ->
                  viewModel.editProject(project, title, desc, cat, date)
                },
                onDeleteProject = { project ->
                  viewModel.deleteProject(project)
                },
                onAddTask = { projId, title, role, prio ->
                  viewModel.addTask(projId, title, role, prio)
                },
                onEditTask = { task, title, role, prio ->
                  viewModel.editTask(task, title, role, prio)
                },
                onToggleTaskStatus = { task ->
                  viewModel.toggleTaskStatus(task)
                },
                onDeleteTask = { task ->
                  viewModel.deleteTask(task)
                }
              )
            }

            Screen.SETTINGS -> {
              SettingsScreen(
                currentSettings = founderSettings,
                onSaveSettings = { updated -> viewModel.saveFounderSettings(updated) },
                onResetWorkspace = { viewModel.resetWorkspace() }
              )
            }
          }
        }
      }
    }
  }
}
