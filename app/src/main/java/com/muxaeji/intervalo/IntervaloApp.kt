package com.muxaeji.intervalo

import android.app.Application
import com.muxaeji.intervalo.core.CrashReporter
import timber.log.Timber

class IntervaloApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
        CrashReporter(this).install()
        Timber.i("Application started")
    }
}

private class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority <= android.util.Log.DEBUG) return
        android.util.Log.println(priority, tag ?: "Intervalo", message)
        if (t != null) {
            android.util.Log.println(priority, tag ?: "Intervalo", android.util.Log.getStackTraceString(t))
        }
    }
}
