package com.hiring.somanath_task.util.logging

class TestLogger : Logger {
    private val logs = mutableListOf<String>()
    
    fun getLogs(): List<String> = logs.toList()
    fun clearLogs() = logs.clear()

    override fun d(tag: String, message: String) {
        logs.add("D/$tag: $message")
        println("D/$tag: $message")
    }

    override fun i(tag: String, message: String) {
        logs.add("I/$tag: $message")
        println("I/$tag: $message")
    }

    override fun w(tag: String, message: String) {
        logs.add("W/$tag: $message")
        println("W/$tag: $message")
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        val errorMessage = if (throwable != null) {
            "$message - ${throwable.message}"
        } else {
            message
        }
        logs.add("E/$tag: $errorMessage")
        println("E/$tag: $errorMessage")
    }
}