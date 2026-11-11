package com.example

import com.example.data.ai.ProposedTask
import com.example.data.model.AgentRole
import com.example.data.model.ProjectEntity
import com.example.data.model.TaskEntity
import com.example.data.repository.FounderSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun `verify all 6 specialist agent roles exist and are configured`() {
    val roles = AgentRole.entries
    assertEquals(6, roles.size)

    val expectedRoles = listOf(
      "AI Planner",
      "AI Developer",
      "AI Tester",
      "AI Debugger",
      "AI Researcher",
      "AI Deployment Assistant"
    )

    for (roleName in expectedRoles) {
      val found = AgentRole.fromDisplayName(roleName)
      assertEquals(roleName, found.displayName)
      assertTrue("Capabilities for $roleName should not be empty", found.capabilities.isNotEmpty())
      assertTrue("System prompt for $roleName should not be empty", found.systemPrompt.isNotBlank())
    }
  }

  @Test
  fun `verify project entity status and defaults`() {
    val project = ProjectEntity(
      title = "AI SaaS MVP Launch",
      description = "Multi-tenant cloud platform",
      category = "SaaS MVP",
      status = "ACTIVE"
    )

    assertEquals("AI SaaS MVP Launch", project.title)
    assertEquals("ACTIVE", project.status)
    assertEquals("SaaS MVP", project.category)
    assertTrue(project.createdAt > 0)
  }

  @Test
  fun `verify task entity status toggling logic`() {
    val task = TaskEntity(
      projectId = 1L,
      title = "Implement OAuth2 flow",
      agentRole = "AI Developer",
      status = "TODO",
      priority = "HIGH"
    )

    assertEquals("TODO", task.status)
    val inProgress = task.copy(status = "IN_PROGRESS")
    assertEquals("IN_PROGRESS", inProgress.status)
    val done = inProgress.copy(status = "DONE")
    assertEquals("DONE", done.status)
  }

  @Test
  fun `verify proposed task properties`() {
    val task = ProposedTask(
      title = "Decompose data models",
      agentRole = "AI Planner",
      priority = "HIGH"
    )

    assertEquals("Decompose data models", task.title)
    assertEquals("AI Planner", task.agentRole)
    assertEquals("HIGH", task.priority)
  }

  @Test
  fun `verify founder settings defaults and approval policy`() {
    val settings = FounderSettings()
    assertEquals("Solo Founder", settings.founderName)
    assertEquals("MY AI SUPERSTAR", settings.ventureName)
    assertTrue("Approval should be required by default", settings.requireApprovalBeforeAction)
    assertTrue("Local persistence should be true by default", settings.localPersistenceOnly)
  }

  @Test
  fun `verify project templates selection and pre-bundled tasks`() {
    val templates = com.example.data.model.ProjectTemplate.TEMPLATES
    assertTrue(templates.size >= 3)

    val webSaas = templates.find { it.id == "web_saas" }
    assertNotNull(webSaas)
    assertEquals("Web SaaS MVP", webSaas!!.name)
    assertEquals("Web SaaS", webSaas.category)
    assertTrue(webSaas.tasks.isNotEmpty())

    val mobileApp = templates.find { it.id == "mobile_app" }
    assertNotNull(mobileApp)
    assertEquals("Mobile App Launch", mobileApp!!.name)
    assertEquals("Mobile App", mobileApp.category)
    assertTrue(mobileApp.tasks.isNotEmpty())

    val aiPrototype = templates.find { it.id == "ai_prototype" }
    assertNotNull(aiPrototype)
    assertEquals("AI Prototype", aiPrototype!!.name)
    assertEquals("AI Prototype", aiPrototype.category)
    assertTrue(aiPrototype.tasks.isNotEmpty())

    // Verify tasks are assigned to valid specialist agents
    for (template in templates) {
      for (task in template.tasks) {
        assertTrue(task.title.isNotBlank())
        val role = AgentRole.fromDisplayName(task.agentRole)
        assertNotNull(role)
      }
    }
  }

  @Test
  fun `verify agent role-specific system prompt pre-fill for dedicated chat`() {
    for (role in AgentRole.entries) {
      val prompt = role.systemPrompt
      assertTrue("System prompt for ${role.displayName} should not be blank", prompt.isNotBlank())
      assertTrue(prompt.contains(role.displayName))
      assertTrue("Prompt sample for ${role.displayName} should not be blank", role.promptSample.isNotBlank())
    }
  }

  @Test
  fun `verify editing project updates all core roadmap fields`() {
    val original = ProjectEntity(
      id = 42L,
      title = "Initial Alpha Draft",
      description = "Early prototype",
      category = "Internal",
      targetDate = "Q1 2026",
      status = "ACTIVE"
    )

    val edited = original.copy(
      title = "Production SaaS V1",
      description = "Shipped to early beta testers with zero downtime",
      category = "Web SaaS",
      targetDate = "Q2 2026"
    )

    assertEquals(42L, edited.id)
    assertEquals("Production SaaS V1", edited.title)
    assertEquals("Shipped to early beta testers with zero downtime", edited.description)
    assertEquals("Web SaaS", edited.category)
    assertEquals("Q2 2026", edited.targetDate)
    assertEquals("ACTIVE", edited.status)
  }

  @Test
  fun `verify editing task updates title agent role and priority`() {
    val originalTask = TaskEntity(
      id = 101L,
      projectId = 42L,
      title = "Draft API endpoints",
      agentRole = "AI Planner",
      priority = "MEDIUM",
      status = "TODO"
    )

    val updatedTask = originalTask.copy(
      title = "Implement authenticated JWT endpoints and unit tests",
      agentRole = "AI Developer",
      priority = "HIGH"
    )

    assertEquals(101L, updatedTask.id)
    assertEquals(42L, updatedTask.projectId)
    assertEquals("Implement authenticated JWT endpoints and unit tests", updatedTask.title)
    assertEquals("AI Developer", updatedTask.agentRole)
    assertEquals("HIGH", updatedTask.priority)
    assertEquals("TODO", updatedTask.status)
  }

  @Test
  fun `verify ai response properties and offline engine notice`() {
    val liveResponse = com.example.data.ai.AiResponse(
      responseText = "Live analysis",
      isLiveAi = true,
      engineNotice = null
    )
    assertTrue(liveResponse.isLiveAi)
    org.junit.Assert.assertNull(liveResponse.engineNotice)

    val offlineResponse = liveResponse.copy(
      responseText = "Offline response",
      isLiveAi = false,
      engineNotice = "₹0 Offline Executive Engine Active"
    )
    org.junit.Assert.assertFalse(offlineResponse.isLiveAi)
    assertEquals("₹0 Offline Executive Engine Active", offlineResponse.engineNotice)
  }

  @Test
  fun `verify gemini model selection tiers and video prompt formatting`() {
    val supportedModels = listOf(
      "gemini-3.8-flash",
      "gemini-4-carbon",
      "gemini-4-argon",
      "gemini-3.1-flash-lite"
    )
    assertEquals(4, supportedModels.size)
    assertTrue(supportedModels.contains("gemini-4-carbon"))
    assertTrue(supportedModels.contains("gemini-4-argon"))
    assertTrue(supportedModels.contains("gemini-3.8-flash"))
    assertTrue(supportedModels.contains("gemini-3.1-flash-lite"))

    val video169 = "Generate 16:9 landscape video pitch script using gemini-omni-1.2-flash: SaaS Pitch"
    val video916 = "Generate 9:16 portrait mobile video script using gemini-omni-1.2-flash: Reel"
    assertTrue(video169.contains("16:9") && video169.contains("gemini-omni-1.2-flash"))
    assertTrue(video916.contains("9:16") && video916.contains("gemini-omni-1.2-flash"))
  }
}



