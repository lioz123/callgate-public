package com.callgate.autogater.Helpers

import android.net.Uri
import com.callgate.autogater.PropertiesObjects.GateProperties
import com.callgate.autogater.PropertiesObjects.TimeProperties
import com.google.android.gms.maps.model.LatLng

class Dynamic_Links_Parsher {
    companion object{
        val DISTANCE = "DISTANCE"
        val NAME = "NAME"
        val NUNBER ="NUMBER"
        val LONGTIDUE="LONGTIDUDE"
        val LATITUDE="LATIDUDE"
        val CHAR_SEPARATOR="CHAERSEPARATOR"
        val  TIME_PROPERTIES="TIME_PROPERTIES"

        fun CreateUrlData(
            allgates: List<GateProperties>,
            url: String
        ): String {
            var url1=url
            allgates.forEach { tempGate ->
                var nameStr=""
                var start=true
                var nmArr=tempGate.gateName
                nmArr.forEach {
                    if (start) {
                        start=false
                        nameStr+="${it.toInt()}"
                    } else {
                        if (it == ' ') {
                            nameStr+=CHAR_SEPARATOR
                        } else {
                            nameStr+="${CHAR_SEPARATOR}${it.toInt()}"

                        }
                    }
                }
                println("after decode " + nameStr)
                url1+="&gate&${NAME}=${nameStr}&${NUNBER}=${tempGate.gateNumber}&${LATITUDE}=${tempGate.latlng.latitude}&${LONGTIDUE}=${tempGate.latlng.longitude}&${DISTANCE}=${tempGate.distance}&${TIME_PROPERTIES}=${tempGate.timeconfigurationToString()
                    .replace(" ", "-")}"
                println("full url is: ${url1}")
                println("fater uri parse: ${Uri.parse(url1)}")
            }
            return url1
        }
        fun reciveDynamicLink( urltemp:String):ArrayList<GateProperties>{
            var url = urltemp
            var gatelist = ArrayList<GateProperties>()
            url =url.replace("https://www.callgate.com/autogater?data=?","")
            var gatearr = url.split("&gate")
            println("after split: " + gatearr)
            gatearr.forEach {
                println("gate urlscheme: $it")
                if(it!=""){
                    var parmsArr = it.split("&")
                    var name=""
                    var number=""
                    var longtitude = ""
                    var latitude =""
                    var distance =""
                    var tpList=  arrayListOf<TimeProperties>()
                    parmsArr.forEach {
                        if(it!=""){
                            var parms = it.split("=")
                            println("parms " + it)
                            println("key: ${parms[0]} value:${parms[1]}")
                            when(parms[0]){
                                DISTANCE-> distance=parms[1]
                                LATITUDE->latitude=parms[1]
                                LONGTIDUE->longtitude=parms[1]
                                NAME->name=getTranslatedName(parms, name)
                                NUNBER->number=parms[1]
                                TIME_PROPERTIES-> tpList= getConfiguredTime(parms, tpList)//change tplist
                            }
                            println("name: " +
                                    "$name number:$number latitdude:$latitude longtitdude:$longtitude distance:$distance")
                        }
                    }
                    var gate =GateProperties(name,number, LatLng(latitude.toDouble(),longtitude.toDouble()),0,distance.toInt(),true)
                    gate.tpList=tpList
                    gatelist.add(gate)
                }
            }
            return  gatelist


        }

        private fun getConfiguredTime(
            parms: List<String>,
            tpList: ArrayList<TimeProperties>):ArrayList<TimeProperties> {
            var tpList1=tpList
            var parm=parms[1].replace("-", " ")
            tpList1=TimeProperties.getTimePropertiesListFromString(parm)
            return tpList1
        }

        private fun getTranslatedName(
            parms: List<String>,
            name: String
        ): String {
            var name1=name
            var chArr=parms[1].split(CHAR_SEPARATOR)
            var namestr=""
            chArr.forEach {
                if (it == "") {
                    namestr+=" "
                } else {
                    namestr+=it.toInt().toChar()
                }
            }
            name1=namestr
            return name1
        }

    }
}