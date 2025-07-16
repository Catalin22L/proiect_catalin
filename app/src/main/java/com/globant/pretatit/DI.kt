package com.globant.pretatit

import android.content.Context
import com.globant.pretatit.data.datasource.local.SharedPreferencesManager
import com.globant.pretatit.data.repository.TaskRepositoryImpl
import com.globant.pretatit.domain.repos.TaskRepository
import com.google.gson.Gson

object DI {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    // Am pastrat doar SharedPreferences si TaskRepository
    val sharedPrefs: SharedPreferencesManager by lazy {
        SharedPreferencesManager(appContext, Gson())
    }

    val taskRepository: TaskRepository by lazy {
        TaskRepositoryImpl(sharedPrefs)
    }
}