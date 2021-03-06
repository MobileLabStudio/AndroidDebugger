package pl.lab.mobile.androiddebugger.domain.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import pl.lab.mobile.androiddebugger.presentation.DebuggerActivity
import pl.lab.mobile.androiddebugger.R
import pl.lab.mobile.androiddebugger.domain.notification.AppNotification
import pl.lab.mobile.androiddebuggerlogger.ILogger
import pl.lab.mobile.androiddebuggerlogger.domain.logger.Logger

class DebuggerService : Service() {

    private val iLogger = object : ILogger.Stub() {
        val binder = DebuggerServiceBinder(this@DebuggerService)

        override fun asBinder(): IBinder = binder

        override fun log(messageJson: String?) {
            binder.addMessage(messageJson)
        }

        override fun logList(jsonMessages: MutableList<String>?) {
            binder.addMessages(jsonMessages)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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

        val notification = NotificationCompat
            .Builder(
                this,
                AppNotification.Debugger.getChannelId(applicationContext)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentTitle("Android Debugger")
            .setSubText("running")
            .setContentText("Android debugger is active")
            .setSmallIcon(R.drawable.ic_android)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)
        isRunningMutable.postValue(true)
        Logger.sendDebuggerState(applicationContext, true)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return iLogger
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunningMutable.postValue(false)
        Logger.sendDebuggerState(applicationContext, false)
    }

    companion object {
        private val isRunningMutable = MutableLiveData(false)
        val isRunning: LiveData<Boolean>
            get() = isRunningMutable

        private const val NOTIFICATION_ID = 1
    }
}