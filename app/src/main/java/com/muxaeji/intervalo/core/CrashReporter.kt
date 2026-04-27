package com.muxaeji.intervalo.core

import android.content.Context
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Instant
import timber.log.Timber

class CrashReporter(
    private val context: Context
) {
    private val crashDir: File = CrashReportStore.crashDir(context)

    fun install() {
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runCatching {
                persistCrash(thread, throwable)
            }.onFailure {
                Timber.e(it, "Failed to persist crash report")
            }
            previousHandler?.uncaughtException(thread, throwable)
        }
        logPendingCrashes()
    }

    private fun persistCrash(thread: Thread, throwable: Throwable) {
        if (!crashDir.exists()) crashDir.mkdirs()
        val now = Instant.now().toString().replace(":", "-")
        val file = File(crashDir, "crash_$now.txt")
        file.writeText(
            buildString {
                appendLine("timestamp=$now")
                appendLine("thread=${thread.name}")
                appendLine("exception=${throwable::class.java.name}")
                appendLine("message=${throwable.message.orEmpty()}")
                appendLine("--- stacktrace ---")
                appendLine(stacktrace(throwable))
            }
        )
        cleanupOldReports()
    }

    private fun logPendingCrashes() {
        if (!crashDir.exists()) return
        crashDir.listFiles()
            ?.sortedByDescending { it.lastModified() }
            ?.take(3)
            ?.forEach { report ->
                Timber.w("Pending crash report detected: %s", report.absolutePath)
            }
    }

    private fun cleanupOldReports() {
        crashDir.listFiles()
            ?.sortedByDescending { it.lastModified() }
            ?.drop(MAX_REPORTS)
            ?.forEach { it.delete() }
    }

    private fun stacktrace(throwable: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        throwable.printStackTrace(pw)
        return sw.toString()
    }

    private companion object {
        const val MAX_REPORTS = 10
    }
}
