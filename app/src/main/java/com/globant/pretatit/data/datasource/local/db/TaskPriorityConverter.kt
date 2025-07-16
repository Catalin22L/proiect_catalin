package com.globant.pretatit.data.datasource.local.db

import androidx.room.TypeConverter
import com.globant.pretatit.presentation.TaskPriority

class TaskPriorityConverter {

    @TypeConverter
    fun fromTaskPriority(priority: TaskPriority): String = priority.name

    @TypeConverter
    fun toTaskPriority(name: String): TaskPriority =
        TaskPriority.valueOf(name)
}