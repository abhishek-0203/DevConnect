package com.example.blogmultiplatform.util

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.logging.FileHandler
import java.util.logging.Formatter
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger as JLogger

class PlainFormatter : Formatter() {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    override fun format(record: LogRecord): String {
        val timestamp = dateFormat.format(Date(record.millis))
        return "[$timestamp] " + record.message + "\n"
    }
}

object LoggerImpl : Logger {
    private val logger: JLogger = JLogger.getLogger("SiteLogger")
    private var fileHandler: FileHandler? = null
    init {
        try {
            if (logger.handlers.isEmpty()) {
                val logFile = File("site.log").absolutePath
                println("LoggerImpl: Writing logs to $logFile")
                fileHandler = FileHandler(logFile, true)
                fileHandler?.formatter = PlainFormatter()
                logger.addHandler(fileHandler)
                logger.useParentHandlers = false
                logger.level = Level.ALL
                logger.info("Logger initialized. Writing to $logFile")
                fileHandler?.flush()
            }
        } catch (e: Exception) {
            println("LoggerImpl: Exception during logger initialization: ${e.message}")
            e.printStackTrace()
        }
    }
    override fun info(message: String, file: String, function: String) {
        logger.info("[$file::$function] $message")
        fileHandler?.flush()
    }
    override fun warning(message: String, file: String, function: String) {
        logger.warning("[$file::$function] $message")
        fileHandler?.flush()
    }
    override fun error(message: String, file: String, function: String) {
        logger.severe("[$file::$function] $message")
        fileHandler?.flush()
    }
}
