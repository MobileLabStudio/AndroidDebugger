package pl.lab.mobile.androiddebugger

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import pl.lab.mobile.androiddebugger.domain.notification.AppNotification

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                AppNotification.Debugger.getChannelId(this),
                AppNotification.Debugger.getChannelName(this),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            NotificationManagerCompat
                .from(applicationContext)
                .createNotificationChannel(notificationChannel)
        }
    }
}