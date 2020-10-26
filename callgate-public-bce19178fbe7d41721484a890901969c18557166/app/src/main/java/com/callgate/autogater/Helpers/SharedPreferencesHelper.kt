package com.callgate.autogater.Helpers

import android.content.Context
import android.content.SharedPreferences
import com.callgate.autogater.activities.MainActivity

class SharedPreferencesHelper {
    companion object{
        val SHARED_PREFERNCES_KEY="com.callgate.autogater.activities.SHARED_PREFRENCES_KEY_v2"
        val FIRST_TIME_LOG_IN = "FIRST_TIME_LOG_IN"
        lateinit var SHARED_PREFERENSES:SharedPreferences


       fun getInstance(context: Context): SharedPreferences? {
           if(!this::SHARED_PREFERENSES.isInitialized){
               SHARED_PREFERENSES= context.getSharedPreferences(SHARED_PREFERNCES_KEY, Context.MODE_PRIVATE)
           }
            return SHARED_PREFERENSES
       }

        fun checkFirstTimeLogIn(context: Context): Boolean {
            var sharedPreferences = getInstance(context)
            return sharedPreferences!!.getBoolean(FIRST_TIME_LOG_IN,true)
        }

        fun setFirstTimeLogIn(context: Context,boolean: Boolean){
          var  pref = getInstance(context)
            pref!!.edit().apply {
                putBoolean(FIRST_TIME_LOG_IN,boolean)
                apply()
            }

        }
    }
}