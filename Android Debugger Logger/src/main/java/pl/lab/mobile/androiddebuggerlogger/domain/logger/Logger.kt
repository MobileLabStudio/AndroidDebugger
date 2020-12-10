package pl.lab.mobile.androiddebuggerlogger.domain.logger

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import pl.lab.mobile.androiddebuggerlogger.ILogger
import pl.lab.mobile.androiddebuggerlogger.data.model.LogMessage

object Logger {

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

    fun start(appName: String, app: Application) {
        this.appName = appName
        val intent = Intent().apply {
            setPackage("pl.lab.mobile.androiddebugger")
            action = "pl.lab.mobile.androiddebugger.bind"
        }
        val bind = app.applicationContext.bindService(
            intent,
            serviceConnection,
            Context.BIND_ABOVE_CLIENT
        )
    }

    fun stop(app: Application) {
        app.unbindService(serviceConnection)
        logger = null
    }

    fun log(message: LogMessage) {
        logger?.log(message.toMap())
    }

    fun logInfo(message: String) {
        logger?.log(LogMessage(LogMessage.Type.INFO, appName, message).toMap())
    }

    fun logWarning(message: String) {
        logger?.log(LogMessage(LogMessage.Type.WARNING, appName, message).toMap())
    }

    fun logError(message: String) {
        logger?.log(LogMessage(LogMessage.Type.ERROR, appName, message).toMap())
    }

    fun logSuccess(message: String) {
        logger?.log(LogMessage(LogMessage.Type.SUCCESS, appName, message).toMap())
    }
}