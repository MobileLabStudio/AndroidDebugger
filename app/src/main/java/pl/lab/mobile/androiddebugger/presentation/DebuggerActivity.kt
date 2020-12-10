package pl.lab.mobile.androiddebugger.presentation

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.lab.mobile.androiddebugger.R
import pl.lab.mobile.androiddebugger.databinding.ActivityMainBinding
import pl.lab.mobile.androiddebugger.domain.service.DebuggerService
import pl.lab.mobile.androiddebuggerlogger.data.model.LogMessage
import pl.lab.mobile.androiddebugger.domain.service.DebuggerServiceBinder
import pl.lab.mobile.androiddebuggerlogger.ILogger

class DebuggerActivity : AppCompatActivity(), DebuggerServiceBinder.Listener {

    private var isRunning = false
    private lateinit var binding: ActivityMainBinding
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var debuggerServiceBinder: DebuggerServiceBinder
    private lateinit var debuggerStateBroadcastReceiver: BroadcastReceiver
    private lateinit var serviceConnection: ServiceConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        messagesAdapter = MessagesAdapter()
        binding.messagesRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(this@DebuggerActivity, RecyclerView.VERTICAL, false).apply {
                    stackFromEnd = true
                }
            adapter = messagesAdapter
            addItemDecoration(
                DividerItemDecoration(
                    this@DebuggerActivity,
                    RecyclerView.VERTICAL
                )
            )
        }

        messagesAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                binding.messagesRecyclerView.scrollToPosition(0)
            }
        })

        binding.startStopButton.setOnClickListener {
            if (isRunning) {
                unbindService(serviceConnection)
                debuggerServiceBinder.service.stopSelf()
            } else {
                val intent = Intent(applicationContext, DebuggerService::class.java)
                ContextCompat.startForegroundService(this, intent)
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        debuggerStateBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                isRunning = intent?.takeIf { it.action == DEBUGGER_STATE_INTENT_ACTION }
                    ?.extras
                    ?.getBoolean(DebuggerService.ARG_DEBUGGER_STATE) == true
                updateUi(isRunning)
            }
        }
        registerReceiver(debuggerStateBroadcastReceiver, IntentFilter(DEBUGGER_STATE_INTENT_ACTION))
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder) {
                debuggerServiceBinder = ILogger.Stub
                    .asInterface(service)
                    .asBinder() as DebuggerServiceBinder
                debuggerServiceBinder.registerListener(this@DebuggerActivity)
                debuggerServiceBinder.messages.observe(debuggerServiceBinder.service.lifecycleOwner) { messages ->
                    messagesAdapter.submitList(messages)
                    binding.messagesRecyclerView.scrollToPosition(messagesAdapter.itemCount - 1)
                }
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                // DO NOTHING
            }
        }
        val intent = Intent(applicationContext, DebuggerService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(debuggerStateBroadcastReceiver)
        try {
            unbindService(serviceConnection)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onLog(message: LogMessage) {
        debuggerServiceBinder.addMessage(message)
    }

    private fun updateUi(started: Boolean) {
        binding.startStopButton.setText(if (started) R.string.stop else R.string.start)
    }

    companion object {
        const val REQUEST_CODE = 1
        const val DEBUGGER_STATE_INTENT_ACTION = "state"
    }
}