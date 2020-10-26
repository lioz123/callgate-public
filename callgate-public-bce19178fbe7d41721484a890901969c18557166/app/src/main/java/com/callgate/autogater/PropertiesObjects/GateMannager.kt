package com.callgate.autogater.PropertiesObjects

import android.content.Context
import android.location.Location
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import com.callgate.autogater.servcies.BackgroundLocationService.BackgroundCompanion
import com.google.android.gms.maps.model.LatLng

class GateMannager(var c:Context,var gates:ArrayList<GateProperties>){
    companion object GateMannagerStatics{
        val TAG= "GATE_MANNAGER.CLASS"
        fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double, unit: String): Double {
            if (lat1 == lat2 && lon1 == lon2) {
                return 0.0
            } else {
                val theta = lon1 - lon2
                var dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(
                    Math.toRadians(lat1)
                ) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta))
                dist = Math.acos(dist)
                dist = Math.toDegrees(dist)
                dist = dist * 60.0 * 1.1515
                if (unit == "K") {
                    dist = dist * 1.609344
                } else if (unit == "N") {
                    dist = dist * 0.8684
                }
                return dist
            }
        }
        //lat1: Double, lon1: Double, lat2: Double, lon2: Double, unit: String
        fun distance(latlng1:LatLng, latlng2: LatLng): Double {
            var lat1 = latlng1.latitude
            var lat2 = latlng2.latitude
            var lon1 = latlng1.longitude
            var lon2 = latlng2.longitude
            var unit ="K"
            if (lat1 == lat2 && lon1 == lon2) {
                return 0.0
            } else {
                val theta = lon1 - lon2
                var dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(
                    Math.toRadians(lat1)
                ) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta))
                dist = Math.acos(dist)
                dist = Math.toDegrees(dist)
                dist = dist * 60.0 * 1.1515
                if (unit == "K") {
                    dist = dist * 1.609344
                } else if (unit == "N") {
                    dist = dist * 0.8684
                }
                return dist
            }
        }
    }
    var blocked = false
    var time = 60000L
    var interval = 1000L
    lateinit var timer :CountDownTimer
    lateinit var currentLocation: Location
    var multyGate:GateProperties
    init {
        multyGate = GateProperties(gates[0].gateName,gates[0].gateNumber,gates[0].latlng,gates[0].uid,gates[0].distance,true)
        if(gates.size>1){


            var maxx=0.0
            var maxy=0.0
            var minx=0.0
            var miny= 0.0
            var middlex= 0.0
            var middley=0.0
            var start = true
            for(i in 0 until gates.size){
                if(gates[i].isActive){
                    if(start){
                        maxx=gates[i].latlng.latitude
                        maxy=gates[i].latlng.longitude
                        minx=gates[i].latlng.latitude
                        miny= gates[i].latlng.longitude
                        start= false
                    }else{
                        if(gates[i].latlng.latitude>maxx){
                            maxx=gates[i].latlng.latitude
                        }else if(gates[i].latlng.latitude<minx){
                            minx = gates[i].latlng.latitude
                        }
                        if(gates[i].latlng.longitude>maxy){
                            maxy=gates[i].latlng.longitude
                        }else if(gates[i].latlng.longitude<miny){
                            miny = gates[i].latlng.longitude
                        }
                    }



                }
            }
            middlex=(maxx+minx)/2.0
            middley=(maxy+miny)/2.0
            var radiuse = distance(minx,miny,maxx,maxy,"K")
            multyGate.latlng= LatLng(middlex,middley)
            multyGate.distance=(radiuse*1000+100).toInt()
            println("multy block distance ${multyGate.distance}" )

        }
        println(this.toString())
        Log.d("TEST",toString())
        // Toast.makeText(c,"TOSTRING +  " + toString() ,Toast.LENGTH_LONG).show()
    }
    lateinit var thread:Thread
    fun updateLocation(location:Location){
        println("Multygate distance is : ${multyGate.distance}")
        this.currentLocation=location
        var i = 0
        if(!blocked){
            for(gate in gates){

                if(gate.isActive){
                    println("$TAG gate is active ${gate}"  )

                    var d =distance(currentLocation.latitude,currentLocation.longitude,gate.latlng.latitude,gate.latlng.longitude,"K")

                    i++

                    println("$TAG "+ i.toString() + " gate distance is " + d + " gateRaduse  : " + gate.distance)
                    if(d<gate.distance.toDouble()/1000.toDouble()){
                        println("$i call gate ")
                        println("start block")
                        blocked=true
                        if(gate.isGateAviableThroughTime()){
                            BackgroundCompanion.startCall(c,gate.gateNumber)

                        }
                        break

                    }
                }
            }
        }else{
            var d =distance(LatLng(currentLocation.latitude,currentLocation.longitude),multyGate.latlng)
            println("block: $location" )

            if(d*1000.0>multyGate.distance){
                //     Toast.makeText(c,"Remove block distance from multy gate:${(d*1000).toInt()}, while multy radiuse is ${multyGate.distance}",
                //   Toast.LENGTH_LONG).show()
                blocked = false               /*
                for(it in gates) {
                    var d =distance(location.latitude,location.longitude,it.latlng.latitude,it.latlng.longitude,"K")
                    if(d*1000.0<it.distance+20){
                        keepBlock=true
                        break
                    }
                }
                */


                if(!blocked){
                    //             Toast.makeText(c,"Remove block distance from multy gate:${(d*1000).toInt()}, while multy radiuse is ${multyGate.distance}", Toast.LENGTH_LONG).show()
                    println("block  removed")
                    println("$TAG Remove block distance from multy gate:${(d * 1000).toInt()}, while multy radiuse is ${multyGate.distance} current location ${location.latitude},${location.longitude} mulygate location: ${multyGate.latlng}" )

                }
            }


        }
        // var d =distance(currentLocation.latitude,currentLocation.longitude,gates[0].latlng.latitude,gates[0].latlng.longitude,"K")

        // Toast.makeText(c,"Distance from gate: " + d.toString() +" i is : " + i,Toast.LENGTH_LONG).show()
        /*
        if(gate.isActive){


        this.currentLocation=location


        if(!blocked){
            println("gate distance " + gate.distance.toDouble()/1000.0)
            println("distance: "+ d )

            if(d<gate.distance.toDouble()/1000.toDouble()){
                blocked=true
                BackgroundCompanion.startCall(c,gate.gateNumber)
            }
        }else{
            if(d>gate.distance.toDouble()/1000.0+0.5){

                blocked=false
            }
        }

    }

         */
    }
/*
    fun startTimer(){


        var handler = Handler(Looper.myLooper())
        timer =object :CountDownTimer(time,interval) {

            override fun onTick(millisUntilFinished: Long) {
                if(millisUntilFinished<40){
                    var mitersToPass = (60000L-millisUntilFinished)/(1000*3600)*15*1000
                    mitersToPass-=10

                    if(distance(currentLocation.latitude,currentLocation.longitude,gate.latlng.latitude,gate.latlng.longitude,"K")<mitersToPass){
                        BackgroundCompanion.startCall(c,gate.gateNumber)
                    }

                }
            }

            override fun onFinish() {
                stop()
            }
        }
        var runnable = Runnable {
            running= true
            handler.post{
                timer.start()

            }
        }

        thread =Thread(runnable)
        thread.start()




    }

    fun stop(){
        running= false;
        try {
            thread.join()

        }catch (e:IllegalThreadStateException){

        }
    }
*/

    override  fun toString():String{
        var str = "multyGate: "+ multyGate.toString()
        gates.forEach {
            str+=" Gate is:" + it.toString()

        }
        return str
    }





}