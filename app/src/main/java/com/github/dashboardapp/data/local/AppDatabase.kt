package com.github.dashboardapp.data.local
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.dashboardapp.data.local.dao.TaskDao
import com.github.dashboardapp.data.local.model.Group
import com.github.dashboardapp.data.local.model.Task
@Database(entities = [Task::class, Group::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
