package com.globant.pretatit.domain.repos

import com.globant.pretatit.core.Failure
import com.globant.pretatit.core.Result
import com.globant.pretatit.presentation.Task

interface TaskRepository {

    fun saveTask(task: Task): Result<Unit, Failure>

    fun getAllTasks(): Result<List<Task>, Failure>
}