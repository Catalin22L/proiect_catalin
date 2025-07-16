package com.globant.pretatit.data.repository

import com.globant.pretatit.core.Failure
import com.globant.pretatit.core.Result
import com.globant.pretatit.data.datasource.local.SharedPreferencesManager
import com.globant.pretatit.domain.repos.TaskRepository
import com.globant.pretatit.presentation.Task

private const val TASKS_KEY = "tasks_list"

// Am modificat repository-ul pentru a folosi SharedPreferencesManager
class TaskRepositoryImpl(
    private val sharedPrefs: SharedPreferencesManager
) : TaskRepository {

    override suspend fun getAllTasks(): Result<List<Task>, Failure> {
        return try {
            val tasks = sharedPrefs.getTasks(TASKS_KEY)
            Result.Success(tasks)
        } catch (_: Exception) {
            Result.Error(Failure.SomethingWentWrong)
        }
    }

    override suspend fun saveTask(task: Task): Result<Unit, Failure> {
        return try {
            val currentTasks = sharedPrefs.getTasks(TASKS_KEY).toMutableList()
            currentTasks.add(task)
            sharedPrefs.saveTasks(TASKS_KEY, currentTasks)
            Result.Success(Unit)
        } catch (_: Exception) {
            Result.Error(Failure.SomethingWentWrong)
        }
    }

    override suspend fun updateTask(task: Task): Result<Unit, Failure> {
        return try {
            val currentTasks = sharedPrefs.getTasks(TASKS_KEY).toMutableList()
            val index = currentTasks.indexOfFirst { it.id == task.id }
            if (index != -1) {
                currentTasks[index] = task
                sharedPrefs.saveTasks(TASKS_KEY, currentTasks)
            }
            Result.Success(Unit)
        } catch (_: Exception) {
            Result.Error(Failure.SomethingWentWrong)
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Unit, Failure> {
        return try {
            val currentTasks = sharedPrefs.getTasks(TASKS_KEY).toMutableList()
            currentTasks.removeAll { it.id == taskId }
            sharedPrefs.saveTasks(TASKS_KEY, currentTasks)
            Result.Success(Unit)
        } catch (_: Exception) {
            Result.Error(Failure.SomethingWentWrong)
        }
    }
}