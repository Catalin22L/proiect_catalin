package com.globant.pretatit.data.repository

import com.globant.pretatit.core.Failure
import com.globant.pretatit.core.Result
import com.globant.pretatit.data.datasource.SharedPreferencesManager
import com.globant.pretatit.domain.repos.TaskRepository
import com.globant.pretatit.presentation.Task

private const val SH_TASK_KEY = "sh.task.key"

class TaskRepositoryImpl(
    private val sharedPreferencesManager: SharedPreferencesManager
) : TaskRepository {

    override fun getAllTasks(): Result<List<Task>, Failure> {
        return try {
            Result.Success(sharedPreferencesManager.getTasks(SH_TASK_KEY))
        } catch (e: Exception) {
            Result.Error(Failure.SomethingWentWrong)
        }
    }

    override fun saveTask(task: Task): Result<Unit, Failure> {
        return try {
            sharedPreferencesManager.saveTask(SH_TASK_KEY, task)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(Failure.SomethingWentWrong)
        }
    }
}