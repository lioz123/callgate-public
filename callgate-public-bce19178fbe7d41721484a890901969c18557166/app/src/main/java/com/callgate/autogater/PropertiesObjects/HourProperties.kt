package com.callgate.autogater.PropertiesObjects

import kotlin.math.min

class HourProperties(var time:String) {
    companion object HourPropertiesStatics{
        fun Builder(time:String):HourProperties{
           var hp= HourProperties(time)
            println("HourProperties: time: $time")
            if(time==""){
                hp.hour=-1
                hp.minute=-1

            }else{
                    var timeArr=  time.split(":")
                println("HourProperties timeArr: $timeArr")
               hp. hour = timeArr[0].toInt()
               hp. minute= timeArr[1].toInt()
                println("HourProperties: hour: ${hp.hour}")
                println("HourProperties: minute: ${hp.minute}")

            }
            return hp
        }
    }
    var hour=-1
      var minute=-1
    init{

    }
     fun getTimeString(): String {
         if(hour==-1||minute==-1){
             return ""
         }
        var minuteStr=getMinuteMinute2Digit(minute.toString())
        var timeStr="${hour}:$minuteStr"
        return timeStr
    }


     fun getMinuteMinute2Digit(
        minute: String
    ): String {

         var openMinute1=minute
        if (minute.toInt() < 10) {
            openMinute1="0$openMinute1"
        }
        return openMinute1
    }


    fun getSum():Int{
        println("Sum is ${hour * 60 + minute}")
        return hour*60+minute
    }

    override fun toString(): String {
        return "Hour: $hour , minute:$minute"
    }
}