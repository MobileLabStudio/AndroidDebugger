package pl.lab.mobile.androiddebuggerlogger.domain

import pl.lab.mobile.androiddebuggerlogger.data.model.LogMessage

interface Logger {

    fun log(message: LogMessage)
}