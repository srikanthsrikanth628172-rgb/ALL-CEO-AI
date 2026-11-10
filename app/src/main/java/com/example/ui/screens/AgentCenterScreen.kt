package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentRole
import com.example.data.model.ProjectEntity
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.LaserGreen
import com.example.ui.theme.NeonBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun AgentCenterScreen(
  projects: List<ProjectEntity>,
  onConsultAgent: (AgentRole) -> Unit,
  onAssignTask: (projectId: Long, title: String, agentRole: String, priority: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var agentForTaskDispatch by remember { mutableStateOf<AgentRole?>(null) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Transparent Architecture Notice Banner
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFF0F1A2E))
          .border(1.dp, NeonBorder, RoundedCornerShape(16.dp))
          .padding(16.dp)
      ) {
        Row(verticalAlignment = Alignment.Top) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = CyberCyan,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "UNIFIED MODEL ARCHITECTURE (V1)",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              ),
              color = CyberCyan
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "All 6 specialist roles are powered by a unified Gemini intelligence model with domain-specific system prompts. They provide focused expertise for planning, coding, QA, debugging, research, and cloud deployment.",
              style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
              color = TextSecondary
            )
          }
        }
      }
    }

    // 2. 6 Specialist Agent Cards
    items(AgentRole.entries, key = { it.id }) { agent ->
      AgentCard(
        agent = agent,
        onConsult = { onConsultAgent(agent) },
        onDispatchTask = { agentForTaskDispatch = agent }
      )
    }

    item {
      Spacer(modifier = Modifier.height(30.dp))
    }
  }

  // Task Dispatch Dialog
  agentForTaskDispatch?.let { agent ->
    DispatchTaskDialog(
      agent = agent,
      projects = projects,
      onDismiss = { agentForTaskDispatch = null },
      onConfirm = { projectId, title, priority ->
        onAssignTask(projectId, title, agent.displayName, priority)
        agentForTaskDispatch = null
      }
    )
  }
}

@Composable
private fun AgentCard(
  agent: AgentRole,
  onConsult: () -> Unit,
  onDispatchTask: () -> Unit
) {
  val (icon, accentColor) = when (agent) {
    AgentRole.AI_PLANNER -> Icons.Default.ViewTimeline to CyberCyan
    AgentRole.AI_DEVELOPER -> Icons.Default.Code to ElectricPurple
    AgentRole.AI_TESTER -> Icons.AutoMirrored.Filled.FactCheck to LaserGreen
    AgentRole.AI_DEBUGGER -> Icons.Default.Build to WarningAmber
    AgentRole.AI_RESEARCHER -> Icons.Default.Search to Color(0xFF38BDF8)
    AgentRole.AI_DEPLOYMENT -> Icons.Default.RocketLaunch to Color(0xFFA78BFA)
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(DarkSurfaceCard)
      .border(1.dp, NeonBorder, RoundedCornerShape(18.dp))
      .clickable(onClick = onConsult)
      .padding(16.dp)
      .testTag("agent_card_${agent.id}")
  ) {
    Column {
      // Header: Avatar, Name, Title, and Status Pill
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(accentColor.copy(alpha = 0.15f))
            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(24.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = agent.displayName,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
          )
          Text(
            text = agent.title,
            style = MaterialTheme.typography.labelSmall,
            color = accentColor
          )
        }

        // Online Status Indicator
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0F2625))
            .border(1.dp, LaserGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(LaserGreen)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = "Ready",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = LaserGreen
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Dedicated Chat Prompt Indicator Banner
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF0D172A))
          .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
          .padding(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.FlashOn,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Tap card to open dedicated chat with pre-filled system prompt",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Medium),
            color = accentColor
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Description
      Text(
        text = agent.description,
        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
        color = TextSecondary
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Capabilities Chips Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        agent.capabilities.take(3).forEach { cap ->
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(DarkSurfaceElevated)
              .border(1.dp, NeonBorder, RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = cap,
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = TextMuted
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        ElevatedButton(
          onClick = onConsult,
          colors = ButtonDefaults.elevatedButtonColors(
            containerColor = accentColor,
            contentColor = Color.Black
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("consult_button_${agent.id}")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Chat,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Dedicated Chat", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onDispatchTask,
          shape = RoundedCornerShape(10.dp),
          border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
            brush = Brush.linearGradient(listOf(NeonBorder, accentColor.copy(alpha = 0.5f)))
          ),
          modifier = Modifier
            .weight(1f)
            .testTag("dispatch_button_${agent.id}")
        ) {
          Icon(
            imageVector = Icons.Default.AssignmentTurnedIn,
            contentDescription = null,
            tint = TextPrimary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Assign Task", color = TextPrimary)
        }
      }
    }
  }
}

@Composable
private fun DispatchTaskDialog(
  agent: AgentRole,
  projects: List<ProjectEntity>,
  onDismiss: () -> Unit,
  onConfirm: (projectId: Long, title: String, priority: String) -> Unit
) {
  var taskTitle by remember { mutableStateOf("") }
  var selectedPriority by remember { mutableStateOf("HIGH") }
  var selectedProjectId by remember { mutableStateOf(projects.firstOrNull()?.id ?: 0L) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = DarkSurface,
    titleContentColor = TextPrimary,
    textContentColor = TextSecondary,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AssignmentTurnedIn,
          contentDescription = null,
          tint = CyberCyan,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Assign Task to ${agent.displayName}")
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
          text = "Create a direct milestone task assigned to ${agent.displayName} in your Project Manager backlog.",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )

        OutlinedTextField(
          value = taskTitle,
          onValueChange = { taskTitle = it },
          label = { Text("Task Title") },
          placeholder = { Text(agent.promptSample) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = DarkSurfaceCard,
            unfocusedContainerColor = DarkSurfaceCard,
            focusedBorderColor = CyberCyan,
            unfocusedBorderColor = NeonBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("dispatch_task_title_input")
        )

        // Target Project Selection
        Text(
          text = "Select Project:",
          style = MaterialTheme.typography.labelSmall,
          color = TextMuted
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          projects.forEach { proj ->
            val isSelected = proj.id == selectedProjectId
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) CyberCyan.copy(alpha = 0.15f) else DarkSurfaceCard)
                .border(1.dp, if (isSelected) CyberCyan else NeonBorder, RoundedCornerShape(8.dp))
                .clickable { selectedProjectId = proj.id }
                .padding(10.dp)
            ) {
              Text(
                text = proj.title,
                style = MaterialTheme.typography.bodySmall.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) CyberCyan else TextPrimary
              )
            }
          }
        }

        // Priority Selector
        Text(
          text = "Priority Level:",
          style = MaterialTheme.typography.labelSmall,
          color = TextMuted
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("HIGH", "MEDIUM", "LOW").forEach { prio ->
            val isSelected = prio == selectedPriority
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) ElectricPurple.copy(alpha = 0.2f) else DarkSurfaceCard)
                .border(1.dp, if (isSelected) ElectricPurple else NeonBorder, RoundedCornerShape(8.dp))
                .clickable { selectedPriority = prio }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = prio,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) ElectricPurple else TextSecondary
              )
            }
          }
        }
      }
    },
    confirmButton = {
      ElevatedButton(
        onClick = {
          if (taskTitle.isNotBlank()) {
            onConfirm(selectedProjectId, taskTitle, selectedPriority)
          }
        },
        enabled = taskTitle.isNotBlank(),
        colors = ButtonDefaults.elevatedButtonColors(
          containerColor = CyberCyan,
          contentColor = Color.Black
        ),
        modifier = Modifier.testTag("confirm_assign_task_button")
      ) {
        Text("Assign Task", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    }
  )
}
