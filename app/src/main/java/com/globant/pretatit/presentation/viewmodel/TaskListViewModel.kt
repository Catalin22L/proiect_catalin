package com.globant.pretatit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.globant.pretatit.domain.GetAllTasksUseCase
import com.globant.pretatit.presentation.Task
import com.globant.pretatit.presentation.TaskPriority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

private val INITIAL_TASK_LIST = mutableListOf<Task>(
    Task("Feed the cat", "It likes to eat mice!", TaskPriority.HIGH),
    Task("Feed the dog", "It likes to eat mice!", TaskPriority.LOW),
    Task("Feed the hamster", "It likes to eat mice!", TaskPriority.URGENT),
)

class TaskListViewModel(
    private val tasksUseCase: GetAllTasksUseCase
) : ViewModel() {

    private val _taskList = MutableStateFlow<MutableList<Task>>(INITIAL_TASK_LIST)
    val taskList: StateFlow<List<Task>> = _taskList

    fun init() {
        tasksUseCase.invoke(Unit)
            .handleResult(
                {
                    _taskList.value = it.toMutableList()
                },
                {
                    Timber.w("SOmething went wrong when reading the tasks from SH")
                }
            )
    }

    fun sortByPriority() {
        _taskList.value = _taskList.value.sortedBy { it.taskPriority.ordinal }.toMutableList()
    }

    fun addTask(task: Task) {
        _taskList.value =
            _taskList.value.apply { add(task) }
    }
}

class TaskViewModelFactory(private val getAllTasksUseCase: GetAllTasksUseCase) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
            return TaskListViewModel(getAllTasksUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}