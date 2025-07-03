package com.github.dashboardapp.data.local.dao
import androidx.room.*
import com.github.dashboardapp.data.local.model.Task
import kotlinx.coroutines.flow.Flow
@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE status = 'PENDING' ORDER BY scheduleValue ASC")
    fun getPendingTasks(): Flow<List<Task>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)
    @Update
    suspend fun updateTask(task: Task)
}
