package com.globant.pretatit.data.datasource.remote

import com.globant.pretatit.presentation.Task
import retrofit2.http.GET

interface TaskApi {

    @GET("tasks")
    suspend fun getAllTasks(): List<Task>
}