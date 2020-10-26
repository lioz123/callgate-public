package com.callgate.autogater.ListAdapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.callgate.autogater.Helpers.Dynamic_Links_Parsher
import com.callgate.autogater.PropertiesObjects.GateProperties
import com.callgate.autogater.PropertiesObjects.GateProperties_Presentation
import com.callgate.autogater.R
import com.callgate.autogater.activities.MainActivity
import com.callgate.autogater.activities.MapsActivity
import com.callgate.autogater.database.DataBaseAdapter
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

class GateListRecycleAdapter(    var gates :ArrayList<GateProperties>) : RecyclerView.Adapter<GateListRecycleAdapter.CustomViewHolder>() {
   lateinit var onClick:View.OnClickListener
   lateinit var onCheckListener :CompoundButton.OnCheckedChangeListener
    var removedGates = ArrayList<GateProperties>()
    init{
        initiateOnClickListener()
        initiateOnCheckListener()
    }

    private fun initiateOnCheckListener() {
        onCheckListener = object : CompoundButton.OnCheckedChangeListener {

            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                println("on checked listener wass called:${isChecked}")
                when(buttonView){
                    is Switch-> setGateActivate(buttonView, isChecked)
                    is CheckBox->     selectGate(buttonView, isChecked)
                }


            }

            private fun selectGate(buttonView: CompoundButton?, isChecked: Boolean) {
                var gate=gates[buttonView!!.id]
                gate.selected=isChecked
            }

            private fun setGateActivate(buttonView: CompoundButton?, isChecked: Boolean) {
                var c=buttonView!!.context
                var da=DataBaseAdapter(c)
                var gate=gates[buttonView.id]
                gate.checked=isChecked
                da.updateGate(gate)
            }

        }
    }

    private fun initiateOnClickListener() {
        onClick = object : View.OnClickListener {
            override fun onClick(v: View?) {
                when (v?.tag) {
                    is ImageButton -> openEditGateActivity(v.context, gates[v.id])
                }
            }


        }
    }

    fun openEditGateActivity(c:Context,gate:GateProperties){
            var intent = Intent(c, MapsActivity::class.java)
            intent.putExtra(MainActivity.ID,gate.uid)
            println("gate:${gate}")
            intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK
            c.startActivity(intent)
    }
inner class CustomViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val deleteText = itemView.findViewById<MaterialTextView>(R.id.gate_list_adapter_delte_text)
    var gateNameTextView = itemView.findViewById(R.id.gate_list_adapter_gate_name_textview) as MaterialTextView
    var gateNumberTextView = itemView.findViewById(R.id.gate_list_adapter_gatephone_textview) as MaterialTextView
    var switch = itemView.findViewById<Switch>(R.id.gate_list_adapter_switch)
    var mapButton = itemView.findViewById<ImageButton>(R.id.gate_list_adatper_openMap_imageView)
    var background = itemView.findViewById<LinearLayout>(R.id.background_layout)
    var foreground = itemView.findViewById<CardView>(R.id.foreground_layout)
    var checkBox =itemView.findViewById<CheckBox>(R.id.gate_list_adapter_checkbox)

lateinit var gate:GateProperties
    fun bindViewHolder(position: Int){

         gate = gates[position]
        when(gate.presentation){
            GateProperties_Presentation.NORMAL->   bindNormalPresentation(position)
            GateProperties_Presentation.EDIT->        bindEditPresentation(position)
        }
        gateNameTextView.text =(gate.gateName)
        gateNumberTextView.text=(gate.gateNumber)
    }

    private fun bindEditPresentation(position: Int) {
        switch.visibility=View.GONE
        mapButton.visibility=View.GONE
        checkBox.visibility=View.VISIBLE
        checkBox.tag=checkBox
        checkBox.id=position
        checkBox.isChecked=gate.selected
        checkBox.setOnCheckedChangeListener(onCheckListener)
    }

    private fun bindNormalPresentation(position: Int) {
        switch.tag=switch
        switch.id=position
        mapButton.tag=mapButton
        mapButton.id=position
        switch.setChecked(gate.isActive)

        mapButton.setOnClickListener(onClick)
        switch.setOnCheckedChangeListener(onCheckListener)
    }
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.gate_list_item,parent,false)
        return CustomViewHolder(v)
    }

    override fun getItemCount(): Int {
      return gates.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bindViewHolder(position)

    }

    fun Delete(c: Context, position: Int) {
        var dh =DataBaseAdapter(c)

        var gate= gates[position]
        removedGates=dh.getGatesByPhoneNumber(gate.gateNumber)

        dh.deleteGatesByNumbers(gate.gateNumber)
        gate.position= position
        gates.remove(gate)
        notifyItemRemoved(position)

    }

    fun restoreItem(c:Context,position: Int) {
          //  gates.add(position,removedGates.last())
            var da = DataBaseAdapter(c)
            da.addGates(removedGates)
            gates.add(position,da.getGatesByPhoneNumber(removedGates.last().gateNumber.toString()).last())
          notifyItemInserted(position)
    }

    fun   CreateShortLink(c:Context) {
        var url="https://www.callgate.com/autogater?data=?"
        var allgates = DataBaseAdapter(c).getAllGates()
        var filteredGates = getFillteredGates(allgates)
        url=Dynamic_Links_Parsher.CreateUrlData(filteredGates, url)
        println("URL after al changes ${Uri.parse(url)}")
        ShareLink(c, url, 0)

    }

    private fun ShareLink(c: Context, url: String, index: Int) {
        FirebaseDynamicLinks.getInstance().createDynamicLink().setLink(Uri.parse(url))
            .setDomainUriPrefix("https://callgate.page.link")
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
            .buildShortDynamicLink()
            .addOnSuccessListener {
                Share(it.shortLink.toString(), c)
            }
            .addOnFailureListener {
                it.printStackTrace()
                if(index>10){
                    Toast.makeText(c,c.getString(R.string.share_failed),Toast.LENGTH_LONG).show()
                    println("err: GateListAdapter: fail to share link ")
                    it.printStackTrace()
                }else{
                    ShareLink(c,url,index+1)
                }

            }
    }


    fun Share(str:String,c:Context){
        var sendIntent= Intent().apply {
            action=Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT,str)
            type="text/plain"
        }
        c.startActivity(sendIntent)
    }

    fun Save(c:Context,allSharedGates: ArrayList<GateProperties>) {
        var filteredGates =getFillteredGates(allSharedGates)
        var da = DataBaseAdapter(c)
        filteredGates.forEach {
                da.addGate(it)
        }
    }

    private fun getFillteredGates(allSharedGates: ArrayList<GateProperties>): List<GateProperties>{
        var selectedGates = gates.filter { it.selected }
        var numberList=selectedGates.map { it.gateNumber }
       return allSharedGates.filter { numberList.contains(it.gateNumber) }
    }

    fun checkAll() {
        gates.forEach {
            it.selected=true
        }
        notifyDataSetChanged()
    }

}