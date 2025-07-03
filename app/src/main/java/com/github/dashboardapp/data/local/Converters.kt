package com.github.dashboardapp.data.local
import androidx.room.TypeConverter
class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String = value.joinToString(",")
    @TypeConverter
    fun toStringList(value: String): List<String> = value.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}
