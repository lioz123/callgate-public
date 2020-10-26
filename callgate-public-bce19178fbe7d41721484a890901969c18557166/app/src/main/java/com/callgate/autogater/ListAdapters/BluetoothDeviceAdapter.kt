package com.callgate.autogater.ListAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callgate.autogater.PropertiesObjects.DeviceProperties
import com.callgate.autogater.R
import com.google.android.material.textview.MaterialTextView

class BluetoothDeviceAdapter(var deviceList:ArrayList<DeviceProperties>, var onClickListener: View.OnClickListener): RecyclerView.Adapter<BluetoothDeviceAdapter.BluetoothViewHolder>() {

    inner class BluetoothViewHolder(v: View): RecyclerView.ViewHolder(v) {
        var nameTextView = itemView.findViewById<MaterialTextView>(R.id.bluetooth_item_name)
        var adressTextView = itemView.findViewById<MaterialTextView>(R.id.bluetooth_item_adress)
        fun bind(position: Int){
            var bluetoothProperties = deviceList[position]
            itemView.setTag(this)
            itemView.setOnClickListener(onClickListener)
            nameTextView.text=bluetoothProperties.name
            adressTextView.text=bluetoothProperties.adress

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BluetoothViewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.bluetooth_item,parent,false)
        return BluetoothViewHolder(v)
    }

    override fun getItemCount() =  deviceList.size

    override fun onBindViewHolder(holder: BluetoothViewHolder, position: Int) {
        holder.bind(position)
    }


}