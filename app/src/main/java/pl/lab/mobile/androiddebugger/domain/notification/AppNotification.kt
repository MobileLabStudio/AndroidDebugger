package pl.lab.mobile.androiddebugger.domain.notification

import android.content.Context
import pl.lab.mobile.androiddebugger.R

object AppNotification {

    object Debugger : AppNotificationChannel {

        override fun getChannelId(context: Context) =
            context.getString(R.string.debugger_channel_id)

        override fun getChannelName(context: Context): String =
            context.getString(R.string.debugger_channel_name)
    }
}