package com.github.dashboardapp.ui.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dashboardapp.data.local.model.Task
import com.github.dashboardapp.data.local.model.TaskStatus
import com.github.dashboardapp.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class HomeViewModel @Inject constructor(private val taskRepository: TaskRepository) : ViewModel() {
    val tasks: StateFlow<List<Task>> = taskRepository.getPendingTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun addTask(task: Task) { viewModelScope.launch { taskRepository.addTask(task) } }
    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) {
        viewModelScope.launch {
            val newStatus = if (isChecked) TaskStatus.COMPLETED else TaskStatus.PENDING
            taskRepository.updateTask(task.copy(status = newStatus))
        }
    }
}
