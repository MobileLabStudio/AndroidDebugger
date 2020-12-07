package pl.lab.mobile.androiddebuggerlogger.domain.logger

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import pl.lab.mobile.androiddebuggerlogger.ILogger
import pl.lab.mobile.androiddebuggerlogger.data.model.LogMessage

object Logger {

    private var logger: ILogger? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            logger = ILogger.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            logger = null
        }
    }

    fun start(context: Context) {
        val intent = Intent().apply {
            setPackage("pl.lab.mobile.androiddebugger")
            action = "pl.lab.mobile.androiddebugger.bind"
        }
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun stop(context: Context) {
        context.unbindService(serviceConnection)
    }

    fun log(message: LogMessage) {
        logger?.log(message.toMap())
    }
}