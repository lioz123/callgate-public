package com.callgate.autogater.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.callgate.autogater.Helpers.SharedPreferencesHelper
import com.callgate.autogater.R

class startApp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_app)

        if(SharedPreferencesHelper.checkFirstTimeLogIn(this)){
           startActivity(Intent(this,SignBluetoothDeviceFirstTime::class.java))
        }else {
            println("not first time")
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
}
