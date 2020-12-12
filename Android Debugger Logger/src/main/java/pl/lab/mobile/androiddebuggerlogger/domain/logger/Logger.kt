package pl.lab.mobile.androiddebuggerlogger.domain.logger

import android.app.Application
import android.content.*
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import pl.lab.mobile.androiddebuggerlogger.ILogger
import pl.lab.mobile.androiddebuggerlogger.data.model.LogMessage
import java.util.*
import java.util.concurrent.*

object Logger {

    private const val ARG_DEBUGGER_RUNNING = "ARG_DEBUGGER_RUNNING"

    private var logger: ILogger? = null
    private var appName: String = ""
    private val messagesQueue = LinkedList<LogMessage>()
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            logger = ILogger.Stub.asInterface(service)
            if (messagesQueue.isNotEmpty()) {
                val executor = Executors.newSingleThreadExecutor()
                executor.execute {
                    val messages = messagesQueue.map(LogMessage::toJson)
                    val uiHandler = Handler(Looper.getMainLooper())
                    uiHandler.post {
                        logger?.logList(messages)
                        messagesQueue.clear()
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            logger = null
        }
    }
    private val debuggerStateBroadcastReceiver = DebuggerStateBroadcastReceiver()

    fun start(appName: String, app: Application) {
        this.appName = appName
        bindToDebuggerService(app)
        val intentFilter = IntentFilter("pl.lab.mobile.androiddebuggerlogger.debuggerStateChanged")
        app.registerReceiver(debuggerStateBroadcastReceiver, intentFilter)
    }

    fun stop(app: Application) {
        app.unregisterReceiver(debuggerStateBroadcastReceiver)
    }

    fun log(message: LogMessage) {
        if (logger == null) {
            messagesQueue.add(message)
            return
        }
        try {
            logger?.log(message.toJson())
        } catch (e: Exception) {
            messagesQueue.add(message)
            Log.d("logger", "cannot log", e)
        }
    }

    fun logInfo(message: String) {
        log(LogMessage(LogMessage.Type.INFO, appName, message))
    }

    fun logWarning(message: String) {
        log(LogMessage(LogMessage.Type.WARNING, appName, message))
    }

    fun logError(message: String) {
        log(LogMessage(LogMessage.Type.ERROR, appName, message))
    }

    fun logSuccess(message: String) {
        log(LogMessage(LogMessage.Type.SUCCESS, appName, message))
    }

    fun sendDebuggerState(context: Context, running: Boolean) {
        val intent = Intent("pl.lab.mobile.androiddebuggerlogger.debuggerStateChanged")
            .apply { putExtra(ARG_DEBUGGER_RUNNING, running) }
        context.sendBroadcast(intent)
    }

    private fun getDebuggerServiceIntent(): Intent {
        return Intent().apply {
            setClassName(
                "pl.lab.mobile.androiddebugger",
                "pl.lab.mobile.androiddebugger.domain.service.DebuggerService"
            )
            action = "pl.lab.mobile.androiddebugger.bind"
        }
    }

    private fun bindToDebuggerService(context: Context): Boolean {
        return context.bindService(
            getDebuggerServiceIntent(),
            serviceConnection,
            Context.BIND_ABOVE_CLIENT
        )
    }

    private class DebuggerStateBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent?) {
            val extras = intent?.extras ?: return
            val doesDebuggerRunning = extras.getBoolean(ARG_DEBUGGER_RUNNING, false)

            if (doesDebuggerRunning) {
                bindToDebuggerService(context)
            } else {
                try {
                    context.unbindService(serviceConnection)
                } catch (e: Exception) {
                    Log.d("logger", "Cannot bind to service", e)
                }
            }
        }
    }
}