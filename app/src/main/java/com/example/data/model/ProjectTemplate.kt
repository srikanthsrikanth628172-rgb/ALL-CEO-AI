package com.example.data.model

data class TemplateTask(
  val title: String,
  val agentRole: String,
  val priority: String = "HIGH"
)

data class ProjectTemplate(
  val id: String,
  val name: String,
  val category: String,
  val tagline: String,
  val description: String,
  val targetTimeline: String,
  val tasks: List<TemplateTask>
) {
  companion object {
    val TEMPLATES = listOf(
      ProjectTemplate(
        id = "web_saas",
        name = "Web SaaS MVP",
        category = "Web SaaS",
        tagline = "Multi-tenant cloud platform with subscription billing",
        description = "Full-stack software-as-a-service application featuring authentication, tier-based billing, user onboarding, and database persistence.",
        targetTimeline = "3-Week Sprint",
        tasks = listOf(
          TemplateTask("Decompose multi-tenant data schema & user hierarchy", "AI Planner", "HIGH"),
          TemplateTask("Implement OAuth2 authentication & session cookies", "AI Developer", "HIGH"),
          TemplateTask("Benchmark PostgreSQL vs Supabase vs Neon DB", "AI Researcher", "MEDIUM"),
          TemplateTask("Audit Stripe subscription webhooks & failure retries", "AI Tester", "HIGH"),
          TemplateTask("Profile endpoint response times under concurrent load", "AI Debugger", "MEDIUM"),
          TemplateTask("Configure GitHub Actions CI/CD and Docker cloud deploy", "AI Deployment Assistant", "HIGH")
        )
      ),
      ProjectTemplate(
        id = "mobile_app",
        name = "Mobile App Launch",
        category = "Mobile App",
        tagline = "Native Android client with offline-first persistence",
        description = "Production-grade Android application with Jetpack Compose UI, Room local caching, and responsive edge-to-edge layouts.",
        targetTimeline = "4-Week Milestone",
        tasks = listOf(
          TemplateTask("Architect offline-first Room database & reactive flows", "AI Planner", "HIGH"),
          TemplateTask("Build Material 3 dark dashboard UI with custom navigation", "AI Developer", "HIGH"),
          TemplateTask("Verify touch target accessibility & backstack transitions", "AI Tester", "HIGH"),
          TemplateTask("Profile memory allocations and frame rate during scroll", "AI Debugger", "MEDIUM"),
          TemplateTask("Evaluate vector icon rendering vs raster asset bundle size", "AI Researcher", "LOW"),
          TemplateTask("Configure Proguard rules & sign release Android App Bundle", "AI Deployment Assistant", "HIGH")
        )
      ),
      ProjectTemplate(
        id = "ai_prototype",
        name = "AI Prototype",
        category = "AI Prototype",
        tagline = "Gemini-powered multimodal agent workflow",
        description = "Intelligent application leveraging Gemini models for structured reasoning, streaming completions, and automated action proposals.",
        targetTimeline = "2-Week Fast Prototype",
        tasks = listOf(
          TemplateTask("Define JSON response schemas & structured output contracts", "AI Planner", "HIGH"),
          TemplateTask("Implement Gemini 3.8 Flash SDK client with streaming", "AI Developer", "HIGH"),
          TemplateTask("Benchmark token latency & cold-start response windows", "AI Researcher", "MEDIUM"),
          TemplateTask("Execute prompt injection & hallucination stress tests", "AI Tester", "HIGH"),
          TemplateTask("Triage timeout handling and offline fallback mechanisms", "AI Debugger", "HIGH"),
          TemplateTask("Deploy server-side App Check attestation and secret guards", "AI Deployment Assistant", "HIGH")
        )
      ),
      ProjectTemplate(
        id = "api_backend",
        name = "Micro-SaaS API",
        category = "API & Cloud",
        tagline = "High-throughput REST API with API key metering",
        description = "Backend microservice offering developer APIs, rate limiting, token quota tracking, and automated documentation.",
        targetTimeline = "2-Week Sprint",
        tasks = listOf(
          TemplateTask("Draft OpenAPI 3.0 specification & request validation rules", "AI Planner", "HIGH"),
          TemplateTask("Build JWT auth middleware & API key rate limiters", "AI Developer", "HIGH"),
          TemplateTask("Conduct stress testing on concurrent database connections", "AI Tester", "HIGH"),
          TemplateTask("Setup automated cloud health checks & uptime monitors", "AI Deployment Assistant", "MEDIUM")
        )
      )
    )
  }
}
