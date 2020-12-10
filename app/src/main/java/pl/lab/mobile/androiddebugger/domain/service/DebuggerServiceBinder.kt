package pl.lab.mobile.androiddebugger.domain.service

import android.os.Binder
import androidx.lifecycle.MutableLiveData
import pl.lab.mobile.androiddebugger.domain.util.TimeUtil
import pl.lab.mobile.androiddebugger.presentation.MessagesAdapter
import pl.lab.mobile.androiddebuggerlogger.data.model.LogMessage

internal class DebuggerServiceBinder(val service: DebuggerService) : Binder(), LoggerListener {

    val messages = MutableLiveData<MutableList<MessagesAdapter.Message>>()

    fun addMessage(message: LogMessage) {
        val adapterMessage = MessagesAdapter.Message(
            type = message.type,
            time = TimeUtil.format(message.time),
            app = message.app,
            message = message.message
        )
        this.messages.postValue(this.messages.value
            ?.also { it.add(adapterMessage) }
            ?: mutableListOf(adapterMessage))
    }

    var listener: Listener? = null
        private set

    fun registerListener(listener: Listener) {
        this.listener = listener
    }

    fun unregisterListener() {
        this.listener = null
    }

    override fun log(message: LogMessage) {
        listener?.onLog(message)
    }

    interface Listener {
        fun onLog(message: LogMessage)
    }
}