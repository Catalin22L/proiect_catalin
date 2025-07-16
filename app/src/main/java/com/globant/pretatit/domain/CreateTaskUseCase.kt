package com.globant.pretatit.domain

import com.globant.pretatit.core.Failure
import com.globant.pretatit.core.Result
import com.globant.pretatit.core.UseCase
import com.globant.pretatit.domain.CreateTaskUseCase.Params
import com.globant.pretatit.domain.repos.TaskRepository
import com.globant.pretatit.presentation.Task

interface CreateTaskUseCase : UseCase<Params, Unit> {
    data class Params(
        val task: Task
    )
}

class CreateTaskUseCaseImpl(
    private val taskRepository: TaskRepository
) : CreateTaskUseCase {
    override suspend fun invoke(params: Params): Result<Unit, Failure> {
        return taskRepository.saveTask(params.task)
    }
}