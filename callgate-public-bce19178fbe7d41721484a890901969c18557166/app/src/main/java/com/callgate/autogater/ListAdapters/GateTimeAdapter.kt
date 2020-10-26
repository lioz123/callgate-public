package com.callgate.autogater.ListAdapters

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.callgate.autogater.PropertiesObjects.HourProperties
import com.callgate.autogater.PropertiesObjects.TimeProperties
import com.callgate.autogater.PropertiesObjects.TimeProperties.TimePropertiesStatics.DAY_NOT_COUNT
import com.callgate.autogater.R
import com.callgate.autogater.database.DataHelperGateProperties
import java.util.*
import kotlin.collections.ArrayList

class GateTimeAdapter(var c: Context, var tplist : ArrayList<TimeProperties>, var fg: FragmentManager):RecyclerView.Adapter<GateTimeAdapter.ViewHolder>() {
    var buttonpos=-1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       var context = parent.context
        var inflater = LayoutInflater.from(context)

        var v = inflater.inflate(R.layout.gate_time_layout,parent,false)
        var viewHolder = ViewHolder(v)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return tplist.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.onBind(position)


    }




   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var buttonsArray = arrayListOf<Button>()
        var title :TextView
        var radioButtonDay :RadioButton
        var radioButtonNight :RadioButton

        var radioButtonOpenAllTime:RadioButton
        var radioButtonCloseAllTime:RadioButton
        var linearLayout:LinearLayout
        var buttonCloseAt:Button
        var buttonOpenAt:Button
        var pos= -1

       init{
            buttonsArray.add(itemView.findViewById(R.id.gate_time_button_1))
            buttonsArray.add(itemView.findViewById(R.id.gate_time_button_2))
            buttonsArray.add(itemView.findViewById(R.id.gate_time_button_3))
            buttonsArray.add(itemView.findViewById(R.id.gate_time_button_4))
            buttonsArray.add(itemView.findViewById(R.id.gate_time_button_5))
            buttonsArray.add(itemView.findViewById(R.id.gate_time_button_6))
            buttonsArray.add(itemView.findViewById(R.id.gate_time_button_7))
            title =itemView.findViewById(R.id.gate_time_layout_title_textview)
            radioButtonCloseAllTime=itemView.findViewById(R.id.gate_time_layout_radio_closealltime)
            radioButtonOpenAllTime=itemView.findViewById(R.id.gate_time_layout_radio_openalltime)
            radioButtonDay=itemView.findViewById(R.id.gate_time_layout_radio_day)
            radioButtonNight=itemView.findViewById(R.id.gate_time_layout_radio_night)
            linearLayout = itemView.findViewById(R.id.LinearLayout_extanded_time_options)
            buttonCloseAt=itemView.findViewById(R.id.gate_time_layout_button_close_at)
            buttonOpenAt=itemView.findViewById(R.id.gate_time_layout_button_open_at)
        }

        fun onBind(postion:Int){
            pos=postion
            var tp = tplist[postion]
            setTitle()
            configurateDaysButtons(tp)
            configureCloseTimeButton(tp)
            configureOpenTimeButton(tp)
            handleGateTimeBehavior(tp)
            radioButtonCloseAllTimeOnCLick(tp)
            RadioButtonOpenAllTimeOnClick(tp)
            RadioButtonDayOnClick(tp)
            RadioButtonNightOnClick(tp)
            ButtonOpenAtOnClick(tp)
            ButtonCloseAtOnClick(tp)
        }

       private fun ButtonCloseAtOnClick(tp: TimeProperties) {
           buttonCloseAt.setOnClickListener {
               showTimePickerFragmenOpenTimet(
                   fg,
                   tp,
                   TimePickerFragmentHelper.CLOSE_TIME,
                   buttonCloseAt
               )

           }
       }

       private fun ButtonOpenAtOnClick(tp: TimeProperties) {
           buttonOpenAt.setOnClickListener {
               showTimePickerFragmenOpenTimet(
                   fg,
                   tp,
                   TimePickerFragmentHelper.OPEN_TIME,
                   buttonOpenAt
               )

           }
       }

       private fun ViewHolder.RadioButtonNightOnClick(
           tp: TimeProperties
       ) {
           radioButtonNight.setOnClickListener {
               //    showTimePickerFragmenOpenTimet(fg,tp,TimePickerFragmentHelper.CLOSE_TIME)
               tp.gateTimeBehavior=DataHelperGateProperties.CLOSE_AT_NIGHT
               //    linearLayout.visibility=View.VISIBLE
               if (linearLayout.visibility == View.GONE) {
                   notifyItemChanged(position)

               }

           }
       }

       private fun RadioButtonDayOnClick(tp: TimeProperties) {
           radioButtonDay.setOnClickListener {
               //    showTimePickerFragmenOpenTimet(fg,tp,TimePickerFragmentHelper.CLOSE_TIME)
               //    linearLayout.visibility=View.VISIBLE
               tp.gateTimeBehavior=DataHelperGateProperties.CLOSE_AT_DAY
               if (linearLayout.visibility == View.GONE) {
                   notifyItemChanged(position)

               }

           }
       }

       private fun ViewHolder.RadioButtonOpenAllTimeOnClick(tp: TimeProperties) {
           radioButtonOpenAllTime.setOnClickListener {

               //   linearLayout.visibility=View.GONE
               tp.gateTimeBehavior=DataHelperGateProperties.OPEN_ALL_TIME
               if (linearLayout.visibility != View.GONE) {
                   notifyItemChanged(pos)

               }

           }
       }

       private fun radioButtonCloseAllTimeOnCLick(tp: TimeProperties) {
           radioButtonCloseAllTime.setOnClickListener {
               tp.gateTimeBehavior=DataHelperGateProperties.CLOSE_ALL_TIME
               //  linearLayout.visibility=View.GONE
               if (linearLayout.visibility != View.GONE) {
                   notifySelfChanged()
               }
           }
       }

       fun handleGateTimeBehavior(tp:TimeProperties){
           when(tp.gateTimeBehavior){
               DataHelperGateProperties.OPEN_ALL_TIME ->{
                   println("GateTimeAdapter: behavior:open all time${tp.gateTimeBehavior} ")
                  radioButtonOpenAllTime.isChecked=true
                  linearLayout.visibility=View.GONE
               }
               DataHelperGateProperties.CLOSE_ALL_TIME->{
                   println("GateTimeAdapter: behavior:close all time${tp.gateTimeBehavior} ")
                radioButtonCloseAllTime.isChecked=true
                  linearLayout.visibility=View.GONE
               }
               DataHelperGateProperties.CLOSE_AT_DAY-> {
                   println("GateTimeAdapter: behavior:close at day ${tp.gateTimeBehavior} ")
                   radioButtonDay.isChecked=true
                  linearLayout.visibility=View.VISIBLE
               }
               DataHelperGateProperties.CLOSE_AT_NIGHT->{
                   println("GateTimeAdapter: behavior:close at night time${tp.gateTimeBehavior} ")
                  radioButtonNight.isChecked=true
                   linearLayout.visibility=View.VISIBLE
               }
           }
       }

       private fun configureOpenTimeButton(tp: TimeProperties) {
           var openTime=HourProperties.Builder(tp.openTime)
           setTimeButtonText(buttonOpenAt, openTime.getTimeString())
           println("openTIme String is ${buttonCloseAt.text.toString()} close time string is : ${tp.closeTime}")
       }
       
  
       private fun configureCloseTimeButton(tp: TimeProperties) {
           var closeTime=HourProperties.Builder(tp.closeTime)
           setTimeButtonText(buttonCloseAt, closeTime.getTimeString())
       }

       
       fun setTimeButtonText(button: Button,text:String){
           if(text==""){
               button.text=itemView.context.getString(R.string.click_here)
           }else{
               button.text=text
           }
       }

       fun setTitle(){
           if(pos==0){
              title.setText(R.string.week_days)
           }else if(pos==1){
              title.setText(R.string.weekend_time)

           }else{
              title.setText(R.string.other
               )

           }
       }

       private fun configurateDaysButtons(tp: TimeProperties) {
           tp.days.forEachIndexed { index, day ->
               setDayButtonColors(day, index)
               buttonsArray[index].setOnClickListener(getButtonArrayOnClickListener(tp, index))
           }
       }

        fun setDayButtonColors(day: String, index: Int) {
           if (day == TimeProperties.DAY_NOT_COUNT) {
               buttonsArray[index].setTextColor(Color.BLACK)
               buttonsArray[index].backgroundTintList=ContextCompat.getColorStateList( buttonsArray[index].context,R.color.white   )



           } else {
               buttonsArray[index].setTextColor(itemView.context.getColor(R.color.white))

               buttonsArray[index].backgroundTintList=ContextCompat.getColorStateList( buttonsArray[index].context,R.color.colorAccent)



           }
       }
       fun notifySelfChanged(){
           notifyItemChanged(pos)
       }
       private fun getButtonArrayOnClickListener(tp: TimeProperties, index: Int
       ): View.OnClickListener {
           return object : View.OnClickListener {
               override fun onClick(v: View?) {
                  if (tp.days[index] == DAY_NOT_COUNT) {
                       ChangeDayInlListToDayNotCount(index)
                      tp.days[index]=   TimeProperties.getDayByInt(index)
                      notifyDataSetChanged()
                   } else {
                      tp.days[index]=   DAY_NOT_COUNT
                        notifySelfChanged()
                   }

               }
           }
       }

       private fun ChangeDayInlListToDayNotCount(index: Int) {
           tplist.forEach {
               it.days[index] = DAY_NOT_COUNT
           }
       }
   }





       fun showTimePickerFragmenOpenTimet(fg:FragmentManager, tp: TimeProperties,tag:String,button: Button){
        TimePickerFragmentHelper(tp,button).show(fg,tag)
    }



    class TimePickerFragmentHelper(var tp:TimeProperties,var button:Button) : DialogFragment(), TimePickerDialog.OnTimeSetListener {
        companion object{
            var OPEN_TIME= "OPEN_TIME"
            var CLOSE_TIME= "CLOSE_TIME"
        }
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current time as the default values for the picker

            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            // Create a new instance of TimePickerDialog and return it
            var dialog =TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))

            return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
        }

        override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
            var minuteStr = "$minute"
            if(minute<10){
                minuteStr="0$minuteStr"

            }
            button.setText("${hourOfDay}:${minuteStr}")

            if(tag== OPEN_TIME){

                    tp.openTime="${hourOfDay}:${minute}"
            }else{
                tp.closeTime="${hourOfDay}:${minute}"


            }
        }
    }


}