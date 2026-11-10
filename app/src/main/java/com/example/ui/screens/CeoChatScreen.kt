package com.example.ui.screens

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.ai.GeminiAiService
import com.example.data.ai.ProposedTask
import com.example.data.model.AgentRole
import com.example.data.model.ChatMessageEntity
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
fun CeoChatScreen(
  chatMessages: List<ChatMessageEntity>,
  activePersona: String,
  chatInput: String,
  isGenerating: Boolean,
  errorMessage: String?,
  aiService: GeminiAiService,
  onPersonaSelect: (String) -> Unit,
  onInputChange: (String) -> Unit,
  onSendMessage: (customPrompt: String?) -> Unit,
  onApprovePlan: (ChatMessageEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  val listState = rememberLazyListState()

  // Scroll to bottom when new messages arrive
  LaunchedEffect(chatMessages.size, isGenerating) {
    if (chatMessages.isNotEmpty()) {
      listState.animateScrollToItem(chatMessages.size - 1)
    }
  }

  val allPersonas = listOf("AI CEO") + AgentRole.entries.map { it.displayName }

  val suggestionChips = listOf(
    "Break down my MVP into steps",
    "Suggest next best action",
    "Explain architecture in simple English",
    "Draft a production sprint plan",
    "Review edge cases & risk matrix"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .imePadding()
  ) {
    // 1. Specialist Role Persona Bar
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(DarkSurfaceElevated)
        .padding(vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.SmartToy,
          contentDescription = null,
          tint = CyberCyan,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "ACTIVE CONSULTANT",
          style = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold
          ),
          color = TextSecondary
        )
      }

      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 4.dp)
      ) {
        items(allPersonas) { persona ->
          val isSelected = persona.equals(activePersona, ignoreCase = true)
          FilterChip(
            selected = isSelected,
            onClick = { onPersonaSelect(persona) },
            label = {
              Text(
                text = persona,
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              )
            },
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
            modifier = Modifier.testTag("persona_chip_${persona.replace(" ", "_")}")
          )
        }
      }
    }

    val activeAgent = AgentRole.entries.find { it.displayName.equals(activePersona, ignoreCase = true) }
    var isSystemPromptExpanded by remember(activePersona) { mutableStateOf(false) }

    if (activeAgent != null) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(DarkSurface)
          .border(1.dp, NeonBorder)
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .testTag("dedicated_instance_banner")
      ) {
        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .fillMaxWidth()
              .clickable { isSystemPromptExpanded = !isSystemPromptExpanded }
          ) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(CyberCyan.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(14.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "DEDICATED INSTANCE: ${activeAgent.displayName}",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = CyberCyan
              )
              Text(
                text = "${activeAgent.title} • System Prompt Loaded",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = TextSecondary
              )
            }
            IconButton(
              onClick = { isSystemPromptExpanded = !isSystemPromptExpanded },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = if (isSystemPromptExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = "Toggle system prompt details",
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
              )
            }
          }

          AnimatedVisibility(visible = isSystemPromptExpanded) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
              Text(
                text = "Pre-filled Role-Specific System Prompt:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                color = TextSecondary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(DarkSurfaceElevated)
                  .border(1.dp, NeonBorder, RoundedCornerShape(8.dp))
                  .padding(10.dp)
              ) {
                Text(
                  text = activeAgent.systemPrompt,
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                  color = TextPrimary
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                  onClick = { onInputChange(activeAgent.systemPrompt) },
                  shape = RoundedCornerShape(6.dp),
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                  Text("Pre-fill System Prompt", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = CyberCyan)
                }

                OutlinedButton(
                  onClick = { onInputChange(activeAgent.promptSample) },
                  shape = RoundedCornerShape(6.dp),
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                  Text("Pre-fill Task Sample", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = ElectricPurple)
                }
              }
            }
          }
        }
      }
    }

    if (isGenerating) {
      LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth(),
        color = CyberCyan,
        trackColor = DarkSurfaceCard
      )
    } else {
      HorizontalDivider(color = NeonBorder)
    }

    // 2. Chat History Messages
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(vertical = 12.dp)
    ) {
      items(chatMessages, key = { it.id }) { message ->
        ChatBubble(
          message = message,
          aiService = aiService,
          onApprovePlan = { onApprovePlan(message) }
        )
      }

      if (isGenerating) {
        item {
          GeneratingBubble(activePersona)
        }
      }
    }

    // 3. Quick Suggestion Chips (visible when input is blank)
    if (chatInput.isBlank() && !isGenerating) {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(suggestionChips) { suggestion ->
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(DarkSurfaceCard)
              .border(1.dp, NeonBorder, RoundedCornerShape(12.dp))
              .clickable { onSendMessage(suggestion) }
              .padding(horizontal = 12.dp, vertical = 6.dp)
              .testTag("suggestion_chip")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.ElectricBolt,
                contentDescription = null,
                tint = WarningAmber,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = suggestion,
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimary
              )
            }
          }
        }
      }
    }

    // 3.5 Intelligence Superpowers (Google Search Grounding & Voice Conversations)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(CyberCyan.copy(alpha = 0.12f))
          .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
          .clickable {
            val prefix = "Ground with latest tech & market documentation: "
            if (!chatInput.startsWith(prefix)) {
              onInputChange(prefix + chatInput)
            }
          }
          .padding(horizontal = 8.dp, vertical = 4.dp)
          .testTag("grounding_chip")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Language,
            contentDescription = null,
            tint = CyberCyan,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Google Search Grounding: Active",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
            color = CyberCyan
          )
        }
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(ElectricPurple.copy(alpha = 0.12f))
          .border(1.dp, ElectricPurple.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
          .clickable {
            val prefix = "Executive voice briefing format: "
            if (!chatInput.startsWith(prefix)) {
              onInputChange(prefix + chatInput)
            }
          }
          .padding(horizontal = 8.dp, vertical = 4.dp)
          .testTag("voice_mode_chip")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = null,
            tint = ElectricPurple,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Voice Briefing: Ready",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
            color = ElectricPurple
          )
        }
      }
    }

    // 4. Input Box
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(DarkSurfaceElevated)
        .border(1.dp, NeonBorder)
        .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = chatInput,
          onValueChange = onInputChange,
          placeholder = {
            Text(
              text = "Ask $activePersona to plan, explain, or suggest...",
              style = MaterialTheme.typography.bodyMedium,
              color = TextMuted
            )
          },
          modifier = Modifier
            .weight(1f)
            .testTag("chat_input_field"),
          shape = RoundedCornerShape(20.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = DarkSurfaceCard,
            unfocusedContainerColor = DarkSurfaceCard,
            focusedBorderColor = CyberCyan,
            unfocusedBorderColor = NeonBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          maxLines = 4
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
          onClick = { onSendMessage(null) },
          enabled = chatInput.isNotBlank() && !isGenerating,
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
              if (chatInput.isNotBlank() && !isGenerating) CyberCyan else DarkSurfaceCard
            )
            .testTag("chat_send_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send Message",
            tint = if (chatInput.isNotBlank() && !isGenerating) Color.Black else TextMuted,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun ChatBubble(
  message: ChatMessageEntity,
  aiService: GeminiAiService,
  onApprovePlan: () -> Unit
) {
  val isUser = message.sender == "USER"
  val formattedTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp))

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
  ) {
    // Persona & Timestamp Header
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
      if (!isUser) {
        Box(
          modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(CyberCyan, ElectricPurple))),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.SmartToy,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(10.dp)
          )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = message.persona,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = CyberCyan
        )
        Spacer(modifier = Modifier.width(8.dp))
      } else {
        Icon(
          imageVector = Icons.Default.Person,
          contentDescription = null,
          tint = TextMuted,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Founder",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
          color = TextSecondary
        )
        Spacer(modifier = Modifier.width(8.dp))
      }

      Text(
        text = formattedTime,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
        color = TextMuted
      )
    }

    // Message Body Card
    Box(
      modifier = Modifier
        .clip(
          RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = if (isUser) 16.dp else 4.dp,
            bottomEnd = if (isUser) 4.dp else 16.dp
          )
        )
        .background(
          if (isUser) {
            Brush.linearGradient(listOf(Color(0xFF281F4B), Color(0xFF1D2644)))
          } else {
            Brush.linearGradient(listOf(Color(0xFF131A2B), Color(0xFF172034)))
          }
        )
        .border(
          width = 1.dp,
          color = if (isUser) ElectricPurple.copy(alpha = 0.4f) else NeonBorder,
          shape = RoundedCornerShape(16.dp)
        )
        .padding(14.dp)
    ) {
      Column {
        Text(
          text = message.text,
          style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
          color = TextPrimary
        )

        // 5. Interactive Founder Approval Gate for Proposed Plans
        if (!message.proposedTasksJson.isNullOrBlank()) {
          val proposedTasks = aiService.deserializeTasksFromJson(message.proposedTasksJson)
          if (proposedTasks.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            ApprovalPlanCard(
              proposedTasks = proposedTasks,
              isApproved = message.isApproved,
              onApprove = onApprovePlan
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ApprovalPlanCard(
  proposedTasks: List<ProposedTask>,
  isApproved: Boolean,
  onApprove: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(Color(0xFF0C1322))
      .border(
        width = 1.dp,
        color = if (isApproved) LaserGreen.copy(alpha = 0.6f) else CyberCyan.copy(alpha = 0.6f),
        shape = RoundedCornerShape(14.dp)
      )
      .padding(14.dp)
      .testTag("approval_plan_card")
  ) {
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = if (isApproved) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.FactCheck,
          contentDescription = null,
          tint = if (isApproved) LaserGreen else CyberCyan,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (isApproved) "PLAN APPROVED & COMMITTED" else "FOUNDER APPROVAL REQUIRED",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          ),
          color = if (isApproved) LaserGreen else CyberCyan
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = if (isApproved) {
          "The following ${proposedTasks.size} tasks have been added directly to your Project Manager:"
        } else {
          "Review the ${proposedTasks.size} action tasks proposed by your AI specialist:"
        },
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary
      )

      Spacer(modifier = Modifier.height(10.dp))

      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        proposedTasks.forEachIndexed { index, task ->
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(DarkSurfaceCard)
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = "${index + 1}.",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = CyberCyan
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = task.title,
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimary,
              modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF1E293B))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = task.agentRole.replace("AI ", ""),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = ElectricPurple
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      if (!isApproved) {
        ElevatedButton(
          onClick = onApprove,
          colors = ButtonDefaults.elevatedButtonColors(
            containerColor = CyberCyan,
            contentColor = Color.Black
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("approve_plan_button")
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Approve Plan & Add to Tasks",
            fontWeight = FontWeight.Bold
          )
        }
      } else {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(top = 4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = LaserGreen,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Active in Project Manager Backlog",
            style = MaterialTheme.typography.labelSmall,
            color = LaserGreen
          )
        }
      }
    }
  }
}

@Composable
private fun GeneratingBubble(persona: String) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(14.dp))
      .background(DarkSurfaceCard)
      .border(1.dp, CyberCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
      .padding(14.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(16.dp)
          .clip(CircleShape)
          .background(CyberCyan),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.SmartToy,
          contentDescription = null,
          tint = Color.Black,
          modifier = Modifier.size(10.dp)
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Text(
        text = "$persona is formulating strategic action plan...",
        style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
        color = CyberCyan
      )
    }
  }
}
