package com.globant.pretatit.data.datasource.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.globant.pretatit.presentation.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val PREF_NAME = "my_app_prefs"

class SharedPreferencesManager(
    context: Context,
    private val gson: Gson
) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getString(key: String): String {
        return preferences.getString(key, "") ?: ""
    }

    fun putString(key: String, value: String) {
        preferences.edit { putString(key, value) }
    }

    fun saveTask(key: String, task: Task) {
        val list = getTasks(key).toMutableList()
        list.add(task)

        val json = gson.toJson(list)
        putString(key, json)
    }

    fun getTasks(key: String): List<Task> {
        val serialized = getString(key)

        if (serialized.isEmpty()) {
            return emptyList()
        }

        val type = object : TypeToken<List<Task>>() {}.type
        return gson.fromJson(serialized, type)
    }
}