package com.dilekbaykara.tasky.features.shared.presentation.notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
class NotificationSyncService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}
