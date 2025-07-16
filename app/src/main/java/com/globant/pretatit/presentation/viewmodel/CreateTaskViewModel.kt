package com.globant.pretatit.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.globant.pretatit.core.Result
import com.globant.pretatit.domain.repos.TaskRepository
import com.globant.pretatit.presentation.Task
import com.globant.pretatit.presentation.TaskCategory
import com.globant.pretatit.presentation.TaskPriority
import kotlinx.coroutines.launch

class CreateTaskViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    var isEditMode by mutableStateOf(false)
        private set

    private var taskId: String? = null
    private var isDone: Boolean = false

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var priority by mutableStateOf(TaskPriority.NONE)
    var category by mutableStateOf(TaskCategory.OTHER)
    var dueDate by mutableStateOf<Long?>(null)

    fun loadTask(task: Task?) {
        if (task != null) {
            isEditMode = true
            taskId = task.id
            title = task.title
            description = task.description
            priority = task.taskPriority
            category = task.category
            dueDate = task.dueDate
            isDone = task.isDone
        }
    }

    fun saveOrUpdateTask(onResult: (Boolean) -> Unit) {
        if (title.isBlank() || description.isBlank()) {
            onResult(false)
            return
        }

        viewModelScope.launch {
            val taskToSave = Task(
                id = taskId ?: java.util.UUID.randomUUID().toString(),
                title = title,
                description = description,
                taskPriority = priority,
                category = category,
                dueDate = dueDate,
                isDone = isDone
            )

            val result = if (isEditMode) {
                taskRepository.updateTask(taskToSave)
            } else {
                taskRepository.saveTask(taskToSave)
            }

            when (result) {
                is Result.Success -> onResult(true)
                is Result.Error -> onResult(false)
            }
        }
    }
}

class CreateTaskModelFactory(private val taskRepository: TaskRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateTaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateTaskViewModel(taskRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}