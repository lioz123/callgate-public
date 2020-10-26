package com.callgate.autogater.Helpers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

class PermmisonsHelpers {
    companion object{
        val ALL_PERMMISSIONS_REQUEST_CODE=1
         fun getAllPermissions(): Array<String> {
            var list= arrayListOf<String>(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SYSTEM_ALERT_WINDOW
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                list.add(Manifest.permission.MANAGE_OWN_CALLS)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                list.add(Manifest.permission.FOREGROUND_SERVICE)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                list.add( Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
            var array = arrayOfNulls<String>(list.size)
            return list.toArray(array)

        }
        fun askAllPermmsions(activity: Activity){

            ActivityCompat.requestPermissions(activity, getAllPermissions(), ALL_PERMMISSIONS_REQUEST_CODE)
        }

        fun askCallPermmision(activity: Activity){
            if(ActivityCompat.checkSelfPermission(activity,Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CALL_PHONE), ALL_PERMMISSIONS_REQUEST_CODE)
            }



        }


    }
}