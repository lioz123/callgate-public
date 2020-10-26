package com.callgate.autogater.Helpers

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AlertDialog
import com.callgate.autogater.R

class GpsHelper {
    companion object{
        fun checksEnabledGPS(activity: Activity) :Boolean{
            var lm = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var gps_enabled=false

            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                if (!gps_enabled) {
                    OpenGPSSettings(activity)
                    return false
                }
            } catch (e: Exception) {
            }
            return  true
        }

        fun OpenGPSSettings(activty:Activity){
            val alertDialog: AlertDialog? = activty.let {

                val builder = AlertDialog.Builder(ContextThemeWrapper(it, R.style.myDialog))

                builder.apply {
                    setTitle(R.string.turn_on_gps)
                    setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialog, id ->
                        var intent =  Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        activty.startActivity(intent);
                    })

                }

                builder.create()
            }
            alertDialog?.show()
        }

    }
}