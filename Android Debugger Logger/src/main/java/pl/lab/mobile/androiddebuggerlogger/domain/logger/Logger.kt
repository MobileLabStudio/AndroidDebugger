package pl.lab.mobile.androiddebuggerlogger.domain.logger

import android.app.Application
import android.content.*
import android.os.IBinder
import android.util.Log
import pl.lab.mobile.androiddebuggerlogger.ILogger
import pl.lab.mobile.androiddebuggerlogger.data.model.LogMessage

object Logger {

    private const val ARG_DEBUGGER_RUNNING = "ARG_DEBUGGER_RUNNING"

    private var logger: ILogger? = null
    private var appName: String = ""
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            logger = ILogger.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            logger = null
        }
    }

    private val debuggerStateBroadcastReceiver = DebuggerStateBroadcastReceiver()

    fun start(appName: String, app: Application) {
        this.appName = appName
        val intentFilter = IntentFilter("pl.lab.mobile.androiddebuggerlogger.debuggerStateChanged")
        app.registerReceiver(debuggerStateBroadcastReceiver, intentFilter)
    }

    fun stop(app: Application) {
        app.unregisterReceiver(debuggerStateBroadcastReceiver)
    }

    fun log(message: LogMessage) {
        try {
            logger?.log(message.toMap())
        } catch (e: Exception) {
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

    private class DebuggerStateBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent?) {
            val extras = intent?.extras ?: return
            val doesDebuggerRunning = extras.getBoolean(ARG_DEBUGGER_RUNNING, false)

            val bindServiceIntent = Intent().apply {
                setClassName(
                    "pl.lab.mobile.androiddebugger",
                    "pl.lab.mobile.androiddebugger.domain.service.DebuggerService"
                )
                action = "pl.lab.mobile.androiddebugger.bind"
            }

            if (doesDebuggerRunning) {
                context.bindService(
                    bindServiceIntent,
                    serviceConnection,
                    Context.BIND_ABOVE_CLIENT
                )
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