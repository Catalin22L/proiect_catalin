package com.globant.pretatit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.globant.pretatit.domain.CreateTaskUseCase
import com.globant.pretatit.presentation.Task
import com.globant.pretatit.presentation.TaskPriority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateTaskViewModel(
    private val createTaskUseCase: CreateTaskUseCase
) : ViewModel(
) {
    private var task: Task = Task("", "", TaskPriority.NONE)

    fun updateTitle(title: String) {
        task = task.copy(title = title)
    }

    fun updateDescription(description: String) {
        task = task.copy(description = description)
    }

    fun updatePriority(priority: TaskPriority) {
        task = task.copy(taskPriority = priority)
    }

    fun createTask(): Task {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                createTaskUseCase(CreateTaskUseCase.Params(task))
            }
        }

        return task
    }
}

class CreateTaskModelFactory(private val createTaskUseCase: CreateTaskUseCase) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateTaskViewModel::class.java)) {
            return CreateTaskViewModel(createTaskUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}