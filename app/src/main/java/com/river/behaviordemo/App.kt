package com.river.behaviordemo

import android.app.Application
import android.util.Log
import com.river.behavior.BehaviorManager

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        BehaviorManager.init(this).setBehaviorListener {
            Log.i("TRACE_BEHAVIOR", it.toString())
        }
    }
}