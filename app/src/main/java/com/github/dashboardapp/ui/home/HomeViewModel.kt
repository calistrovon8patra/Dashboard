package com.github.dashboardapp.ui.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dashboardapp.data.local.model.Task
import com.github.dashboardapp.data.local.model.TaskStatus
import com.github.dashboardapp.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    val tasks: StateFlow<List<Task>> = taskRepository.getPendingTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTask(name: String, groupId: String?) {
        if (name.isBlank()) return // Don't add empty tasks
        viewModelScope.launch {
            val newTask = Task(name = name, groupId = groupId)
            taskRepository.addTask(newTask)
        }
    }

    // UPDATED with new checkbox logic
    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) {
        viewModelScope.launch {
            var taskToUpdate = task.copy()

            if (isChecked) {
                // NEW: If checking the box, stop the timer and save the time.
                if (task.timerIsRunning) {
                    val elapsed = System.currentTimeMillis() - task.timerStartTime
                    val finalTime = task.timeLoggedInMillis + elapsed
                    taskToUpdate = taskToUpdate.copy(timerIsRunning = false, timeLoggedInMillis = finalTime)
                }
                taskToUpdate = taskToUpdate.copy(status = TaskStatus.COMPLETED, countDone = 1)
            } else {
                // Un-checking the box
                taskToUpdate = taskToUpdate.copy(status = TaskStatus.PENDING, countDone = 0)
            }
            taskRepository.updateTask(taskToUpdate)
        }
    }

    fun onTimerPlayPause(task: Task) {
        viewModelScope.launch {
            if (task.timerIsRunning) {
                // Pausing the timer
                val elapsed = System.currentTimeMillis() - task.timerStartTime
                val newTotalTime = task.timeLoggedInMillis + elapsed
                taskRepository.updateTask(task.copy(timerIsRunning = false, timeLoggedInMillis = newTotalTime))
            } else {
                // Starting the timer
                taskRepository.updateTask(task.copy(timerIsRunning = true, timerStartTime = System.currentTimeMillis()))
            }
        }
    }

    fun onTimerStop(task: Task, saveTime: Boolean) {
        viewModelScope.launch {
            val elapsed = if (task.timerIsRunning) System.currentTimeMillis() - task.timerStartTime else 0
            val finalTime = task.timeLoggedInMillis + elapsed
            
            taskRepository.updateTask(
                task.copy(
                    timerIsRunning = false,
                    timeLoggedInMillis = if (saveTime) finalTime else task.timeLoggedInMillis
                )
            )
        }
    }
}
