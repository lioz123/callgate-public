package com.callgate.autogater.PropertiesObjects

import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.Button
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.callgate.autogater.activities.TimeConfiguration
import com.callgate.autogater.database.DataHelperGateProperties
import com.google.android.material.appbar.AppBarLayout
import java.time.DayOfWeek

import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min

class TimeProperties(var closeTime:String, var openTime:String,var days:ArrayList<String> ,var gateTimeBehavior:String) {

    companion object TimePropertiesStatics{
     val DAY_NOT_COUNT="DAY_NOT_COUNT"
        val TIME_BEHAVIOR_NONE="TIME_BEHAVIOR_NONE"
        var WEEK_DAYS = "DAYS"
        val TIME_PROPERTIES_STR ="TP:"
        fun Builder(closeTime:String, openTime: String, days: ArrayList<String>, gateTimeBehavior:String):TimeProperties{
            var tp  = TimeProperties(closeTime,openTime,days,gateTimeBehavior)
            println("CreateTimeProperties: $tp")
            return tp
        }
        fun getTimePropertiesListFromString( str:String):ArrayList<TimeProperties>{
            println("getTimePropertiesFromString : $str")
             var strList = str.split(TIME_PROPERTIES_STR)
            var tplist = arrayListOf<TimeProperties>()
            for(i in 0 until strList.size ){
                println("getTimePropertiesFromString current tp: ${strList[i]}")

                var tp = TimeProperties("","" , ArrayList<String>(),"")
               var tpStr = strList[i].split(" ")
                 for( j in 0 until tpStr.size step 2){
                     println("getTimePropertiesFromString current property: ${tpStr[j]}")

                     if(tpStr[j]==DataHelperGateProperties.GATE_TIME_BEHAVIOR){
                         println("getTimePropertiesFromString current succeed property: ${tpStr[j]}")

                         tp.gateTimeBehavior=tpStr[j+1]
                     }
                     if(tpStr[j]==DataHelperGateProperties.CLOSE_AT){
                         println("getTimePropertiesFromString current succeed property: ${tpStr[j]}")

                         tp.closeTime=tpStr[j+1]
                     }
                     if(tpStr[j]==DataHelperGateProperties.OPEN_AT){
                         println("getTimePropertiesFromString current succeed property: ${tpStr[j]}")

                         tp.openTime=tpStr[j+1]

                     }

                     if(tpStr[j]==TimeProperties.WEEK_DAYS){
                         var days = arrayListOf<String>()
                         for(x in j+1 until j+8){
                             days.add(tpStr[x])
                         }
                         tp.days=days
                         tplist.add(tp)
                         break;
                     }



                 }

            }
            return tplist
        }
        fun getDayByInt(day:Int):String{
            var dayStr = DAY_NOT_COUNT
            when(day){
                0-> dayStr=DataHelperGateProperties.SUNDAY
                1-> dayStr=DataHelperGateProperties.MONDAY
                2->dayStr=DataHelperGateProperties.TUESDAY
                3->dayStr=DataHelperGateProperties.WEDNESDAY
                4->dayStr=DataHelperGateProperties.THURSDAY
                5->dayStr=DataHelperGateProperties.FRIDAY
                6->dayStr=DataHelperGateProperties.SATURDAY

            }
            return dayStr

        }
    }
    var closeTimeProperties:HourProperties
    var openTimeProperties:HourProperties
    fun workWithCurrentTime():Boolean{
        closeTimeProperties= HourProperties.Builder(closeTime)
        println("Timeproperties closetime properties closetime properteis: $closeTimeProperties")
        openTimeProperties=HourProperties.Builder(openTime)
        var calendar = Calendar.getInstance()
        var day = calendar.get(Calendar.DAY_OF_WEEK)
        var hour =calendar.get(Calendar.HOUR_OF_DAY)
        var minute = calendar.get(Calendar.MINUTE)
        var sum = hour*60+minute
        println("TimeProperties: time behavior: $gateTimeBehavior")

        when(gateTimeBehavior){
            DataHelperGateProperties.CLOSE_ALL_TIME->{

                if (checkIfGateIsCloseTody(day)) return true
            }
            DataHelperGateProperties.CLOSE_AT_DAY->{
                if (gateCloseAtDayHandler(day, hour, minute)) return true
            }
            DataHelperGateProperties.CLOSE_AT_NIGHT->{
                println("TimeProperties when gateCloseAtNight")
                //check if gate is aviable only in this day
                return HandleCloseGateAtNight(day, sum)


            }
}
println("TimeProperties: workWithCurrnetTime Does not work with current time. closTime: ${closeTimeProperties }  current time : $day , $hour,$minute ")
return false

    }

    private fun HandleCloseGateAtNight(day: Int, sum: Int): Boolean {
        println("Night behavior is called")
        var day1=day
        if (closeTimeProperties.getSum() < openTimeProperties.getSum()) {
            if (days[day1 - 1] != DAY_NOT_COUNT) {
                if (sum >= closeTimeProperties.getSum() && sum < openTimeProperties.getSum()) {
                    println("Night behavior returns true")
                    return true
                }
            }
        }//check if yesterday time integrated with today's time
        else {
            if (sum >= closeTimeProperties.getSum()) {
                println("night behavior: sub is bigger then close time properties sum")
                return true
            }

            var formerDay=day1 - 1
            if (day1 == 0) {
                day1=7
            }

            if (days[formerDay - 1] != DAY_NOT_COUNT) {
                if (sum < openTimeProperties.getSum()) {
                    println("night behavior: gate is allready closed from yesterday")
                    return true
                }
            }

        }
        return false
    }

    private fun gateCloseAtDayHandler(
        day: Int,
        hour: Int,
        minute: Int
    ): Boolean {
        if (days[day - 1] != DAY_NOT_COUNT) {
            if (hour * 60 + minute >= closeTimeProperties.getSum()) {

                if (hour * 60 + minute <= openTimeProperties.getSum()) {

                    return true
                }
            }
        }
        return false
    }

    private fun checkIfGateIsCloseTody(day: Int): Boolean {
        if (days[day - 1] != DAY_NOT_COUNT) {
            println("TimeProperties: workwith current day thourh gate behavior: $gateTimeBehavior")
            return true
        }
        return false
    }

    fun getClosestTime(){
        var calendar = Calendar.getInstance()
       var day =  calendar.get(Calendar.DAY_OF_WEEK)
        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        var minute = calendar.get(Calendar.MINUTE)
        when(gateTimeBehavior){
            DataHelperGateProperties.CLOSE_ALL_TIME->{
              
            }
            DataHelperGateProperties.CLOSE_AT_DAY->{

            }
            DataHelperGateProperties.CLOSE_AT_NIGHT->{


            }
        }

    }

    fun getClosestDay( cal:Calendar):Day{
        var day= cal.get(Calendar.DAY_OF_WEEK)
        println("current day: $day" )
        println("closeTime $closeTime openTime:$openTime")
        closeTimeProperties= HourProperties.Builder(closeTime)
        openTimeProperties=HourProperties.Builder(openTime)

        var bestDay=-1
        var start =true
        if(gateTimeBehavior==DataHelperGateProperties.OPEN_ALL_TIME){
            return Day(-1,-1,-1)
        }
        days.forEachIndexed{i , dayStr->
            if(dayStr!= DAY_NOT_COUNT){
                if(start){
                    start =false
                    bestDay=i
                }
                    if(day-1 -i >=0){
                        if(day-1-i==0){
                            var dobj = Day(i+1 , closeTimeProperties.hour,closeTimeProperties.minute)
                            println("Time properties best day: ${dobj}" )
                            return dobj

                        }else{
                            if(i>bestDay){
                                bestDay=i
                            }
                        }
                    }

            }
        }
        var dobj =  Day(bestDay+1,closeTimeProperties.hour,closeTimeProperties.minute)
        println("time properties: are not same days,  best day: ${dobj}")
       return dobj
    }


    init{
    if(days.size<7){
        for(i in days.size until 7){
            days.add(DAY_NOT_COUNT)
        }
    }
        println("close time: $closeTime openTime: $openTime " )
        closeTimeProperties= HourProperties.Builder(closeTime)
        openTimeProperties=HourProperties.Builder(openTime)
}

    override fun toString(): String {
        var str="${DataHelperGateProperties.GATE_TIME_BEHAVIOR} ${gateTimeBehavior} ${DataHelperGateProperties.OPEN_AT} ${openTime} ${DataHelperGateProperties.CLOSE_AT} ${closeTime} ${WEEK_DAYS}"

        days.forEach {
            str+=" " +it
        }
        return str

    }



}