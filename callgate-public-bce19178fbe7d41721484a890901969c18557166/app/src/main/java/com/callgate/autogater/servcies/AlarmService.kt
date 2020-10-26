package com.callgate.autogater.servcies

import android.R
import android.app.*
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.callgate.autogater.PropertiesObjects.Day
import com.callgate.autogater.activities.MainActivity
import java.util.*

class AlarmService : Service() {
    companion object AlarmServiceStatics{
      val  CHANNEL_ID="CALL_GATE_ALARM_SERVICE"
       val REQUEST_CODE=1213
        val ALARM_ACTION ="ALARM_ACTION"
        lateinit var BLUETOOTH_DEVICE:BluetoothDevice
        var running=false

    }
    val binder = AlarmServiceBinder()
    lateinit var pendingIntent:PendingIntent
    override fun onBind(intent: Intent?): IBinder? {

        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("On start command")
        var day= intent!!.getParcelableExtra<Day>(BluetoothReciever.DAY_KEY)
        println("Day in alarm service is $day")
        println("AlarmService: onCreate: called")
        CreateNotification(day)
        BLUETOOTH_DEVICE = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        var calendar = Calendar.getInstance()
        var date = Date()
        calendar.time=date
        calendar.set(Calendar.DAY_OF_WEEK,day.day)
        calendar.set(Calendar.HOUR_OF_DAY,day.hour)
        calendar.set(Calendar.MINUTE,day.minute)

        println("set time to $calendar")
        var am = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var i = Intent(this,BluetoothReciever::class.java)
        var device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        i.putExtra(BluetoothDevice.EXTRA_DEVICE, device)
        i.action= ALARM_ACTION
        pendingIntent = PendingIntent.getBroadcast(applicationContext, REQUEST_CODE,i, 0)
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,pendingIntent)
        running=true
        return super.onStartCommand(intent, flags, startId)
    }

    inner class AlarmServiceBinder: Binder(){
        fun getService(): AlarmService = this@AlarmService
    }
    override fun onCreate() {
        super.onCreate()


    }

    override fun onDestroy() {
        var am = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        am.cancel(pendingIntent)
        running=false
        super.onDestroy()
    }

    fun CreateNotification(day:Day){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            var serviceChannel = NotificationChannel(CHANNEL_ID,"auto gater foreground service location",
                NotificationManager.IMPORTANCE_HIGH)
            var notificationManager= getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(serviceChannel)
        }
        var intent = Intent(this, MainActivity::class.java)
        var pendingIntent = PendingIntent.getActivity(this,0, intent,0)
        var stopIntent = Intent(this,CloseBackgroundServiceBroadCast::class.java)
        stopIntent.action= CloseBackgroundServiceBroadCast.CLOSE_ALARM_SERVICE
        var minuteStr = day.minute.toString()
        if(day.minute<10){
            minuteStr="0$minuteStr"
        }
        var textContent = resources.getString(com.callgate.autogater.R.string.waiting_for_gate) +" ${day.hour}:${minuteStr}"
        var pendingStop = PendingIntent.getBroadcast(this,30,stopIntent,0)
        var notification = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("CallGate")
            .setContentText(textContent)
            .setSmallIcon(com.callgate.autogater.R.drawable.appicon)
            //   .addAction(com.example.autogater.R.drawable.dismiss_test,"Stop")
            .addAction(R.drawable.ic_menu_search,getString(com.callgate.autogater.R.string.stop),pendingStop)

            .setContentIntent(pendingIntent).build()

        //   notificationManager.notify(0,notification)
        startForeground(1,notification)

    }
}