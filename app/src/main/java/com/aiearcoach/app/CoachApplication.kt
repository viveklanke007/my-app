package com.aiearcoach.app

import android.app.Application
import com.aiearcoach.app.data.AppDatabase

class CoachApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Database eagerly
        AppDatabase.getInstance(this)
    }
}
