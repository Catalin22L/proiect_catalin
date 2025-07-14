package com.globant.pretatit.presentation

import java.io.Serializable

data class Task(
    val title: String,
    val description: String,
    val taskPriority: TaskPriority
) : Serializable

enum class TaskPriority {
    NONE,
    LOW,
    MEDIUM,
    HIGH,
    URGENT
    ;

    companion object {
        fun toListOfStrings(): List<String> {
            return entries.map {
                it.name
            }
        }

        fun getValueByName(name: String): TaskPriority {
            for (priority in entries) {
                if (priority.name == name) {
                    return priority
                }
            }
            return NONE
        }
    }
}
