package pl.lab.mobile.androiddebugger.domain.notification

import android.content.Context

interface AppNotificationChannel {

    fun getChannelId(context: Context): String

    fun getChannelName(context: Context): String
}