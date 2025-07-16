package com.globant.pretatit.data.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.globant.pretatit.data.TaskEntity

@Database(
    entities = [TaskEntity::class], version = 1
)
@TypeConverters(TaskPriorityConverter::class)
abstract class PregatitDatabase : RoomDatabase() {

    abstract fun getTaskDAO(): TaskDao
}