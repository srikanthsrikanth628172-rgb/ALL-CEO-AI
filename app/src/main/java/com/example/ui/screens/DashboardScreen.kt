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
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.ActivityEntity
import com.example.data.repository.FounderSettings
import com.example.ui.Screen
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
  founderSettings: FounderSettings,
  totalProjects: Int,
  pendingTasksCount: Int,
  completedTasksCount: Int,
  nextBestAction: String,
  recentActivities: List<ActivityEntity>,
  onNavigate: (Screen) -> Unit,
  onConsultCeo: (prompt: String) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    // 1. Executive Welcome Header
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(
            Brush.horizontalGradient(
              listOf(Color(0xFF0D1E36), Color(0xFF161530))
            )
          )
          .border(1.dp, CyberCyan.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
          .padding(20.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(CyberCyan, ElectricPurple))),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "COMMAND PROTOCOL ACTIVE",
              style = MaterialTheme.typography.labelMedium.copy(
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold
              ),
              color = CyberCyan
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Welcome back, ${founderSettings.founderName}",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = TextPrimary
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "Commanding ${founderSettings.ventureName} with zero employees and full AI executive leverage.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }

    // 2. 4 KPI Metrics Grid (2x2)
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          KpiCard(
            title = "Total Projects",
            value = totalProjects.toString(),
            subtitle = "Active roadmaps",
            icon = Icons.AutoMirrored.Filled.Assignment,
            accentColor = CyberCyan,
            modifier = Modifier.weight(1f),
            testTag = "kpi_total_projects",
            onClick = { onNavigate(Screen.PROJECT_MANAGER) }
          )

          KpiCard(
            title = "Pending Tasks",
            value = pendingTasksCount.toString(),
            subtitle = "Awaiting execution",
            icon = Icons.Default.PendingActions,
            accentColor = WarningAmber,
            modifier = Modifier.weight(1f),
            testTag = "kpi_pending_tasks",
            onClick = { onNavigate(Screen.PROJECT_MANAGER) }
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          KpiCard(
            title = "Completed Tasks",
            value = completedTasksCount.toString(),
            subtitle = "Verified done",
            icon = Icons.Default.CheckCircle,
            accentColor = LaserGreen,
            modifier = Modifier.weight(1f),
            testTag = "kpi_completed_tasks",
            onClick = { onNavigate(Screen.PROJECT_MANAGER) }
          )

          KpiCard(
            title = "AI Agent Status",
            value = "6 Ready",
            subtitle = "All specialist roles online",
            icon = Icons.Default.Groups,
            accentColor = ElectricPurple,
            modifier = Modifier.weight(1f),
            testTag = "kpi_agent_status",
            onClick = { onNavigate(Screen.AGENT_CENTER) }
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }

    // 3. Next Best Action Recommendation Card
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .background(DarkSurfaceElevated)
          .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
          .padding(16.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(CyberCyan.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "AI CEO: NEXT BEST ACTION",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              ),
              color = CyberCyan
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = nextBestAction,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary
          )

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            ElevatedButton(
              onClick = {
                onConsultCeo("How should I execute this immediate next action: $nextBestAction")
                onNavigate(Screen.CEO_CHAT)
              },
              colors = ButtonDefaults.elevatedButtonColors(
                containerColor = CyberCyan,
                contentColor = Color.Black
              ),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("execute_next_action_button")
            ) {
              Icon(
                imageVector = Icons.Default.RocketLaunch,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("Plan in Chat", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
              onClick = { onNavigate(Screen.PROJECT_MANAGER) },
              border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = Brush.linearGradient(listOf(NeonBorder, CyberCyan))),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("view_tasks_button")
            ) {
              Text("View Tasks", color = TextPrimary)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))
    }

    // 4. Quick Actions Row
    item {
      Text(
        text = "COMMAND SHORTCUTS",
        style = MaterialTheme.typography.labelSmall.copy(
          letterSpacing = 1.5.sp,
          fontWeight = FontWeight.Bold
        ),
        color = TextMuted,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        QuickActionButton(
          label = "New Project",
          icon = Icons.Default.Add,
          accentColor = CyberCyan,
          testTag = "quick_action_new_project",
          onClick = { onNavigate(Screen.PROJECT_MANAGER) },
          modifier = Modifier.weight(1f)
        )

        QuickActionButton(
          label = "Consult CEO",
          icon = Icons.Default.Forum,
          accentColor = ElectricPurple,
          testTag = "quick_action_consult_ceo",
          onClick = { onNavigate(Screen.CEO_CHAT) },
          modifier = Modifier.weight(1f)
        )

        QuickActionButton(
          label = "Agent Cards",
          icon = Icons.Default.Groups,
          accentColor = LaserGreen,
          testTag = "quick_action_agent_center",
          onClick = { onNavigate(Screen.AGENT_CENTER) },
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(22.dp))
    }

    // 5. Recent Activity Feed Header
    item {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 4.dp, bottom = 8.dp)
      ) {
        Text(
          text = "RECENT ACTIVITY STREAM",
          style = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.Bold
          ),
          color = TextMuted
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
          text = "${recentActivities.size} Events",
          style = MaterialTheme.typography.labelSmall,
          color = TextMuted
        )
      }
    }

    // Recent Activity List
    if (recentActivities.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceCard)
            .border(1.dp, NeonBorder, RoundedCornerShape(14.dp))
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No recent activity yet. Create a project or consult the AI CEO to begin.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
          )
        }
      }
    } else {
      items(recentActivities, key = { it.id }) { activity ->
        ActivityItemRow(activity)
        Spacer(modifier = Modifier.height(8.dp))
      }
    }

    item {
      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}

@Composable
private fun KpiCard(
  title: String,
  value: String,
  subtitle: String,
  icon: ImageVector,
  accentColor: Color,
  testTag: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .background(DarkSurfaceCard)
      .border(1.dp, NeonBorder, RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
      .padding(14.dp)
      .testTag(testTag)
  ) {
    Column {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(accentColor.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
          text = value,
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = TextPrimary
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
        color = TextPrimary
      )

      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall,
        color = TextMuted
      )
    }
  }
}

@Composable
private fun QuickActionButton(
  label: String,
  icon: ImageVector,
  accentColor: Color,
  testTag: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(DarkSurfaceCard)
      .border(1.dp, NeonBorder, RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .padding(vertical = 12.dp, horizontal = 8.dp)
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(accentColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
        color = TextPrimary
      )
    }
  }
}

@Composable
private fun ActivityItemRow(activity: ActivityEntity) {
  val (badgeColor, badgeText) = when (activity.tag) {
    "PROJECT" -> CyberCyan to "PROJECT"
    "TASK" -> LaserGreen to "TASK"
    "CHAT" -> ElectricPurple to "AI CEO"
    "SETTINGS" -> WarningAmber to "CONFIG"
    else -> TextSecondary to "SYSTEM"
  }

  val formattedTime = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    .format(Date(activity.timestamp))

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(DarkSurfaceCard)
      .border(1.dp, NeonBorder, RoundedCornerShape(12.dp))
      .padding(12.dp)
  ) {
    Row(verticalAlignment = Alignment.Top) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(badgeColor.copy(alpha = 0.15f))
          .padding(horizontal = 6.dp, vertical = 3.dp)
      ) {
        Text(
          text = badgeText,
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          ),
          color = badgeColor
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = activity.title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary
          )
          Spacer(modifier = Modifier.weight(1f))
          Text(
            text = formattedTime,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = TextMuted
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = activity.detail,
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )
      }
    }
  }
}
