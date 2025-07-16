package com.globant.pretatit.presentation

import java.io.Serializable
import java.util.UUID

// Am adaugat `dueDate` pentru a retine data limita
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val taskPriority: TaskPriority,
    val category: TaskCategory,
    val isDone: Boolean = false,
    val dueDate: Long? = null // Stocat ca timestamp (milisecunde)
) : Serializable

// Restul enum-urilor (TaskPriority, TaskCategory) raman la fel
enum class TaskPriority {
    NONE,
    LOW,
    MEDIUM,
    HIGH,
    URGENT;

    companion object {
        fun toListOfStrings(): List<String> = entries.map { it.name }
        fun getValueByName(name: String): TaskPriority = entries.find { it.name == name } ?: NONE
    }
}

enum class TaskCategory {
    WORK,
    PERSONAL,
    SHOPPING,
    OTHER;

    companion object {
        fun toListOfStrings(): List<String> = entries.map { it.name }
        fun getValueByName(name: String): TaskCategory = entries.find { it.name == name } ?: OTHER
    }
}