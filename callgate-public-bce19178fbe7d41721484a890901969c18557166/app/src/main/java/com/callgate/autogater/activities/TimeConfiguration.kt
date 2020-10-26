package com.callgate.autogater.activities

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.callgate.autogater.Helpers.LanguageHelper
import com.callgate.autogater.ListAdapters.GateTimeAdapter
import com.callgate.autogater.PropertiesObjects.GateProperties
import com.callgate.autogater.PropertiesObjects.TimeProperties
import com.callgate.autogater.R
import com.callgate.autogater.activities.MapsActivity.TAGS.DELTE_GATE_LIST
import com.callgate.autogater.activities.MapsActivity.TAGS.GATE_ARRAY
import com.callgate.autogater.activities.MapsActivity.TAGS.NEW_GATE
import com.callgate.autogater.database.DataBaseAdapter
import com.callgate.autogater.database.DataHelperGateProperties
import com.google.android.material.appbar.MaterialToolbar
import java.util.*
import kotlin.collections.ArrayList

class TimeConfiguration : AppCompatActivity() {
    companion object TimeConfigurationStatics{
        var TP_LIST = arrayListOf<TimeProperties>()
    }
    lateinit var recyclerView: RecyclerView
    lateinit var madapter: GateTimeAdapter
    lateinit var mlayoutManager: LinearLayoutManager
    lateinit var mToolabr:MaterialToolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_configuration)
        mToolabr=findViewById(R.id.toolbar)
        mToolabr.setTitle(getString(R.string.time_configuration))

        println("Time configuration was callled")
        recyclerView = findViewById(R.id.time_configuration_recycle)
        mlayoutManager=LinearLayoutManager(this)
        var tplist = ArrayList<TimeProperties>()
        if(TP_LIST.size==0){
            tplist.add(TimeProperties("","", arrayListOf(DataHelperGateProperties.SUNDAY,DataHelperGateProperties.MONDAY,DataHelperGateProperties.TUESDAY,DataHelperGateProperties.WEDNESDAY,DataHelperGateProperties.THURSDAY),TimeProperties.TIME_BEHAVIOR_NONE))
            tplist.add(TimeProperties("","", arrayListOf(TimeProperties.DAY_NOT_COUNT,TimeProperties.DAY_NOT_COUNT,TimeProperties.DAY_NOT_COUNT,TimeProperties.DAY_NOT_COUNT,TimeProperties.DAY_NOT_COUNT,DataHelperGateProperties.FRIDAY,DataHelperGateProperties.SATURDAY),TimeProperties.TIME_BEHAVIOR_NONE))
        }else{
            tplist= TP_LIST
        }

        mlayoutManager.orientation=LinearLayoutManager.VERTICAL
        madapter= GateTimeAdapter(this,tplist,supportFragmentManager)
        recyclerView.apply {
            layoutManager=mlayoutManager
            adapter=madapter
            setHasFixedSize(true)
        }
    }

    override fun attachBaseContext(context: Context) {
        var perfs = PreferenceManager.getDefaultSharedPreferences(context)
        var language= perfs.getString("language","reply") as String
        var neoContext=context
        println("check language is : " + language)
        if(language!="reply"){

            neoContext= LanguageHelper.setLanguage(language,context)
        }
        super.attachBaseContext(neoContext)
    }

    class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current time as the default values for the picker
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            // Create a new instance of TimePickerDialog and return it
            return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
        }

        override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
            // Do something with the time chosen by the user
        }
    }

    fun ConfigureTimeOnCLick(v: View){
        madapter.tplist.forEach {
            if(it.gateTimeBehavior==TimeProperties.TIME_BEHAVIOR_NONE){
                Toast.makeText(applicationContext,R.string.choose_gate_mode,Toast.LENGTH_LONG).show()
                return
            }else{
                if(it.closeTime==""&&it.gateTimeBehavior==DataHelperGateProperties.CLOSE_AT_NIGHT||it.closeTime==""&&it.gateTimeBehavior==DataHelperGateProperties.CLOSE_AT_DAY||it.openTime==""&&it.gateTimeBehavior==DataHelperGateProperties.CLOSE_AT_DAY||it.openTime==""&&it.gateTimeBehavior==DataHelperGateProperties.CLOSE_AT_NIGHT){
                    Toast.makeText(applicationContext,R.string.choose_time,Toast.LENGTH_LONG).show()
                    return
                }
            }
        }
        TP_LIST=madapter.tplist
        TP_LIST=madapter.tplist
        val dh=DataBaseAdapter(applicationContext)

        GATE_ARRAY.forEach {
            it.tpList=TP_LIST
           AddGateToDataBase(it,dh)
        }


        DELTE_GATE_LIST.forEach {
            if (it.uid != NEW_GATE) {
                dh.deleteGate(it.uid)
            }
        }
        TP_LIST.forEach {
            println("timeProperties : " +it.toString())
        }
        startActivity(Intent(this,MainActivity::class.java))
    }
    private fun AddGateToDataBase(
        gate: GateProperties,
        dh: DataBaseAdapter
    ) {
        if (gate.uid == NEW_GATE) {
            dh.addGate(gate)
        } else {
            dh.updateGate(gate)

        }
    }
}
