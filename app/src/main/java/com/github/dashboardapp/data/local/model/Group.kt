package com.github.dashboardapp.data.local.model
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "groups")
data class Group(@PrimaryKey val id: String, val name: String, val gradientId: Int)
