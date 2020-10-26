package com.callgate.autogater.Helpers

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.provider.Settings
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AlertDialog
import com.callgate.autogater.PropertiesObjects.DeviceProperties
import com.callgate.autogater.R

class BluetoothHelper {
    companion object{
        fun checkIfBluetoothTurnedOn():Boolean {
            var ba =  BluetoothAdapter.getDefaultAdapter()
            return ba.isEnabled
        }

        fun BuildBluetoothIsOfAlertDialog(
            activty: Activity,
            addDismissOption: Boolean
        ): AlertDialog {
            var alertDialog : AlertDialog?= activty.let{
                var builder= AlertDialog.Builder(ContextThemeWrapper(it, R.style.myDialog))
                builder.apply {
                    setTitle(activty.resources.getString(R.string.turn_on_bluetoth))
                    setPositiveButton(activty.resources.getString(R.string.ok),
                        DialogInterface.OnClickListener{ dialog, id->
                            var intent =Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                            intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
                            activty.startActivity(intent)
                        })
                    if(addDismissOption){
                        setNegativeButton(context.getString(R.string.later), DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        })
                    }
                }
                builder.create()
            }
            return alertDialog as AlertDialog
        }

        fun getBluetothDevicesFromBluetoothManager(context: Context):ArrayList<BluetoothDevice>{
            var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            var devicelist = bluetoothAdapter.bondedDevices
            var devices = arrayListOf<BluetoothDevice>()
            var deviceMannager=context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            devices.addAll(deviceMannager.getConnectedDevices(BluetoothProfile.GATT_SERVER))
            devices.addAll(devicelist)
            return devices
        }

        fun BluetoothDeviceToDeviceProperty(devicelist: ArrayList<BluetoothDevice>):ArrayList<DeviceProperties>{
            var dplist = arrayListOf<DeviceProperties>()
            devicelist.forEach {
                dplist.add(DeviceProperties(-1,it.name,it.address))
            }
            return dplist
        }
    }
}