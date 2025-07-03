package com.github.dashboardapp.data.repository
import com.github.dashboardapp.data.local.dao.TaskDao
import com.github.dashboardapp.data.local.model.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class TaskRepository @Inject constructor(private val taskDao: TaskDao) {
    fun getPendingTasks(): Flow<List<Task>> = taskDao.getPendingTasks()
    suspend fun addTask(task: Task) { taskDao.insertTask(task) }
    suspend fun updateTask(task: Task) { taskDao.updateTask(task) }
}
