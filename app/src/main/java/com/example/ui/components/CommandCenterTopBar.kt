package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Screen
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.LaserGreen
import com.example.ui.theme.NeonBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommandCenterTopBar(
  currentScreen: Screen,
  onOpenDrawer: () -> Unit,
  onAgentStatusClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  TopAppBar(
    modifier = modifier.border(width = 1.dp, color = NeonBorder),
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = DarkSurface,
      titleContentColor = TextPrimary
    ),
    navigationIcon = {
      IconButton(
        onClick = onOpenDrawer,
        modifier = Modifier.testTag("drawer_toggle_button")
      ) {
        Icon(
          imageVector = Icons.Default.Menu,
          contentDescription = "Open Navigation Menu",
          tint = CyberCyan
        )
      }
    },
    title = {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = CyberCyan,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "MY AI SUPERSTAR",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            ),
            color = TextPrimary
          )
        }
        Text(
          text = currentScreen.title,
          style = MaterialTheme.typography.bodySmall,
          color = TextMuted
        )
      }
    },
    actions = {
      // Agent Status Pill
      Box(
        modifier = Modifier
          .padding(end = 12.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFF0F2625))
          .border(1.dp, LaserGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
          .clickable(onClick = onAgentStatusClick)
          .padding(horizontal = 10.dp, vertical = 6.dp)
          .testTag("agent_status_pill"),
        contentAlignment = Alignment.Center
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(LaserGreen)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "6 Ready",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = LaserGreen
          )
        }
      }
    }
  )
}
