package pl.lab.mobile.androiddebugger.domain.service

import pl.lab.mobile.androiddebuggerlogger.data.model.LogMessage

internal interface LoggerListener {

    fun log(message: LogMessage)
}