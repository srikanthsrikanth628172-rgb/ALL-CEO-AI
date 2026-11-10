package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.FounderSettings
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
fun SettingsScreen(
  currentSettings: FounderSettings,
  onSaveSettings: (FounderSettings) -> Unit,
  onResetWorkspace: () -> Unit,
  modifier: Modifier = Modifier
) {
  var founderName by remember(currentSettings) { mutableStateOf(currentSettings.founderName) }
  var ventureName by remember(currentSettings) { mutableStateOf(currentSettings.ventureName) }
  var missionStatement by remember(currentSettings) { mutableStateOf(currentSettings.missionStatement) }
  var customInstructions by remember(currentSettings) { mutableStateOf(currentSettings.customAiInstructions) }
  var requireApproval by remember(currentSettings) { mutableStateOf(currentSettings.requireApprovalBeforeAction) }
  var localPersistence by remember(currentSettings) { mutableStateOf(currentSettings.localPersistenceOnly) }

  var showResetDialog by remember { mutableStateOf(false) }
  var showSavedBanner by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Founder Profile Section
    item {
      SectionCard(
        title = "FOUNDER PROFILE",
        icon = Icons.Default.Person,
        accentColor = CyberCyan
      ) {
        OutlinedTextField(
          value = founderName,
          onValueChange = { founderName = it },
          label = { Text("Founder Name / Alias") },
          colors = defaultFieldColors(),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("settings_founder_name_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = ventureName,
          onValueChange = { ventureName = it },
          label = { Text("Venture / Company Name") },
          colors = defaultFieldColors(),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("settings_venture_name_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = missionStatement,
          onValueChange = { missionStatement = it },
          label = { Text("Company Core Mission") },
          colors = defaultFieldColors(),
          modifier = Modifier.fillMaxWidth(),
          maxLines = 3
        )
      }
    }

    // 2. AI Custom Instructions Section
    item {
      SectionCard(
        title = "AI SYSTEM INSTRUCTIONS",
        icon = Icons.Default.Psychology,
        accentColor = ElectricPurple
      ) {
        Text(
          text = "Custom directives injected into the AI CEO and specialist agent models:",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = customInstructions,
          onValueChange = { customInstructions = it },
          placeholder = { Text("e.g. Always structure project plans into 3 phases with assigned agent roles.") },
          colors = defaultFieldColors(),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("settings_custom_instructions_input"),
          minLines = 3,
          maxLines = 6
        )
      }
    }

    // 3. Privacy, Security & Autonomous Control Policies
    item {
      SectionCard(
        title = "SECURITY & AUTONOMY POLICIES",
        icon = Icons.Default.Security,
        accentColor = LaserGreen
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Require Founder Approval",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
              color = TextPrimary
            )
            Text(
              text = "AI must show interactive review cards before modifying backlog or creating tasks.",
              style = MaterialTheme.typography.bodySmall,
              color = TextMuted
            )
          }
          Switch(
            checked = requireApproval,
            onCheckedChange = { requireApproval = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = LaserGreen,
              checkedTrackColor = Color(0xFF0F3628)
            )
          )
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = NeonBorder)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Offline-First Local Vault",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
              color = TextPrimary
            )
            Text(
              text = "All project specs, tasks, and strategy chat stored on-device in Room database.",
              style = MaterialTheme.typography.bodySmall,
              color = TextMuted
            )
          }
          Switch(
            checked = localPersistence,
            onCheckedChange = { localPersistence = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = CyberCyan,
              checkedTrackColor = Color(0xFF0C2C3E)
            )
          )
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = NeonBorder)
        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.Top) {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = WarningAmber,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Zero External Publishing Policy: MY AI SUPERSTAR never connects to external social media accounts or publishes anything automatically without manual verification.",
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
            color = TextSecondary
          )
        }
      }
    }

    // 4. Save Button
    item {
      ElevatedButton(
        onClick = {
          onSaveSettings(
            FounderSettings(
              founderName = founderName,
              ventureName = ventureName,
              missionStatement = missionStatement,
              customAiInstructions = customInstructions,
              requireApprovalBeforeAction = requireApproval,
              localPersistenceOnly = localPersistence
            )
          )
          showSavedBanner = true
        },
        colors = ButtonDefaults.elevatedButtonColors(
          containerColor = CyberCyan,
          contentColor = Color.Black
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("save_settings_button")
      ) {
        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Save All Settings", fontWeight = FontWeight.Bold)
      }

      if (showSavedBanner) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "✓ Configuration saved successfully to local preferences.",
          style = MaterialTheme.typography.bodySmall,
          color = LaserGreen,
          modifier = Modifier.padding(start = 4.dp)
        )
      }
    }

    // 5. How to Use Handbook & Founder Guide
    item {
      SectionCard(
        title = "HOW TO USE MY AI SUPERSTAR",
        icon = Icons.Default.Book,
        accentColor = WarningAmber
      ) {
        val steps = listOf(
          "1. Executive Dashboard" to "View company KPIs (Total Projects, Pending & Completed Tasks) and execute AI CEO's dynamic Next Best Action.",
          "2. Strategic CEO Chat" to "Give open-ended instructions. The AI CEO breaks your vision into small steps and plain-English plans.",
          "3. Plan Approval Gate" to "When the AI CEO proposes a task breakdown, tap 'Approve Plan & Add to Tasks' to commit items to your backlog.",
          "4. 6 Specialist Departments" to "Switch into AI Planner, AI Developer, AI Tester, AI Debugger, AI Researcher, or AI Deployment Assistant for deep-dive domain support.",
          "5. Project Manager" to "Track milestone progress bars, toggle task completion, and create custom software roadmaps with zero overhead."
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          steps.forEach { (heading, explanation) ->
            Column {
              Text(
                text = heading,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = CyberCyan
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = explanation,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                color = TextSecondary
              )
            }
          }
        }
      }
    }

    // 6. Danger Zone: Reset Workspace
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFF221115))
          .border(1.dp, ErrorCrimson.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
          .padding(16.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = null,
              tint = ErrorCrimson,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "DANGER ZONE: RESET WORKSPACE",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              ),
              color = ErrorCrimson
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Resetting clears all projects, tasks, and strategy chat history, returning the command center to a clean slate.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedButton(
            onClick = { showResetDialog = true },
            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = androidx.compose.ui.graphics.SolidColor(ErrorCrimson)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("reset_workspace_button")
          ) {
            Text("Clear & Reset Workspace", color = ErrorCrimson, fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(30.dp))
    }
  }

  if (showResetDialog) {
    AlertDialog(
      onDismissRequest = { showResetDialog = false },
      containerColor = DarkSurface,
      titleContentColor = TextPrimary,
      textContentColor = TextSecondary,
      title = { Text("Confirm Workspace Reset") },
      text = { Text("Are you sure you want to delete all current projects, tasks, and chat history?") },
      confirmButton = {
        ElevatedButton(
          onClick = {
            onResetWorkspace()
            showResetDialog = false
          },
          colors = ButtonDefaults.elevatedButtonColors(
            containerColor = ErrorCrimson,
            contentColor = Color.White
          ),
          modifier = Modifier.testTag("confirm_reset_button")
        ) {
          Text("Yes, Reset Everything")
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetDialog = false }) {
          Text("Cancel", color = TextSecondary)
        }
      }
    )
  }
}

@Composable
private fun SectionCard(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  accentColor: Color,
  content: @Composable () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(DarkSurfaceCard)
      .border(1.dp, NeonBorder, RoundedCornerShape(16.dp))
      .padding(16.dp)
  ) {
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = title,
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          ),
          color = accentColor
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      content()
    }
  }
}

@Composable
private fun defaultFieldColors() = OutlinedTextFieldDefaults.colors(
  focusedContainerColor = DarkSurfaceElevated,
  unfocusedContainerColor = DarkSurfaceElevated,
  focusedBorderColor = CyberCyan,
  unfocusedBorderColor = NeonBorder,
  focusedTextColor = TextPrimary,
  unfocusedTextColor = TextPrimary
)
