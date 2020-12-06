package pl.lab.mobile.androiddebuggerlogger.domain

import android.app.Service
import android.os.Binder
import pl.lab.mobile.androiddebuggerlogger.data.model.LogMessage

class LoggerBinder(private val service: Service) : Binder(), Logger {

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