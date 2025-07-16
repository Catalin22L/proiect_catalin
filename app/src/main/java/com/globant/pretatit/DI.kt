package com.globant.pretatit

import android.content.Context
import androidx.room.Room
import com.globant.pretatit.data.datasource.PregatitDatabase
import com.globant.pretatit.data.datasource.SharedPreferencesManager
import com.globant.pretatit.data.repository.TaskRepositoryImpl
import com.globant.pretatit.domain.repos.TaskRepository
import com.google.gson.Gson

private const val DATABASE_NAME = "pregatit.db"

object DI {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val sharedPrefs: SharedPreferencesManager by lazy {
        SharedPreferencesManager(appContext, Gson())
    }

    val taskRepository: TaskRepository by lazy {
        TaskRepositoryImpl(database)
    }

    val database: PregatitDatabase by lazy {
        Room.databaseBuilder(appContext, PregatitDatabase::class.java, DATABASE_NAME)
            .build()
    }
}