package dev.dubsky.aiko.logging

import com.apollographql.apollo.api.BooleanExpression
import dev.dubsky.aiko.config.ConfigManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Logger {
    private val logDirectory = "${System.getenv("AppData")}/Aiko/logs"
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val timestampFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    var DEBUG: Boolean = ConfigManager.config.Logging

    init {
        File(logDirectory).mkdirs()
    }

    private fun getLogFile(): File {
        val date = dateFormat.format(Date())
        return File("$logDirectory/$date.log")
    }

    fun log(level: LogLevel, className: String, message: String) {
        if (!DEBUG) return
        val timestamp = timestampFormat.format(Date())
        val logEntry = "[$timestamp] [$level] [$className]: $message\n"

        getLogFile().appendText(logEntry)
    }

}