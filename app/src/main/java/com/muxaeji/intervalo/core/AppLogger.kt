package com.muxaeji.intervalo.core

import timber.log.Timber

object AppLogger {
    fun d(tag: String, message: String) = Timber.tag(tag).d(message)

    fun i(tag: String, message: String) = Timber.tag(tag).i(message)

    fun w(tag: String, message: String) = Timber.tag(tag).w(message)

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Timber.tag(tag).e(throwable, message)
        } else {
            Timber.tag(tag).e(message)
        }
    }
}
