package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ActivityEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.TaskEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    ProjectEntity::class,
    TaskEntity::class,
    ChatMessageEntity::class,
    ActivityEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun projectDao(): ProjectDao
  abstract fun taskDao(): TaskDao
  abstract fun chatMessageDao(): ChatMessageDao
  abstract fun activityDao(): ActivityDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "ai_ceo_command_center.db"
        )
          .addCallback(AppDatabaseCallback(scope))
          .build()
        INSTANCE = instance
        instance
      }
    }
  }

  private class AppDatabaseCallback(
    private val scope: CoroutineScope
  ) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
      super.onCreate(db)
      INSTANCE?.let { database ->
        scope.launch(Dispatchers.IO) {
          populateInitialData(database)
        }
      }
    }

    private suspend fun populateInitialData(database: AppDatabase) {
      val projectDao = database.projectDao()
      val taskDao = database.taskDao()
      val chatDao = database.chatMessageDao()
      val activityDao = database.activityDao()

      val now = System.currentTimeMillis()

      // Seed Project 1: AI SaaS MVP Launch
      val saasId = projectDao.insertProject(
        ProjectEntity(
          title = "AI SaaS MVP Launch",
          description = "Multi-tenant cloud platform with Gemini analytics, automated billing, and subscriber onboarding.",
          category = "SaaS MVP",
          status = "ACTIVE",
          targetDate = "End of Month",
          createdAt = now - 86400000L * 2
        )
      )

      taskDao.insertTask(
        TaskEntity(
          projectId = saasId,
          title = "Decompose architecture & sprint roadmap",
          agentRole = "AI Planner",
          status = "DONE",
          priority = "HIGH",
          createdAt = now - 86400000L * 2
        )
      )
      taskDao.insertTask(
        TaskEntity(
          projectId = saasId,
          title = "Benchmark Postgres vs DynamoDB for user metadata",
          agentRole = "AI Researcher",
          status = "DONE",
          priority = "MEDIUM",
          createdAt = now - 86400000L
        )
      )
      taskDao.insertTask(
        TaskEntity(
          projectId = saasId,
          title = "Implement OAuth2 & user session management",
          agentRole = "AI Developer",
          status = "IN_PROGRESS",
          priority = "HIGH",
          createdAt = now - 72000000L
        )
      )
      taskDao.insertTask(
        TaskEntity(
          projectId = saasId,
          title = "Audit edge cases for payment checkout webhooks",
          agentRole = "AI Tester",
          status = "TODO",
          priority = "HIGH",
          createdAt = now - 36000000L
        )
      )
      taskDao.insertTask(
        TaskEntity(
          projectId = saasId,
          title = "Set up Docker build & GitHub Actions CI/CD",
          agentRole = "AI Deployment Assistant",
          status = "TODO",
          priority = "HIGH",
          createdAt = now - 18000000L
        )
      )
      taskDao.insertTask(
        TaskEntity(
          projectId = saasId,
          title = "Profile API latency on bulk inference endpoints",
          agentRole = "AI Debugger",
          status = "TODO",
          priority = "MEDIUM",
          createdAt = now - 7200000L
        )
      )

      // Seed Project 2: Mobile App Development
      val mobileId = projectDao.insertProject(
        ProjectEntity(
          title = "Mobile App Development",
          description = "Native Android command center client with offline Room persistence and real-time AI streaming.",
          category = "Mobile",
          status = "ACTIVE",
          targetDate = "Mid Sprint",
          createdAt = now - 86400000L
        )
      )

      taskDao.insertTask(
        TaskEntity(
          projectId = mobileId,
          title = "Finalize Material 3 obsidian dark theme and typography",
          agentRole = "AI Planner",
          status = "DONE",
          priority = "HIGH",
          createdAt = now - 86400000L
        )
      )
      taskDao.insertTask(
        TaskEntity(
          projectId = mobileId,
          title = "Build reactive Room repository and state management",
          agentRole = "AI Developer",
          status = "DONE",
          priority = "HIGH",
          createdAt = now - 43200000L
        )
      )
      taskDao.insertTask(
        TaskEntity(
          projectId = mobileId,
          title = "Verify touch target accessibility and backstack navigation",
          agentRole = "AI Tester",
          status = "IN_PROGRESS",
          priority = "MEDIUM",
          createdAt = now - 21600000L
        )
      )
      taskDao.insertTask(
        TaskEntity(
          projectId = mobileId,
          title = "Configure zero-downtime release checklist",
          agentRole = "AI Deployment Assistant",
          status = "TODO",
          priority = "MEDIUM",
          createdAt = now - 10800000L
        )
      )

      // Seed Initial Welcome Chat Message
      chatDao.insertMessage(
        ChatMessageEntity(
          sender = "AI",
          persona = "AI CEO",
          text = "Welcome back, Founder. I am your AI CEO. We are tracking 2 active projects across 6 specialized departments (Planning, Development, QA Testing, Debugging, Market Research, and Cloud Deployment). What strategic objective shall we execute today?",
          proposedTasksJson = null,
          isApproved = false,
          timestamp = now - 600000L
        )
      )

      // Seed Initial Activities
      activityDao.insertActivity(
        ActivityEntity(
          title = "Command Center Initialized",
          detail = "MY AI SUPERSTAR V1 initialized with 6 specialist agents online.",
          tag = "SYSTEM",
          timestamp = now - 86400000L * 2
        )
      )
      activityDao.insertActivity(
        ActivityEntity(
          title = "Template Preloaded",
          detail = "AI SaaS MVP Launch sprint backlog generated with 6 milestone tasks.",
          tag = "PROJECT",
          timestamp = now - 86400000L
        )
      )
      activityDao.insertActivity(
        ActivityEntity(
          title = "Sprint Roadmap Approved",
          detail = "Founder approved sprint milestones for Mobile App Development.",
          tag = "CHAT",
          timestamp = now - 43200000L
        )
      )
    }
  }
}
