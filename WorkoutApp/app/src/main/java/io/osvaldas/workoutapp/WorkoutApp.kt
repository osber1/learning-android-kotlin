package io.osvaldas.workoutapp

import android.app.Application
import io.osvaldas.workoutapp.history.HistoryDatabase

class WorkoutApp : Application() {
    val db by lazy {
        HistoryDatabase.getInstance(this)
    }
}