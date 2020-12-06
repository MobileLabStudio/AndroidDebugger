package pl.lab.mobile.androiddebugger

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.TextView
import androidx.core.content.ContextCompat
import pl.lab.mobile.androiddebugger.domain.service.DebuggerService
import pl.lab.mobile.androiddebuggerlogger.ILogger
import pl.lab.mobile.androiddebuggerlogger.data.model.LogMessage
import pl.lab.mobile.androiddebuggerlogger.domain.LoggerBinder

class MainActivity : AppCompatActivity(), LoggerBinder.Listener {

    private var logger: LoggerBinder? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            logger = ILogger.Stub.asInterface(service).asBinder() as? LoggerBinder
            logger?.registerListener(this@MainActivity)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            logger?.unregisterListener()
            logger = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val debuggerServiceIntent = Intent(this, DebuggerService::class.java)
        ContextCompat.startForegroundService(this, debuggerServiceIntent)
        bindService(debuggerServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onLog(message: LogMessage) {
        findViewById<TextView>(R.id.text_view).text = message.message + message.type
    }

    override fun onDestroy() {
        super.onDestroy()
        logger?.unregisterListener()
        logger?.stop()
        unbindService(serviceConnection)
    }

    companion object {
        const val REQUEST_CODE = 1
    }
}