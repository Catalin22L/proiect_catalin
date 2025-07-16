package com.globant.pretatit.data.repository

import com.globant.pretatit.core.Failure
import com.globant.pretatit.core.Result
import com.globant.pretatit.data.TaskEntity
import com.globant.pretatit.data.datasource.PregatitDatabase
import com.globant.pretatit.domain.repos.TaskRepository
import com.globant.pretatit.presentation.Task

class TaskRepositoryImpl(
    private val database: PregatitDatabase
) : TaskRepository {

    override suspend fun getAllTasks(): Result<List<Task>, Failure> {
        return try {
            val taskList = database.getTaskDAO().getAllTasks().map {
                Task(it.title, it.description, it.taskPriority)
            }
            Result.Success(taskList)
        } catch (_: Exception) {
            Result.Error(Failure.SomethingWentWrong)
        }
    }

    override suspend fun saveTask(task: Task): Result<Unit, Failure> {
        return try {
            database.getTaskDAO().insertTask(
                TaskEntity(
                    title = task.title,
                    description = task.description,
                    taskPriority = task.taskPriority
                )
            )
            Result.Success(Unit)
        } catch (_: Exception) {
            Result.Error(Failure.SomethingWentWrong)
        }
    }
}