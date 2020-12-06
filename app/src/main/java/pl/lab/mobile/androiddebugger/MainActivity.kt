package pl.lab.mobile.androiddebugger

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import androidx.core.content.ContextCompat
import pl.lab.mobile.androiddebugger.domain.service.DebuggerService
import pl.lab.mobile.androiddebuggerlogger.data.model.LogMessage
import pl.lab.mobile.androiddebuggerlogger.domain.LoggerBinder

class MainActivity : AppCompatActivity(), LoggerBinder.Listener {

    private var loggerBinder: LoggerBinder? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            loggerBinder = service as? LoggerBinder
            loggerBinder?.registerListener(this@MainActivity)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
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

    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
        loggerBinder?.stop()
        loggerBinder?.unregisterListener()
    }

    companion object {
        const val REQUEST_CODE = 1
    }
}