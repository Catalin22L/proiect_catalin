package com.globant.pretatit

import android.content.Context
import androidx.room.Room
import com.globant.pretatit.data.datasource.local.db.PregatitDatabase
import com.globant.pretatit.data.datasource.local.SharedPreferencesManager
import com.globant.pretatit.data.datasource.remote.TaskApi
import com.globant.pretatit.data.repository.TaskRepositoryImpl
import com.globant.pretatit.domain.repos.TaskRepository
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val DATABASE_NAME = "pregatit.db"
private const val BASE_URL = "https://pregatit.free.beeceptor.com/"

object DI {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val sharedPrefs: SharedPreferencesManager by lazy {
        SharedPreferencesManager(appContext, Gson())
    }

    val taskRepository: TaskRepository by lazy {
        TaskRepositoryImpl(database, retrofit.create(TaskApi::class.java))
    }

    val database: PregatitDatabase by lazy {
        Room.databaseBuilder(appContext, PregatitDatabase::class.java, DATABASE_NAME)
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}