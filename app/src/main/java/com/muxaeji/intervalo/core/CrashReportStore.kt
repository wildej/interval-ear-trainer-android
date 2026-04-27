package com.muxaeji.intervalo.core

import android.content.Context
import java.io.File

object CrashReportStore {
    private const val CRASH_DIR_NAME = "crash-reports"

    fun crashDir(context: Context): File = File(context.filesDir, CRASH_DIR_NAME)

    fun hasReports(context: Context): Boolean =
        crashDir(context).listFiles()?.isNotEmpty() == true

    fun latestReport(context: Context): File? =
        crashDir(context).listFiles()?.maxByOrNull { it.lastModified() }
}
