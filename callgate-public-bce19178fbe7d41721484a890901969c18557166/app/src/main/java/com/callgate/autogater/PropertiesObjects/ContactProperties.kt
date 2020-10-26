package com.callgate.autogater.PropertiesObjects

class ContactProperties (var name:String, var phone:String){

    var uid =-1

    constructor(uid:Int,name:String,phone:String):this(name,phone){
        this.uid=uid
    }


    
}