package com.globant.pretatit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.globant.pretatit.domain.repos.TaskRepository
import com.globant.pretatit.presentation.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber

class TaskListViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _allTasks = MutableStateFlow<List<Task>>(emptyList())
    // Stare pentru a gestiona sortarea
    private val _sortState = MutableStateFlow(SortState())
    private val _categoryFilter = MutableStateFlow("ALL")

    val taskList = combine(_allTasks, _sortState, _categoryFilter) { tasks, sortState, category ->
        val filteredTasks = if (category == "ALL") {
            tasks
        } else {
            tasks.filter { it.category.name == category }
        }

        val sortedByDone = filteredTasks.sortedBy { it.isDone }

        // Logica de sortare
        when (sortState.criteria) {
            SortCriteria.PRIORITY -> {
                if (sortState.order == SortOrder.ASC) {
                    sortedByDone.sortedBy { it.taskPriority.ordinal }
                } else {
                    sortedByDone.sortedByDescending { it.taskPriority.ordinal }
                }
            }
            SortCriteria.DATE -> {
                if (sortState.order == SortOrder.ASC) {
                    // Sarcinile fara data sunt puse la sfarsit
                    sortedByDone.sortedWith(compareBy(nullsLast()) { it.dueDate })
                } else {
                    sortedByDone.sortedWith(compareByDescending(nullsLast()) { it.dueDate })
                }
            }
        }
    }

    private val _showDeleteConfirmation = MutableStateFlow<Task?>(null)
    val showDeleteConfirmation = _showDeleteConfirmation.asStateFlow()

    fun init() {
        viewModelScope.launch {
            taskRepository.getAllTasks().handleResult(
                successFlow = { _allTasks.value = it },
                errorFlow = { Timber.w("Error fetching tasks") }
            )
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            taskRepository.saveTask(task)
            init()
        }
    }

    // Comuta intre sortare ascendenta si descendenta
    fun toggleSortOrder() {
        _sortState.value = _sortState.value.copy(
            order = if (_sortState.value.order == SortOrder.ASC) SortOrder.DESC else SortOrder.ASC
        )
    }

    // Comuta intre sortarea dupa prioritate si data
    fun toggleSortCriteria() {
        _sortState.value = _sortState.value.copy(
            criteria = if (_sortState.value.criteria == SortCriteria.PRIORITY) SortCriteria.DATE else SortCriteria.PRIORITY
        )
    }

    fun filterByCategory(category: String) {
        _categoryFilter.value = category
    }

    fun onTaskCheckedChange(task: Task, isChecked: Boolean) {
        viewModelScope.launch {
            val updatedTask = task.copy(isDone = isChecked)
            taskRepository.updateTask(updatedTask)
            init()
        }
    }

    fun onDeleteTaskClicked(task: Task) {
        _showDeleteConfirmation.value = task
    }

    fun confirmTaskDeletion() {
        _showDeleteConfirmation.value?.let { task ->
            viewModelScope.launch {
                taskRepository.deleteTask(task.id)
                _showDeleteConfirmation.value = null
                init()
            }
        }
    }

    fun cancelTaskDeletion() {
        _showDeleteConfirmation.value = null
    }
}

// Stari si criterii pentru sortare
enum class SortOrder { ASC, DESC }
enum class SortCriteria { PRIORITY, DATE }
data class SortState(
    val criteria: SortCriteria = SortCriteria.PRIORITY,
    val order: SortOrder = SortOrder.ASC
)


class TaskViewModelFactory(private val taskRepository: TaskRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
            return TaskListViewModel(taskRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}