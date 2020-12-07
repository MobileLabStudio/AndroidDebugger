package pl.lab.mobile.androiddebugger.domain.service

import android.app.Service
import android.os.Binder
import pl.lab.mobile.androiddebuggerlogger.data.model.LogMessage

internal class LoggerListenerBinder(private val service: Service) : Binder(), LoggerListener {

    private var listener: Listener? = null

    fun registerListener(listener: Listener) {
        this.listener = listener
    }

    fun unregisterListener() {
        this.listener = null
    }

    override fun log(message: LogMessage) {
        listener?.onLog(message)
    }

    fun stop() {
        service.stopForeground(true)
    }

    interface Listener {
        fun onLog(message: LogMessage)
    }
}