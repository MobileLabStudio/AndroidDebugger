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
import pl.lab.mobile.androiddebugger.MainActivity
import pl.lab.mobile.androiddebugger.R
import pl.lab.mobile.androiddebuggerlogger.domain.LoggerBinder

class DebuggerService : Service() {

    private val loggerBinder = LoggerBinder(this)

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

        val pendingIntent = Intent(applicationContext, MainActivity::class.java)
            .run {
                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                PendingIntent.getActivity(
                    applicationContext,
                    MainActivity.REQUEST_CODE,
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
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = loggerBinder

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }

    private companion object {
        const val NOTIFICATION_ID = 1
    }
}