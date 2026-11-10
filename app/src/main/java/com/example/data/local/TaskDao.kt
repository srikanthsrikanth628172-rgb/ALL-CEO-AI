package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
  @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
  fun getAllTasks(): Flow<List<TaskEntity>>

  @Query("SELECT * FROM tasks WHERE projectId = :projectId ORDER BY createdAt DESC")
  fun getTasksForProject(projectId: Long): Flow<List<TaskEntity>>

  @Query("SELECT * FROM tasks WHERE status != 'DONE' ORDER BY createdAt DESC")
  fun getPendingTasks(): Flow<List<TaskEntity>>

  @Query("SELECT * FROM tasks WHERE status = 'DONE' ORDER BY createdAt DESC")
  fun getCompletedTasks(): Flow<List<TaskEntity>>

  @Query("SELECT COUNT(*) FROM tasks WHERE status != 'DONE'")
  fun getPendingTaskCount(): Flow<Int>

  @Query("SELECT COUNT(*) FROM tasks WHERE status = 'DONE'")
  fun getCompletedTaskCount(): Flow<Int>

  @Query("SELECT COUNT(*) FROM tasks")
  fun getTotalTaskCount(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: TaskEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTasks(tasks: List<TaskEntity>)

  @Update
  suspend fun updateTask(task: TaskEntity)

  @Delete
  suspend fun deleteTask(task: TaskEntity)

  @Query("DELETE FROM tasks WHERE projectId = :projectId")
  suspend fun deleteTasksForProject(projectId: Long)

  @Query("DELETE FROM tasks")
  suspend fun clearAll()
}
