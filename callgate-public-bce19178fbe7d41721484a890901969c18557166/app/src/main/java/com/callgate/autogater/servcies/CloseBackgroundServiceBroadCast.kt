package com.callgate.autogater.servcies


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CloseBackgroundServiceBroadCast : BroadcastReceiver(){
    companion object CloseBackgroundServiceBroadCastStatics{
        var CLOSE_BACKGROUND_LISTENTER_ACTION="CLOSE_BACKGROUND_LISTENER_ACTION"
        var CLOSE_ALARM_SERVICE ="CLOSE_ALARM_SERVICE"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        var action = intent?.action
        println("close service was called " +action)
        when(action){
            BackgroundLocationService.ACTION_STOP_DRIVING-> context?.stopService(Intent(context,BackgroundLocationService::class.java))
            CLOSE_BACKGROUND_LISTENTER_ACTION -> {
                println("Stop the coorect service")
                      context?.stopService(Intent(context, BackgroundLocationService::class.java))

            }

            CLOSE_ALARM_SERVICE->{
                context?.stopService(Intent(context,AlarmService::class.java))
            }
        }



    }

}