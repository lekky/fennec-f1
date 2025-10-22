package com.f1calendar.util

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object AppLogger {
    private const val TAG = "F1Calendar"
    private val logs = mutableListOf<String>()
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    fun d(tag: String, message: String) {
        val timestamp = dateFormat.format(Date())
        val logMessage = "[$timestamp] DEBUG/$tag: $message"
        logs.add(logMessage)
        Log.d(TAG, "[$tag] $message")

        // Keep only last 500 logs
        if (logs.size > 500) {
            logs.removeAt(0)
        }
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        val timestamp = dateFormat.format(Date())
        val logMessage = if (throwable != null) {
            "[$timestamp] ERROR/$tag: $message\n${throwable.stackTraceToString()}"
        } else {
            "[$timestamp] ERROR/$tag: $message"
        }
        logs.add(logMessage)
        Log.e(TAG, "[$tag] $message", throwable)

        // Keep only last 500 logs
        if (logs.size > 500) {
            logs.removeAt(0)
        }
    }

    fun i(tag: String, message: String) {
        val timestamp = dateFormat.format(Date())
        val logMessage = "[$timestamp] INFO/$tag: $message"
        logs.add(logMessage)
        Log.i(TAG, "[$tag] $message")

        // Keep only last 500 logs
        if (logs.size > 500) {
            logs.removeAt(0)
        }
    }

    fun getAllLogs(): String {
        return logs.joinToString("\n")
    }

    fun clear() {
        logs.clear()
    }
}
