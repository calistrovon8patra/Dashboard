package com.github.dashboardapp.data.local.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
enum class TaskType { ONE_TIME, REPEATING, COUNTABLE, FLEXIBLE }
enum class ScheduleMode { DATE, WEEK, MONTH }
enum class TaskStatus { PENDING, COMPLETED }
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: TaskType = TaskType.ONE_TIME,
    val groupId: String? = null,
    val subtaskIds: List<String> = emptyList(),
    val countTotal: Int = 1,
    val countDone: Int = 0,
    val timerEnabled: Boolean = false,
    val scheduleMode: ScheduleMode = ScheduleMode.DATE,
    val scheduleValue: Long? = null,
    val status: TaskStatus = TaskStatus.PENDING
)
