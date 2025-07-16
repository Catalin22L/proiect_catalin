package com.globant.pretatit.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.globant.pretatit.data.datasource.local.db.TaskPriorityConverter
import com.globant.pretatit.presentation.TaskPriority

@Entity(tableName = "tasks")
@TypeConverters(TaskPriorityConverter::class)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val taskPriority: TaskPriority
)