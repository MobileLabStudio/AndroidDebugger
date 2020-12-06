package pl.lab.mobile.androiddebuggersample

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.EditText
import pl.lab.mobile.androiddebuggerlogger.ILogger
import pl.lab.mobile.androiddebuggerlogger.data.model.LogMessage

class MainActivity : AppCompatActivity() {

    private var logger: ILogger? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            logger = ILogger.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            logger = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.log_message_button).setOnClickListener {
            val content = findViewById<EditText>(R.id.message_edit_text)
                .text
                ?.toString()
                ?: return@setOnClickListener
            val message =
                LogMessage(
                    LogMessage.Type.INFO,
                    "sample app",
                    content,
                    System.currentTimeMillis()
                )
            logger?.log(message.toMap())
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent().apply {
            setPackage("pl.lab.mobile.androiddebugger")
            action = "pl.lab.mobile.androiddebugger.StartDebugger"
        }
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }
}