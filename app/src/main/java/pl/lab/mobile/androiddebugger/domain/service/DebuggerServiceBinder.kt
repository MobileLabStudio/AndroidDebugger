package pl.lab.mobile.androiddebugger.domain.service

import android.os.Binder
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import pl.lab.mobile.androiddebugger.domain.util.TimeUtil
import pl.lab.mobile.androiddebugger.presentation.MessagesAdapter
import pl.lab.mobile.androiddebuggerlogger.data.model.LogMessage

internal class DebuggerServiceBinder(val service: DebuggerService) : Binder() {

    val messages = MutableLiveData<MutableList<MessagesAdapter.Message>>()

    fun addMessage(jsonMessage: String?) {
        jsonMessage ?: return
        GlobalScope.launch(Dispatchers.Default) {
            val adapterMessage = LogMessage.fromJson(jsonMessage)
                ?.toAdapterMessage()
                ?: return@launch
            withContext(Dispatchers.Main) {
                messages.value = messages.value
                    ?.also { it.add(adapterMessage) }
                    ?: mutableListOf(adapterMessage)
            }
        }
    }

    fun addMessages(jsonMessages: List<String>?) {
        jsonMessages ?: return
        GlobalScope.launch(Dispatchers.Default) {
            val messages = jsonMessages
                .map { async(Dispatchers.Default) { LogMessage.fromJson(it) } }
                .awaitAll()
                .asSequence()
                .filterNotNull()
                .sortedBy { it.time }
                .map { it.toAdapterMessage() }

            withContext(Dispatchers.Main) {
                this@DebuggerServiceBinder.messages.value =
                    this@DebuggerServiceBinder.messages.value
                        ?.also { it.addAll(messages) }
                        ?: messages.toMutableList()
            }
        }
    }

    private fun LogMessage.toAdapterMessage(): MessagesAdapter.Message {
        return MessagesAdapter.Message(
            type = type,
            time = TimeUtil.format(time),
            app = app,
            message = message
        )
    }
}