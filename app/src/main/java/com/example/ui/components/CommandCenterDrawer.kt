package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
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
import com.example.data.repository.FounderSettings
import com.example.ui.Screen
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.LaserGreen
import com.example.ui.theme.NeonBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CommandCenterDrawer(
  currentScreen: Screen,
  founderSettings: FounderSettings,
  onNavigate: (Screen) -> Unit,
  onCloseDrawer: () -> Unit,
  modifier: Modifier = Modifier
) {
  ModalDrawerSheet(
    modifier = modifier.width(320.dp),
    drawerContainerColor = DarkSurface,
    drawerContentColor = TextPrimary
  ) {
    Column(
      modifier = Modifier
        .fillMaxHeight()
        .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
      // Command Center Header Branding
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(
            Brush.verticalGradient(
              listOf(Color(0xFF142036), Color(0xFF0F1829))
            )
          )
          .border(1.dp, NeonBorder, RoundedCornerShape(16.dp))
          .padding(16.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(CyberCyan, ElectricPurple))),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "MY AI SUPERSTAR",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.ExtraBold,
                  letterSpacing = 1.sp
                ),
                color = CyberCyan
              )
              Text(
                text = "Autonomous AI Command Center",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          HorizontalDivider(color = NeonBorder)
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = null,
              tint = TextMuted,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = founderSettings.founderName,
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
              color = TextPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0D2818))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "SOLO FOUNDER",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = LaserGreen
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
      Text(
        text = "NAVIGATION",
        style = MaterialTheme.typography.labelSmall.copy(
          letterSpacing = 1.5.sp,
          fontWeight = FontWeight.Bold
        ),
        color = TextMuted,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Drawer Destination Items
      DrawerItem(
        label = "Dashboard",
        icon = Icons.Default.Dashboard,
        isSelected = currentScreen == Screen.DASHBOARD,
        testTag = "drawer_item_dashboard",
        onClick = {
          onNavigate(Screen.DASHBOARD)
          onCloseDrawer()
        }
      )

      DrawerItem(
        label = "AI CEO Chat",
        icon = Icons.Default.Forum,
        isSelected = currentScreen == Screen.CEO_CHAT,
        testTag = "drawer_item_ceo_chat",
        onClick = {
          onNavigate(Screen.CEO_CHAT)
          onCloseDrawer()
        }
      )

      DrawerItem(
        label = "Agent Center",
        icon = Icons.Default.Groups,
        isSelected = currentScreen == Screen.AGENT_CENTER,
        testTag = "drawer_item_agent_center",
        onClick = {
          onNavigate(Screen.AGENT_CENTER)
          onCloseDrawer()
        }
      )

      DrawerItem(
        label = "Project Manager",
        icon = Icons.AutoMirrored.Filled.Assignment,
        isSelected = currentScreen == Screen.PROJECT_MANAGER,
        testTag = "drawer_item_project_manager",
        onClick = {
          onNavigate(Screen.PROJECT_MANAGER)
          onCloseDrawer()
        }
      )

      DrawerItem(
        label = "Settings & Handbook",
        icon = Icons.Default.Settings,
        isSelected = currentScreen == Screen.SETTINGS,
        testTag = "drawer_item_settings",
        onClick = {
          onNavigate(Screen.SETTINGS)
          onCloseDrawer()
        }
      )

      Spacer(modifier = Modifier.weight(1f))

      // Footer
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(DarkSurfaceCard)
          .border(1.dp, NeonBorder, RoundedCornerShape(12.dp))
          .padding(12.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(LaserGreen)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "6 Specialist Agents Armed",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
              color = LaserGreen
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "AI CEO Engine: Gemini 3.8 Flash • V1.0",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = TextMuted
          )
        }
      }
    }
  }
}

@Composable
private fun DrawerItem(
  label: String,
  icon: ImageVector,
  isSelected: Boolean,
  testTag: String,
  onClick: () -> Unit
) {
  NavigationDrawerItem(
    label = {
      Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
      )
    },
    icon = {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) CyberCyan else TextSecondary
      )
    },
    selected = isSelected,
    onClick = onClick,
    modifier = Modifier
      .padding(vertical = 4.dp)
      .testTag(testTag),
    colors = NavigationDrawerItemDefaults.colors(
      selectedContainerColor = Color(0xFF00384D),
      selectedTextColor = CyberCyan,
      selectedIconColor = CyberCyan,
      unselectedContainerColor = Color.Transparent,
      unselectedTextColor = TextSecondary,
      unselectedIconColor = TextSecondary
    ),
    shape = RoundedCornerShape(12.dp)
  )
}
