package pl.lab.mobile.androiddebugger.presentation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import pl.lab.mobile.androiddebugger.databinding.ActivityMainBinding
import pl.lab.mobile.androiddebugger.domain.service.DebuggerService
import pl.lab.mobile.androiddebuggerlogger.ILogger
import pl.lab.mobile.androiddebuggerlogger.data.model.LogMessage
import pl.lab.mobile.androiddebugger.domain.service.LoggerListenerBinder

class MainActivity : AppCompatActivity(), LoggerListenerBinder.Listener {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding
    private lateinit var messagesAdapter: MessagesAdapter
    private var logger: LoggerListenerBinder? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            logger = ILogger.Stub.asInterface(service).asBinder() as? LoggerListenerBinder
            logger?.registerListener(this@MainActivity)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            logger?.unregisterListener()
            logger = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val debuggerServiceIntent = Intent(this, DebuggerService::class.java)
        ContextCompat.startForegroundService(this, debuggerServiceIntent)
        bindService(debuggerServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        messagesAdapter = MessagesAdapter()
        binding.messagesRecyclerView.adapter = messagesAdapter
        binding.messagesRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                RecyclerView.VERTICAL
            )
        )

        viewModel.messages.observe(this) { messages ->
            messagesAdapter.submitList(null)
            messagesAdapter.submitList(messages)
        }
    }

    override fun onLog(message: LogMessage) {
        viewModel.addMessage(message)
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