package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentRole
import com.example.data.model.ProjectEntity
import com.example.data.model.ProjectTemplate
import com.example.data.model.TaskEntity
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.ErrorCrimson
import com.example.ui.theme.LaserGreen
import com.example.ui.theme.NeonBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun ProjectManagerScreen(
  projects: List<ProjectEntity>,
  allTasks: List<TaskEntity>,
  onCreateProject: (title: String, description: String, category: String, targetDate: String) -> Unit,
  onBootstrapTemplate: (ProjectTemplate) -> Unit,
  onUpdateProjectStatus: (project: ProjectEntity, newStatus: String) -> Unit,
  onEditProject: (project: ProjectEntity, title: String, description: String, category: String, targetDate: String) -> Unit,
  onDeleteProject: (project: ProjectEntity) -> Unit,
  onAddTask: (projectId: Long, title: String, agentRole: String, priority: String) -> Unit,
  onEditTask: (task: TaskEntity, title: String, agentRole: String, priority: String) -> Unit,
  onToggleTaskStatus: (TaskEntity) -> Unit,
  onDeleteTask: (TaskEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  var showCreateProjectDialog by remember { mutableStateOf(false) }
  var projectForNewTask by remember { mutableStateOf<ProjectEntity?>(null) }
  var projectForEdit by remember { mutableStateOf<ProjectEntity?>(null) }
  var taskForEdit by remember { mutableStateOf<TaskEntity?>(null) }
  var filterStatus by remember { mutableStateOf("ALL") } // ALL, ACTIVE, COMPLETED

  val filteredProjects = projects.filter {
    when (filterStatus) {
      "ACTIVE" -> it.status == "ACTIVE"
      "COMPLETED" -> it.status == "COMPLETED"
      else -> true
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 10.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Instant Bootstrap Templates Carousel
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(DarkSurfaceElevated)
          .border(1.dp, NeonBorder, RoundedCornerShape(16.dp))
          .padding(14.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(WarningAmber.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.FlashOn,
              contentDescription = null,
              tint = WarningAmber,
              modifier = Modifier.size(16.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "BOOTSTRAP STARTER TEMPLATES",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            ),
            color = CyberCyan
          )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Instantly launch full project roadmaps pre-bundled with milestone tasks assigned to your AI agents:",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          contentPadding = PaddingValues(vertical = 4.dp)
        ) {
          items(ProjectTemplate.TEMPLATES, key = { it.id }) { template ->
            TemplateCarouselCard(
              template = template,
              onBootstrap = { onBootstrapTemplate(template) }
            )
          }
        }
      }
    }

    // 2. Filter Bar & New Project Button
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
          listOf("ALL" to "All", "ACTIVE" to "Active", "COMPLETED" to "Done").forEach { (key, label) ->
            val isSelected = filterStatus == key
            FilterChip(
              selected = isSelected,
              onClick = { filterStatus = key },
              label = { Text(label, style = MaterialTheme.typography.labelSmall) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                selectedLabelColor = CyberCyan,
                containerColor = DarkSurfaceCard,
                labelColor = TextSecondary
              ),
              border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = isSelected,
                borderColor = if (isSelected) CyberCyan else NeonBorder
              ),
              modifier = Modifier.testTag("filter_chip_$key")
            )
          }
        }

        ElevatedButton(
          onClick = { showCreateProjectDialog = true },
          colors = ButtonDefaults.elevatedButtonColors(
            containerColor = CyberCyan,
            contentColor = Color.Black
          ),
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.testTag("create_project_button")
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("New Project", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
        }
      }
    }

    // 3. Projects & Tasks List
    if (filteredProjects.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceCard)
            .border(1.dp, NeonBorder, RoundedCornerShape(16.dp))
            .padding(28.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Assignment,
              contentDescription = null,
              tint = TextMuted,
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "No projects found matching filter.",
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Choose a template above or tap 'New Project' to create one.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }
        }
      }
    } else {
      items(filteredProjects, key = { it.id }) { project ->
        val projectTasks = allTasks.filter { it.projectId == project.id }
        ProjectCard(
          project = project,
          tasks = projectTasks,
          onAddTask = { projectForNewTask = project },
          onEditProject = { projectForEdit = project },
          onEditTask = { task -> taskForEdit = task },
          onToggleTask = onToggleTaskStatus,
          onDeleteTask = onDeleteTask,
          onUpdateStatus = { newStatus -> onUpdateProjectStatus(project, newStatus) },
          onDeleteProject = { onDeleteProject(project) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(30.dp))
    }
  }

  // Dialog 1: Create Project Dialog
  if (showCreateProjectDialog) {
    CreateProjectDialog(
      onDismiss = { showCreateProjectDialog = false },
      onConfirm = { title, desc, cat, date ->
        onCreateProject(title, desc, cat, date)
        showCreateProjectDialog = false
      },
      onBootstrap = { template ->
        onBootstrapTemplate(template)
        showCreateProjectDialog = false
      }
    )
  }

  // Dialog 2: Add Task Dialog
  projectForNewTask?.let { project ->
    AddTaskDialog(
      project = project,
      onDismiss = { projectForNewTask = null },
      onConfirm = { title, agentRole, priority ->
        onAddTask(project.id, title, agentRole, priority)
        projectForNewTask = null
      }
    )
  }

  // Dialog 3: Edit Project Dialog
  projectForEdit?.let { project ->
    EditProjectDialog(
      project = project,
      onDismiss = { projectForEdit = null },
      onConfirm = { title, desc, cat, date ->
        onEditProject(project, title, desc, cat, date)
        projectForEdit = null
      }
    )
  }

  // Dialog 4: Edit Task Dialog
  taskForEdit?.let { task ->
    EditTaskDialog(
      task = task,
      onDismiss = { taskForEdit = null },
      onConfirm = { title, role, prio ->
        onEditTask(task, title, role, prio)
        taskForEdit = null
      }
    )
  }
}

@Composable
private fun TemplateCarouselCard(
  template: ProjectTemplate,
  onBootstrap: () -> Unit
) {
  val accentColor = when (template.id) {
    "web_saas" -> CyberCyan
    "mobile_app" -> ElectricPurple
    "ai_prototype" -> LaserGreen
    else -> WarningAmber
  }

  Box(
    modifier = Modifier
      .width(220.dp)
      .clip(RoundedCornerShape(14.dp))
      .background(DarkSurfaceCard)
      .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
      .padding(12.dp)
      .testTag("template_card_${template.id}")
  ) {
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(accentColor.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = template.category.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
            color = accentColor
          )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
          text = "${template.tasks.size} Tasks",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
          color = TextMuted
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = template.name,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = TextPrimary
      )

      Text(
        text = template.targetTimeline,
        style = MaterialTheme.typography.labelSmall,
        color = accentColor
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = template.tagline,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 14.sp),
        color = TextSecondary,
        maxLines = 2
      )

      Spacer(modifier = Modifier.height(10.dp))

      ElevatedButton(
        onClick = onBootstrap,
        colors = ButtonDefaults.elevatedButtonColors(
          containerColor = accentColor,
          contentColor = Color.Black
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("bootstrap_button_${template.id}")
      ) {
        Icon(
          imageVector = Icons.Default.RocketLaunch,
          contentDescription = null,
          modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text("Bootstrap", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
      }
    }
  }
}

@Composable
private fun ProjectCard(
  project: ProjectEntity,
  tasks: List<TaskEntity>,
  onAddTask: () -> Unit,
  onEditProject: () -> Unit,
  onEditTask: (TaskEntity) -> Unit,
  onToggleTask: (TaskEntity) -> Unit,
  onDeleteTask: (TaskEntity) -> Unit,
  onUpdateStatus: (String) -> Unit,
  onDeleteProject: () -> Unit
) {
  var isExpanded by remember { mutableStateOf(true) }

  val completedCount = tasks.count { it.status == "DONE" }
  val totalCount = tasks.size
  val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

  val statusColor = when (project.status) {
    "COMPLETED" -> LaserGreen
    "ON_HOLD" -> WarningAmber
    else -> CyberCyan
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(DarkSurfaceCard)
      .border(1.dp, NeonBorder, RoundedCornerShape(16.dp))
      .padding(14.dp)
      .testTag("project_card_${project.id}")
  ) {
    Column {
      // Header: Title, Category, Status, Target Date
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = project.title,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = TextPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(statusColor.copy(alpha = 0.15f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = project.status,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = statusColor
              )
            }
          }

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = "${project.category} • Target: ${project.targetDate}",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
          )
        }

        IconButton(
          onClick = onEditProject,
          modifier = Modifier
            .size(28.dp)
            .testTag("edit_project_button_${project.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit Project",
            tint = CyberCyan,
            modifier = Modifier.size(16.dp)
          )
        }

        IconButton(
          onClick = { isExpanded = !isExpanded },
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = "Toggle tasks view",
            tint = TextSecondary
          )
        }
      }

      if (project.description.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = project.description,
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Progress Bar & Counter
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier
            .weight(1f)
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = statusColor,
          trackColor = DarkSurfaceElevated
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = "$completedCount/$totalCount Done (${(progress * 100).toInt()}%)",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
          color = TextSecondary
        )
      }

      // Tasks List Section (Expandable)
      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(top = 12.dp)) {
          HorizontalDivider(color = NeonBorder)
          Spacer(modifier = Modifier.height(8.dp))

          if (tasks.isEmpty()) {
            Text(
              text = "No tasks yet. Tap '+ Add Task' to assign items to your AI agents.",
              style = MaterialTheme.typography.bodySmall,
              color = TextMuted,
              modifier = Modifier.padding(vertical = 6.dp)
            )
          } else {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              tasks.forEach { task ->
                TaskRowItem(
                  task = task,
                  onToggle = { onToggleTask(task) },
                  onEdit = { onEditTask(task) },
                  onDelete = { onDeleteTask(task) }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Card Action Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = onAddTask,
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.testTag("add_task_to_project_${project.id}")
            ) {
              Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("Add Task", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
            }

            if (project.status != "COMPLETED") {
              OutlinedButton(
                onClick = { onUpdateStatus("COMPLETED") },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = LaserGreen,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Mark Completed", style = MaterialTheme.typography.labelSmall, color = LaserGreen)
              }
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
              onClick = onDeleteProject,
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Project",
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun TaskRowItem(
  task: TaskEntity,
  onToggle: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  val isDone = task.status == "DONE"

  val priorityColor = when (task.priority) {
    "HIGH" -> WarningAmber
    "LOW" -> TextMuted
    else -> ElectricPurple
  }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(DarkSurfaceElevated)
      .clickable(onClick = onToggle)
      .padding(horizontal = 10.dp, vertical = 6.dp)
      .testTag("task_item_${task.id}")
  ) {
    Icon(
      imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
      contentDescription = if (isDone) "Mark Pending" else "Mark Done",
      tint = if (isDone) LaserGreen else TextMuted,
      modifier = Modifier.size(18.dp)
    )

    Spacer(modifier = Modifier.width(10.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = task.title,
        style = MaterialTheme.typography.bodySmall.copy(
          fontWeight = FontWeight.Medium,
          textDecoration = if (isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
        ),
        color = if (isDone) TextMuted else TextPrimary
      )

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = task.agentRole,
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
          color = CyberCyan
        )
        Text(
          text = " • ",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
          color = TextMuted
        )
        Text(
          text = task.priority,
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
          color = priorityColor
        )
      }
    }

    IconButton(
      onClick = onEdit,
      modifier = Modifier
        .size(24.dp)
        .testTag("edit_task_button_${task.id}")
    ) {
      Icon(
        imageVector = Icons.Default.Edit,
        contentDescription = "Edit task",
        tint = CyberCyan.copy(alpha = 0.8f),
        modifier = Modifier.size(14.dp)
      )
    }

    IconButton(
      onClick = onDelete,
      modifier = Modifier
        .size(24.dp)
        .testTag("delete_task_button_${task.id}")
    ) {
      Icon(
        imageVector = Icons.Default.Delete,
        contentDescription = "Delete task",
        tint = TextMuted.copy(alpha = 0.6f),
        modifier = Modifier.size(14.dp)
      )
    }
  }
}

@Composable
private fun CreateProjectDialog(
  onDismiss: () -> Unit,
  onConfirm: (title: String, description: String, category: String, targetDate: String) -> Unit,
  onBootstrap: (ProjectTemplate) -> Unit
) {
  var activeTab by remember { mutableStateOf("TEMPLATE") } // TEMPLATE or CUSTOM
  var selectedTemplate by remember { mutableStateOf(ProjectTemplate.TEMPLATES.first()) }

  var customTitle by remember { mutableStateOf("") }
  var customDescription by remember { mutableStateOf("") }
  var customCategory by remember { mutableStateOf("SaaS") }
  var customTargetDate by remember { mutableStateOf("Next Milestone") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = DarkSurface,
    titleContentColor = TextPrimary,
    textContentColor = TextSecondary,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Assignment,
          contentDescription = null,
          tint = CyberCyan,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Create Project")
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Mode Switcher Tabs
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceElevated)
            .padding(2.dp)
        ) {
          listOf("TEMPLATE" to "Starter Template", "CUSTOM" to "Custom Blank").forEach { (tabKey, tabLabel) ->
            val isSelected = activeTab == tabKey
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) CyberCyan.copy(alpha = 0.2f) else Color.Transparent)
                .clickable { activeTab = tabKey }
                .padding(vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = tabLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) CyberCyan else TextSecondary
              )
            }
          }
        }

        if (activeTab == "TEMPLATE") {
          Text("Select Template:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ProjectTemplate.TEMPLATES.forEach { template ->
              val isSelected = selectedTemplate.id == template.id
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) CyberCyan.copy(alpha = 0.12f) else DarkSurfaceCard)
                  .border(1.dp, if (isSelected) CyberCyan else NeonBorder, RoundedCornerShape(8.dp))
                  .clickable { selectedTemplate = template }
                  .padding(10.dp)
              ) {
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = template.name,
                      style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                      ),
                      color = if (isSelected) CyberCyan else TextPrimary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                      text = "${template.tasks.size} Tasks",
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                      color = TextMuted
                    )
                  }
                  Text(
                    text = template.tagline,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                  )
                }
              }
            }
          }
        } else {
          OutlinedTextField(
            value = customTitle,
            onValueChange = { customTitle = it },
            label = { Text("Project Title") },
            placeholder = { Text("e.g. AI Workflow Orchestrator") },
            colors = defaultFieldColors(),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("new_project_title_input")
          )

          OutlinedTextField(
            value = customDescription,
            onValueChange = { customDescription = it },
            label = { Text("Core Objective") },
            placeholder = { Text("What milestone are we shipping?") },
            colors = defaultFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = customCategory,
              onValueChange = { customCategory = it },
              label = { Text("Category") },
              colors = defaultFieldColors(),
              modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
              value = customTargetDate,
              onValueChange = { customTargetDate = it },
              label = { Text("Target Date") },
              colors = defaultFieldColors(),
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    },
    confirmButton = {
      ElevatedButton(
        onClick = {
          if (activeTab == "TEMPLATE") {
            onBootstrap(selectedTemplate)
          } else if (customTitle.isNotBlank()) {
            onConfirm(customTitle, customDescription, customCategory, customTargetDate)
          }
        },
        enabled = activeTab == "TEMPLATE" || customTitle.isNotBlank(),
        colors = ButtonDefaults.elevatedButtonColors(
          containerColor = CyberCyan,
          contentColor = Color.Black
        ),
        modifier = Modifier.testTag("confirm_create_project_button")
      ) {
        Text(
          text = if (activeTab == "TEMPLATE") "Bootstrap Template" else "Create Project",
          fontWeight = FontWeight.Bold
        )
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    }
  )
}

@Composable
private fun AddTaskDialog(
  project: ProjectEntity,
  onDismiss: () -> Unit,
  onConfirm: (title: String, agentRole: String, priority: String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var selectedRole by remember { mutableStateOf("AI Developer") }
  var selectedPriority by remember { mutableStateOf("HIGH") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = DarkSurface,
    titleContentColor = TextPrimary,
    textContentColor = TextSecondary,
    title = {
      Text("Add Task to ${project.title}")
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Task Description") },
          placeholder = { Text("e.g. Implement OAuth2 flow") },
          colors = defaultFieldColors(),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("add_task_title_input")
        )

        Text("Assign to Agent Role:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          AgentRole.entries.forEach { role ->
            val isSelected = selectedRole == role.displayName
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) CyberCyan.copy(alpha = 0.15f) else DarkSurfaceCard)
                .border(1.dp, if (isSelected) CyberCyan else NeonBorder, RoundedCornerShape(6.dp))
                .clickable { selectedRole = role.displayName }
                .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
              Text(
                text = "${role.displayName} (${role.title})",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) CyberCyan else TextPrimary
              )
            }
          }
        }

        Text("Priority:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          listOf("HIGH", "MEDIUM", "LOW").forEach { prio ->
            val isSelected = selectedPriority == prio
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) ElectricPurple.copy(alpha = 0.2f) else DarkSurfaceCard)
                .border(1.dp, if (isSelected) ElectricPurple else NeonBorder, RoundedCornerShape(6.dp))
                .clickable { selectedPriority = prio }
                .padding(vertical = 6.dp),
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
          if (title.isNotBlank()) {
            onConfirm(title, selectedRole, selectedPriority)
          }
        },
        enabled = title.isNotBlank(),
        colors = ButtonDefaults.elevatedButtonColors(
          containerColor = CyberCyan,
          contentColor = Color.Black
        ),
        modifier = Modifier.testTag("confirm_add_task_button")
      ) {
        Text("Add Task", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    }
  )
}

@Composable
private fun defaultFieldColors() = OutlinedTextFieldDefaults.colors(
  focusedContainerColor = DarkSurfaceCard,
  unfocusedContainerColor = DarkSurfaceCard,
  focusedBorderColor = CyberCyan,
  unfocusedBorderColor = NeonBorder,
  focusedTextColor = TextPrimary,
  unfocusedTextColor = TextPrimary
)

@Composable
private fun EditProjectDialog(
  project: ProjectEntity,
  onDismiss: () -> Unit,
  onConfirm: (title: String, description: String, category: String, targetDate: String) -> Unit
) {
  var title by remember { mutableStateOf(project.title) }
  var description by remember { mutableStateOf(project.description) }
  var category by remember { mutableStateOf(project.category) }
  var targetDate by remember { mutableStateOf(project.targetDate) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = DarkSurface,
    titleContentColor = TextPrimary,
    textContentColor = TextSecondary,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Edit,
          contentDescription = null,
          tint = CyberCyan,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Edit Project")
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Project Title") },
          colors = defaultFieldColors(),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("edit_project_title_input")
        )

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Core Objective") },
          colors = defaultFieldColors(),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("edit_project_desc_input")
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            colors = defaultFieldColors(),
            modifier = Modifier.weight(1f)
          )

          OutlinedTextField(
            value = targetDate,
            onValueChange = { targetDate = it },
            label = { Text("Target Date") },
            colors = defaultFieldColors(),
            modifier = Modifier.weight(1f)
          )
        }
      }
    },
    confirmButton = {
      ElevatedButton(
        onClick = {
          if (title.isNotBlank()) {
            onConfirm(title, description, category, targetDate)
          }
        },
        enabled = title.isNotBlank(),
        colors = ButtonDefaults.elevatedButtonColors(
          containerColor = CyberCyan,
          contentColor = Color.Black
        ),
        modifier = Modifier.testTag("confirm_edit_project_button")
      ) {
        Text("Save Changes", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    }
  )
}

@Composable
private fun EditTaskDialog(
  task: TaskEntity,
  onDismiss: () -> Unit,
  onConfirm: (title: String, agentRole: String, priority: String) -> Unit
) {
  var title by remember { mutableStateOf(task.title) }
  var selectedRole by remember { mutableStateOf(task.agentRole) }
  var selectedPriority by remember { mutableStateOf(task.priority) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = DarkSurface,
    titleContentColor = TextPrimary,
    textContentColor = TextSecondary,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Edit,
          contentDescription = null,
          tint = CyberCyan,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Edit Task")
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Task Description") },
          colors = defaultFieldColors(),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("edit_task_title_input")
        )

        Text("Assign to Agent Role:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          AgentRole.entries.forEach { role ->
            val isSelected = selectedRole == role.displayName
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) CyberCyan.copy(alpha = 0.15f) else DarkSurfaceCard)
                .border(1.dp, if (isSelected) CyberCyan else NeonBorder, RoundedCornerShape(6.dp))
                .clickable { selectedRole = role.displayName }
                .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
              Text(
                text = "${role.displayName} (${role.title})",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) CyberCyan else TextPrimary
              )
            }
          }
        }

        Text("Priority:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          listOf("HIGH", "MEDIUM", "LOW").forEach { prio ->
            val isSelected = selectedPriority == prio
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) ElectricPurple.copy(alpha = 0.2f) else DarkSurfaceCard)
                .border(1.dp, if (isSelected) ElectricPurple else NeonBorder, RoundedCornerShape(6.dp))
                .clickable { selectedPriority = prio }
                .padding(vertical = 6.dp),
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
          if (title.isNotBlank()) {
            onConfirm(title, selectedRole, selectedPriority)
          }
        },
        enabled = title.isNotBlank(),
        colors = ButtonDefaults.elevatedButtonColors(
          containerColor = CyberCyan,
          contentColor = Color.Black
        ),
        modifier = Modifier.testTag("confirm_edit_task_button")
      ) {
        Text("Save Task", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    }
  )
}

