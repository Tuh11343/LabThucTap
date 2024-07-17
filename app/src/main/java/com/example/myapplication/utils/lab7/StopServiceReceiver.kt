package com.example.myapplication.utils.lab7
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.myapplication.services.LocationService

class StopServiceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, LocationService::class.java)
        context.stopService(serviceIntent)
    }
}