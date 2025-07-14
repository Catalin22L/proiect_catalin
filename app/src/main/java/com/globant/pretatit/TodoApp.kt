package com.globant.pretatit

import android.app.Application
import timber.log.Timber

class TodoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        DI.init(this)
    }
}