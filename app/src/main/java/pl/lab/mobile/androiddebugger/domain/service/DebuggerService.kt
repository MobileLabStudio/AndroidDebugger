package pl.lab.mobile.androiddebugger.domain.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.*
import pl.lab.mobile.androiddebugger.presentation.DebuggerActivity
import pl.lab.mobile.androiddebugger.R
import pl.lab.mobile.androiddebuggerlogger.ILogger
import pl.lab.mobile.androiddebuggerlogger.data.model.LogMessage

class DebuggerService : Service() {

    private var started = false
    val lifecycleOwner = ServiceLifecycleOwner()
    private val iLogger = object : ILogger.Stub() {
        val binder = DebuggerServiceBinder(this@DebuggerService)

        override fun asBinder(): IBinder = binder

        override fun log(messageJson: MutableMap<String, String>?) {
            val message = LogMessage.fromMap(messageJson) ?: return
            binder.log(message)
        }
    }

    override fun onCreate() {
        super.onCreate()
        lifecycleOwner.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channelId = "Android Debugger"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                "Android Debugger",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            NotificationManagerCompat
                .from(applicationContext)
                .createNotificationChannel(notificationChannel)
        }

        val pendingIntent =
            applicationContext.packageManager.getLaunchIntentForPackage("pl.lab.mobile.androiddebugger")
                ?.run {
                    PendingIntent.getActivity(
                        applicationContext,
                        DebuggerActivity.REQUEST_CODE,
                        this,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }

        val notification = NotificationCompat.Builder(this, channelId)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentTitle("Android Debugger")
            .setSubText("running")
            .setContentText("Android debugger is active")
            .setSmallIcon(R.drawable.ic_android)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)
        started = true
        sendState(true)
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder {
        sendState(started)
        return iLogger
    }

    override fun onDestroy() {
        sendState(false)
        lifecycleOwner.onDestroy()
        super.onDestroy()
    }

    private fun sendState(started: Boolean) {
        val intent = Intent(DebuggerActivity.DEBUGGER_STATE_INTENT_ACTION).apply {
            putExtra(ARG_DEBUGGER_STATE, started)
        }
        sendBroadcast(intent)
    }

    inner class ServiceLifecycleOwner : LifecycleOwner {

        private val registry = LifecycleRegistry(this)

        override fun getLifecycle(): Lifecycle = registry

        fun onCreate() {
            registry.currentState = Lifecycle.State.STARTED
        }

        fun onDestroy() {
            registry.currentState = Lifecycle.State.DESTROYED
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        const val ARG_DEBUGGER_STATE = "debugger state"
    }
}