package com.globant.pretatit.domain.repos

import com.globant.pretatit.core.Failure
import com.globant.pretatit.core.Result
import com.globant.pretatit.presentation.Task

interface TaskRepository {

    suspend fun saveTask(task: Task): Result<Unit, Failure>

    suspend fun getAllTasks(): Result<List<Task>, Failure>
}