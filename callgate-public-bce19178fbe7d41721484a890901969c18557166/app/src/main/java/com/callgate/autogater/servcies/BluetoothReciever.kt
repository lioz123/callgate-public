package com.callgate.autogater.servcies

import android.app.AlarmManager
import android.app.PendingIntent
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.callgate.autogater.PropertiesObjects.Day
import com.callgate.autogater.PropertiesObjects.GateProperties
import com.callgate.autogater.database.DataBaseAdapter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.coroutineContext

class BluetoothReciever:BroadcastReceiver() {
    companion object BluetoothRecieverStatic{
        val BLUETOOTH_DEVICE_ACTION ="BLUETOOTH_DEVICE_ACTION"
        val DAY_KEY="DAY_KEY"


    }
    lateinit var currentDevice: BluetoothDevice
    override fun onReceive(context: Context?, intent: Intent?) {
        println("BluetoothReciver: onCreate")
        println("Recived event " + intent?.action )

        when(intent?.action){
            BluetoothDevice.ACTION_ACL_DISCONNECTED->{
                onBluetoothDisconected(intent, context)
            }
            BluetoothDevice.ACTION_ACL_CONNECTED->{
                onBluetoothDeviceConected(context, intent)
            }
            AlarmService.ALARM_ACTION->{

                onAlarmCalled(context, intent)
            }


        }
    }

    private fun onAlarmCalled(context: Context?, intent: Intent) {
        var startIntent=Intent(context, BackgroundLocationService::class.java)
        startIntent.action=BLUETOOTH_DEVICE_ACTION
        var dev=intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        startIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, dev)

        startSerivce(context, startIntent)
        context?.stopService(Intent(context, AlarmService::class.java))
    }

    private fun onBluetoothDeviceConected(
        context: Context?,
        intent: Intent
    ) {
        var gates=DataBaseAdapter(context!!).getAllGates()

        var device=intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        DataBaseAdapter(context).getAllBluetoothDevices().forEach {
            if (it.adress == device!!.address) {
                val startIntent=Intent(context, BackgroundLocationService::class.java)
                val gates=DataBaseAdapter(context).getAllGates()
                if (isGatesAviable(gates)) {


                    println("get closest time:${getClosetTime(gates)}")
                    startIntent.action=BLUETOOTH_DEVICE_ACTION
                    startIntent.putExtra(
                        BluetoothDevice.EXTRA_DEVICE,
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) as BluetoothDevice
                    )
                    println("start forground service at night")
                    startSerivce(context, startIntent)
                } else {
                    StartAlarmService(context,gates,device)
                 //   InitateAlarm(context,gates,device)
                }
            }
        }
    }


    fun InitateAlarm(c:Context, gates: ArrayList<GateProperties>, device: BluetoothDevice){
        println("AlarmService: onCreate: called")
        var day = getClosetTime(gates)
        println("Day in alarm service is $day")
        var calendar=getCalnendarFromDay(day)
        println("set time to $calendar")
        setAlarm(c, device, calendar)
    }

    private fun setAlarm(
        c: Context,
        device: BluetoothDevice,
        calendar: Calendar
    ) {
        var am=c.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var pendingIntent=getPendingIntentAlarm(c,device)
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    private fun getCalnendarFromDay(day: Day): Calendar {
        var calendar=Calendar.getInstance()
        var date=Date()
        calendar.time=date
        calendar.set(Calendar.DAY_OF_WEEK, day.day)
        calendar.set(Calendar.HOUR_OF_DAY, day.hour)
        calendar.set(Calendar.MINUTE, day.minute)
        return calendar
    }

    private fun StartAlarmService(
        context: Context?,
        gates: ArrayList<GateProperties>,
        device: BluetoothDevice?
    ) {
        var startIntent=Intent(context, AlarmService::class.java)

        var day=getClosetTime(gates)
        if (day.day != -1) {
            println("Best day is ${day}")
            startIntent.putExtra(DAY_KEY, day)
            startIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, device)
            startSerivce(context, startIntent)

        }
    }

    private fun startSerivce(
        context: Context?,
        startIntent: Intent
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(startIntent)

        } else {
            context?.startService(startIntent)
        }
    }

    fun cancelAlarm(c:Context,device: BluetoothDevice){
        var am=c.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var pendingIntent=getPendingIntentAlarm(c, device)
        am.cancel(pendingIntent)

    }

    private fun getPendingIntentAlarm(
        c: Context,
        device: BluetoothDevice
    ): PendingIntent? {
        var i=Intent(c, BluetoothReciever::class.java)
        i.putExtra(BluetoothDevice.EXTRA_DEVICE, device)
        i.action=AlarmService.ALARM_ACTION
        var pendingIntent=PendingIntent.getBroadcast(
            c, AlarmService.REQUEST_CODE, i, 0
        )
        return pendingIntent
    }

    private fun onBluetoothDisconected(
        intent: Intent,
        context: Context?
    ) {
        val device=intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        if(device!=null){
            closeBackgroundLocationService(device, context)

            cancelAlarm(context!!,device)
        }

        /*
        if(AlarmService.running){
            context?.stopService(Intent(context,AlarmService::class.java))
        }

         */
    }

    private fun closeBackgroundLocationService(
        device: BluetoothDevice,
        context: Context?
    ) {
        if (BackgroundLocationService.isServiceRunning && BackgroundLocationService.ACTION == BLUETOOTH_DEVICE_ACTION) {
            println("Background service is running  ")
            if (device.address == BackgroundLocationService.bluetoothDevice.address) {
                context?.stopService(Intent(context, BackgroundLocationService::class.java))

            } else if (device.address == AlarmService.BLUETOOTH_DEVICE.address) {
                context?.stopService(Intent(context, AlarmService::class.java))

            } else {
            }
        }
    }

    fun isGatesAviable(gates:ArrayList<GateProperties>):Boolean{
        gates.forEach{
            if(it.isGateAviableThroughTime()){
                println("gate is aviable $it")
                return true;
            }else{
                println("gate is not avivable")
            }
        }
        println("return false")
        return false
    }



    fun getClosetTime(gates:ArrayList<GateProperties>):Day{
        var cal = Calendar.getInstance()

        var bestDay = Day(-1,-1,-1)
        var start = true
        gates.forEach {
            var day = it.getClostTime()
            if(day.day!=-1){
                if(start){
                    start=false
                    bestDay=day
                }else{
                    if(day.day!=bestDay.day){
                    bestDay = getCloserDay(day,bestDay,cal.get(Calendar.DAY_OF_WEEK))
                    }else if(day.day==bestDay.day){
                        if(day.hour*60+day.minute<bestDay.hour*60+bestDay.minute){
                            bestDay=day
                        }
                    }
                }
            }
        }

        println("best day")
        return bestDay
    }

    fun getCloserDay(day1:Day,day2:Day,dayOfWeek:Int): Day {
        var sum1 = day1.day-dayOfWeek
        var sum2 = day2.day-dayOfWeek
        if(sum1>=0&&sum2>=0||sum1<0&&sum2<0){
            if(sum1<sum2){
                return day1
            }else{
                return  day2
            }
        }else{
            if(sum1>sum2){
                return day1
            }else{
                return day2
            }
        }
    }
}