package com.callgate.autogater.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.view.View
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.callgate.autogater.Helpers.LanguageHelper
import com.callgate.autogater.R

import com.google.android.gms.maps.model.LatLng


class addGate : AppCompatActivity() {
lateinit var latLng: LatLng
    lateinit var addButton:Button
    lateinit var gateName : TextView
    lateinit var gateNumber:TextView
    lateinit var mBringContacts:TextView
    companion object addGateStatics{
        val GATENAME="GATENAME"
        val GATENUMBER="GATENUMBER"
    }
    override fun attachBaseContext(context: Context) {
        var perfs = PreferenceManager.getDefaultSharedPreferences(context)
        var language= perfs.getString("language","reply") as String
        var neoContext=context
        println("check language is : " + language)
        if(language!="reply"){

            neoContext=LanguageHelper.setLanguage(language,context)
        }
        super.attachBaseContext(neoContext)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_gate)
        addButton = findViewById(R.id.maps_next_button)
        gateName = findViewById(R.id.add_gate_gate_name_editText)
        gateNumber=findViewById(R.id.add_gate_phone_number_editText)
        addButton.setOnClickListener(View.OnClickListener {
            if(!gateName.text.toString().equals("")){
                if(!gateNumber.text.toString().equals("")){

                    var intent = Intent(this@addGate,MapsActivity::class.java)
                    intent.putExtra(GATENAME,gateName.text.toString())
                    intent.putExtra(GATENUMBER,gateNumber.text.toString())

                    startActivity(intent)
                }else{
                    Toast.makeText(this@addGate, "gate number is empty " , Toast.LENGTH_LONG).show()

                }
            }else{
                Toast.makeText(this@addGate, "gate name is empty " , Toast.LENGTH_LONG).show()
            }

        })

    //    mBringContacts = findViewById(R.id.add_gate_open_contacts_textView)
        /*
        mBringContacts.setOnClickListener {
            var intent=  Intent(this@addGate,ContactListActivity::class.java)
            mBringContacts.setTextColor(Color.CYAN)

            startActivity(intent)
        }
        mBringContacts.setTextColor(Color.BLUE);
        mBringContacts.setPaintFlags(mBringContacts.getPaintFlags() or   Paint.UNDERLINE_TEXT_FLAG);

         */
    }
}
