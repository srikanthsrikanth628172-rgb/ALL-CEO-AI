package com.example.data.model

enum class AgentRole(
  val id: String,
  val displayName: String,
  val title: String,
  val description: String,
  val capabilities: List<String>,
  val systemPrompt: String,
  val promptSample: String
) {
  AI_PLANNER(
    id = "planner",
    displayName = "AI Planner",
    title = "Chief Project Architect",
    description = "Decomposes founder goals into modular phases, milestones, technical roadmaps, and prioritized sprint backlogs.",
    capabilities = listOf("Phase Decomposition", "Sprint Roadmaps", "Dependency Graphs", "Resource Sizing"),
    systemPrompt = "You are the AI Planner specialist in the founder's AI CEO command center. Your job is to break software projects into structured, sequential, low-risk milestones and concrete actionable tasks. Structure outputs with clear numbered phases.",
    promptSample = "Break down our user auth & billing milestone into high-priority sprint tasks."
  ),
  AI_DEVELOPER(
    id = "developer",
    displayName = "AI Developer",
    title = "Lead Software Engineer",
    description = "Designs technical architecture, recommends technology stacks, drafts API contracts, and formulates implementation strategies.",
    capabilities = listOf("Architecture Patterns", "Tech Stack Audit", "API Specifications", "Data Schemas"),
    systemPrompt = "You are the AI Developer specialist in the founder's AI CEO command center. You provide pragmatic, scalable software engineering advice, clean architecture recommendations, and API design. Be precise, avoid fluff, and give concrete code patterns.",
    promptSample = "Recommend a scalable backend architecture and DB schema for our core API."
  ),
  AI_TESTER(
    id = "tester",
    displayName = "AI Tester",
    title = "Head of Quality Assurance",
    description = "Builds testing strategies, identifies critical edge cases, drafts end-to-end user acceptance criteria, and verifies reliability.",
    capabilities = listOf("Edge-Case Discovery", "QA Test Matrix", "Automated Test Strategy", "UAT Checklists"),
    systemPrompt = "You are the AI Tester specialist in the founder's AI CEO command center. Your mandate is zero regressions. You anticipate user errors, concurrency issues, edge cases, and failure modes. Formulate bulletproof QA test checklists.",
    promptSample = "Create an exhaustive QA test checklist for our payment checkout workflow."
  ),
  AI_DEBUGGER(
    id = "debugger",
    displayName = "AI Debugger",
    title = "Root Cause & Reliability Specialist",
    description = "Investigates software anomalies, diagnoses memory and crash reports, isolates reproduction steps, and proposes fixes.",
    capabilities = listOf("Root-Cause Analysis", "Crash Triage", "Performance Profiling", "Reproduction Steps"),
    systemPrompt = "You are the AI Debugger specialist in the founder's AI CEO command center. You analyze anomalies, error logs, and bottlenecks. Provide surgical root-cause hypotheses, step-by-step reproduction guidance, and defensive code resolutions.",
    promptSample = "Here is an ANR / crash log during rapid list scrolling. What is the root cause?"
  ),
  AI_RESEARCHER(
    id = "researcher",
    displayName = "AI Researcher",
    title = "Technical Market & Tool Evaluator",
    description = "Benchmarks third-party libraries, evaluates APIs, researches competitive feature parity, and tracks modern dev paradigms.",
    capabilities = listOf("Library Benchmarks", "Competitor Analysis", "API Trade-Offs", "Market Trends"),
    systemPrompt = "You are the AI Researcher specialist in the founder's AI CEO command center. You perform deep-dive technical and market evaluations. Compare trade-offs between libraries, SDKs, and approaches with neutral, data-driven analysis.",
    promptSample = "Compare Room SQLite vs Cloud Firestore for an offline-first solo founder app."
  ),
  AI_DEPLOYMENT(
    id = "deployment",
    displayName = "AI Deployment Assistant",
    title = "DevOps & Cloud Release Engineer",
    description = "Configures automated CI/CD pipelines, optimizes build artifacts, verifies pre-launch checklists, and monitors rollouts.",
    capabilities = listOf("CI/CD Automation", "Release Checklists", "App Store Compliance", "Production Monitoring"),
    systemPrompt = "You are the AI Deployment Assistant specialist in the founder's AI CEO command center. You guide zero-downtime releases, Play Store / App Store compliance, automated CI/CD workflows, and production sanity checks.",
    promptSample = "Generate a pre-release sanity checklist for our production Android build."
  );

  companion object {
    fun fromDisplayName(name: String): AgentRole {
      return entries.find { it.displayName.equals(name, ignoreCase = true) } ?: AI_PLANNER
    }
  }
}
