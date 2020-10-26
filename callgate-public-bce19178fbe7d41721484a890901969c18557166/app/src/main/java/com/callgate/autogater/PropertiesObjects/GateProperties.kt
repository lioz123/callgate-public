package com.callgate.autogater.PropertiesObjects

import android.content.Context
import android.graphics.Color
import com.callgate.autogater.R
import com.callgate.autogater.activities.TimeConfiguration
import com.callgate.autogater.database.DataHelperGateProperties
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import java.time.DayOfWeek
import java.util.*
import kotlin.collections.ArrayList

class GateProperties(var gateName:String,var gateNumber:String,var latlng:LatLng,var uid:Int,var distance:Int, var isActive:Boolean){
    companion object GatePropertiesStatics{
        var ALWAYS = "ALLWAYS"
        var NEVER = "NEVER"
    }

    var selected=false
    var presentation = GateProperties_Presentation.NORMAL
    var position=0
    var bloocked = false
    var checked = true
  lateinit  var days :ArrayList<String>
    var tpList = ArrayList<TimeProperties>()
    init {
        /*
      days = arrayListOf(Day(DataHelperGateProperties.SUNDAY)
          ,Day(DataHelperGateProperties.MONDAY),
          Day(DataHelperGateProperties.TUESDAY)
          ,Day(DataHelperGateProperties.WEDNESDAY)
          ,Day(DataHelperGateProperties.THURSDAY)
          ,Day(DataHelperGateProperties.FRIDAY)
          ,Day(DataHelperGateProperties.SATURDAY)
          )
        */

        days = arrayListOf((DataHelperGateProperties.SUNDAY)
            ,(DataHelperGateProperties.MONDAY),
            (DataHelperGateProperties.TUESDAY)
            ,(DataHelperGateProperties.WEDNESDAY)
            ,(DataHelperGateProperties.THURSDAY)
            ,(DataHelperGateProperties.FRIDAY)
            ,(DataHelperGateProperties.SATURDAY)
        )
           var tp = TimeProperties("" , "" ,days , DataHelperGateProperties.CLOSE_ALL_TIME)


        tpList.add(tp)




    }
    lateinit var gateRaduis:Circle;
    lateinit var gateCircule:Circle
    var hasCircle=false

    fun setGateVisualy(mMap:GoogleMap){
        hasCircle=true
        gateRaduis = mMap.addCircle( CircleOptions()
            .center( latlng)
            .radius(distance.toDouble())
            .strokeColor(Color.RED)


            .fillColor(R.color.gray_tranzparent));

         gateCircule = mMap.addCircle( CircleOptions()
            .center( latlng)

            .radius(8.0)
            .strokeColor(R.color.redTranzparent)
            .fillColor(R.color.redTranzparent))
    }



    fun timeconfigurationToString():String{
        var str = ""
        var start =true
        tpList.forEach {
        if(start){
            start = false
            str += it

        }else{
            str +=TimeProperties.TIME_PROPERTIES_STR + it

        }
        }
        return str
    }

    fun isGateAviableThroughTime():Boolean{
        tpList.forEach{
            if(it.workWithCurrentTime()){
                return true
            }
        }
        return false
    }

   fun getClostTime():Day{
       var now = Calendar.getInstance()
        if(isGateAviableThroughTime()){
                return Day(now.get(Calendar.DAY_OF_WEEK),now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE))
        }
       var start =true
       var bestDay=Day(-1,-1,-1)

       tpList.forEach {
            var day = it.getClosestDay(now)
            if(day.day!=-1){
                if(start){
                    start=false
                    bestDay=day
                }else{
                    println("day is:${day.day}")
                    if(day.day!=bestDay.day){
                        println("best day is :${day.day}")
                        bestDay=getCloserDay(day,bestDay,now.get(Calendar.DAY_OF_WEEK))
                    }else if(day.day==bestDay.day){
                        if(day.hour*60+day.minute<bestDay.hour*60+bestDay.minute){
                            bestDay=day
                        }
                    }
                }
            }

        }
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

override fun toString():String{
    return "uid: " + uid + " gateName: " + gateName + " gateNumber" + gateNumber + " lating: " + latlng.toString()
}



}