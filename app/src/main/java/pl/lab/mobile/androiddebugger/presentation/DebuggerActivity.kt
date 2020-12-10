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

class DebuggerActivity : AppCompatActivity() {

    // UI
    private lateinit var binding: ActivityMainBinding
    private lateinit var messagesAdapter: MessagesAdapter

    // Service
    private var debuggerServiceBinder: DebuggerServiceBinder? = null
    private var serviceConnection: ServiceConnection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messagesAdapter = MessagesAdapter()
        binding.messagesRecyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(
                this@DebuggerActivity,
                RecyclerView.VERTICAL,
                false
            )
            linearLayoutManager.stackFromEnd = true
            layoutManager = linearLayoutManager
            adapter = messagesAdapter
            val divider = DividerItemDecoration(this@DebuggerActivity, RecyclerView.VERTICAL)
            addItemDecoration(divider)
        }
        messagesAdapter.registerAdapterDataObserver(AdapterDataObserver())

        binding.startStopButton.setOnClickListener {
            if (DebuggerService.isRunning.value == true) {
                unbindService()
                stopService()
            } else {
                startService()
                bindService()
            }
        }

        if (DebuggerService.isRunning.value == true) {
            bindService()
        }

        DebuggerService.isRunning.observe(this) {
            binding.startStopButton.setText(if (it) R.string.stop else R.string.start)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService()
    }

    private fun unbindService() {
        try {
            val serviceConnection = this.serviceConnection
            if (serviceConnection != null) {
                unbindService(serviceConnection)
                this.serviceConnection = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun bindService() {
        val serviceConnection = this.serviceConnection
            ?: DebuggerServiceConnection().also { serviceConnection = it }
        bindService(getServiceIntent(), serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun startService() {
        ContextCompat.startForegroundService(applicationContext, getServiceIntent())
    }

    private fun stopService() {
        debuggerServiceBinder?.service?.stopSelf()
        debuggerServiceBinder = null
    }

    private fun getServiceIntent() = Intent(applicationContext, DebuggerService::class.java)

    private inner class AdapterDataObserver : RecyclerView.AdapterDataObserver() {

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            binding.messagesRecyclerView.scrollToPosition(0)
        }
    }

    private inner class DebuggerServiceConnection : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val debuggerServiceBinder = ILogger.Stub
                .asInterface(service)
                .asBinder() as DebuggerServiceBinder
            debuggerServiceBinder.messages.observe(this@DebuggerActivity) { messages ->
                messagesAdapter.submitList(messages)
                binding.messagesRecyclerView.scrollToPosition(messagesAdapter.itemCount - 1)
            }
            this@DebuggerActivity.debuggerServiceBinder = debuggerServiceBinder
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            // Do nothing
        }
    }

    companion object {
        const val REQUEST_CODE = 1
    }
}