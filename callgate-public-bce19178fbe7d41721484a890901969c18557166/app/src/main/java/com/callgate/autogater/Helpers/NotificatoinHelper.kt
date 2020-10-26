package com.callgate.autogater.Helpers

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.callgate.autogater.activities.MainActivity
import com.callgate.autogater.servcies.CloseBackgroundServiceBroadCast


class NotificatoinHelper {
    companion object{

        fun CreateNotificationForeground(service:Service, id:Int, str:String, channel_ID:String){
            var c = service.applicationContext
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
                val serviceChannel = NotificationChannel(channel_ID,"auto gater foreground service location",
                    NotificationManager.IMPORTANCE_HIGH)
                val notificationManager= c.getSystemService(NotificationManager::class.java)
                notificationManager?.createNotificationChannel(serviceChannel)
            }
            val intent = Intent(c, MainActivity::class.java)
            val pendingIntent =PendingIntent.getActivity(c,0, intent,0)
            val stopIntent = Intent(c, CloseBackgroundServiceBroadCast::class.java)
            stopIntent.action= CloseBackgroundServiceBroadCast.CLOSE_BACKGROUND_LISTENTER_ACTION

            val pendingStop = PendingIntent.getBroadcast(c,30,stopIntent,0)
            val notification = NotificationCompat.Builder(c,channel_ID)
                .setContentTitle("CallGate")
                .setContentText(str)
                .setSmallIcon(com.callgate.autogater.R.drawable.appicon)
                //   .addAction(com.example.autogater.R.drawable.dismiss_test,"Stop")
                .addAction(R.drawable.ic_menu_search,c.getString(com.callgate.autogater.R.string.stop),pendingStop)
                .setContentIntent(pendingIntent).build()

            //   notificationManager.notify(0,notification)
            service.startForeground(id,notification)


        }
    }
}