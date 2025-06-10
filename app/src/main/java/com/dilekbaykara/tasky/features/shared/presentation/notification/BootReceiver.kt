package com.dilekbaykara.tasky.features.shared.presentation.notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device restarted - rescheduling notifications")
        }
    }
}
