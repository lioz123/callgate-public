package com.callgate.autogater.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.callgate.autogater.Helpers.BluetoothHelper.Companion.BluetoothDeviceToDeviceProperty
import com.callgate.autogater.Helpers.BluetoothHelper.Companion.BuildBluetoothIsOfAlertDialog
import com.callgate.autogater.Helpers.BluetoothHelper.Companion.checkIfBluetoothTurnedOn
import com.callgate.autogater.Helpers.BluetoothHelper.Companion.getBluetothDevicesFromBluetoothManager
import com.callgate.autogater.Helpers.SharedPreferencesHelper
import com.callgate.autogater.ListAdapters.BluetoothDeviceAdapter
import com.callgate.autogater.PropertiesObjects.DeviceProperties
import com.callgate.autogater.R
import com.callgate.autogater.database.DataBaseAdapter
import com.callgate.autogater.navigation_drawer.NavigationDrawerMannager
import com.google.android.material.appbar.MaterialToolbar

class SignBluetoothDevices : AppCompatActivity() {
    lateinit var mListViewSingedDevices : RecyclerView
    lateinit var mListViewPairedDevices:RecyclerView
    lateinit var mAlretDialog : AlertDialog
    lateinit var mRecyclerViewSignDevicesAdapter:BluetoothDeviceAdapter
    lateinit var mRecyclerViewUnSignedAdapter:BluetoothDeviceAdapter
    lateinit var mDataBaseAdapter: DataBaseAdapter
    var neeedToEnableBluetooth =false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sign_bluetooth_devices)
        mListViewPairedDevices = findViewById(R.id.bluetooth_activity_paired_devices)
        mListViewSingedDevices = findViewById(R.id.bluetooth_activity_signed_devices)
        mAlretDialog=BuildBluetoothIsOfAlertDialog(this, false)
        println("on Create SigniBletoothDevice")
        if(!checkIfBluetoothTurnedOn()){
            neeedToEnableBluetooth=true
            mAlretDialog.show()

        }
        InitiateToolbar()
        NavigationDrawerMannager(this)
        mDataBaseAdapter = DataBaseAdapter(this)
        InitiateLists()

    }


    private fun InitiateToolbar() {
        var toolbar=findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.bluetooth)
        setSupportActionBar(toolbar)
    }


    override fun onResume() {
        super.onResume()
        if(neeedToEnableBluetooth){
            if(checkIfBluetoothTurnedOn()){
                mAlretDialog.dismiss()
                InitiateLists()
                neeedToEnableBluetooth=false
            }else{
                mAlretDialog.show()
            }
        }

    }

    fun DontHaveDeviceButtonOnCLick(v:View){
        var pref = SharedPreferencesHelper.getInstance(this)
        var edit = pref!!.edit()
        edit.putBoolean(SharedPreferencesHelper.FIRST_TIME_LOG_IN,false)
        edit.commit()
        startActivity(Intent(this,MainActivity::class.java))
    }








    fun InitiateLists(){
        var signedDevicesOnCLick =onClickListenerSignedAdpater()
        var unSignedOnCLick =onClickListenerUnSignedAdapter()
        var signedDevices = mDataBaseAdapter.getAllBluetoothDevices()
        signedDevices.forEach {
            println("Device is:${it.name} adress:${it.adress}")
        }
        mRecyclerViewSignDevicesAdapter=InitiateRecyclerView(mListViewSingedDevices,signedDevices,signedDevicesOnCLick)
        mRecyclerViewUnSignedAdapter=InitiateRecyclerView(mListViewPairedDevices,getUnSignedDevices(signedDevices),unSignedOnCLick)

    }

    fun getOnClickFirstTimeLogIn() :View.OnClickListener = object:View.OnClickListener{
        override fun onClick(v: View?) {
            var vh = v!!.tag as BluetoothDeviceAdapter.BluetoothViewHolder
            var device =  mRecyclerViewUnSignedAdapter.deviceList[vh.adapterPosition]
            mDataBaseAdapter.insertBluetoothDevice(device)
            startActivity(Intent(applicationContext,MapsActivity::class.java))
        }
    }
    fun getUnSignedDevices(signedDevices: ArrayList<DeviceProperties>): ArrayList<DeviceProperties> {
      var pairedDevices=  BluetoothDeviceToDeviceProperty(getBluetothDevicesFromBluetoothManager(this))
        var unSignedDevices = FilterDevices(pairedDevices,signedDevices)
        return unSignedDevices
    }
    fun FilterDevices(pairedDevices:ArrayList<DeviceProperties>,signedDevices: ArrayList<DeviceProperties>):ArrayList<DeviceProperties>{
        var adressList = signedDevices.map { device->device.adress }
        return ArrayList(pairedDevices.filter { !adressList.contains(it.adress)})
    }
    private fun onClickListenerUnSignedAdapter(): View.OnClickListener {
        return object : View.OnClickListener {
            override fun onClick(v: View?) {
                var vh=v!!.tag as BluetoothDeviceAdapter.BluetoothViewHolder
                var position=vh.adapterPosition
                if(position==-1){
                    return
                }
                var device=mRecyclerViewUnSignedAdapter.deviceList[position]
                ReplaceItemBetweenRecyclerViews(
                    mRecyclerViewUnSignedAdapter,
                    mRecyclerViewSignDevicesAdapter,
                    position
                )

                mDataBaseAdapter.insertBluetoothDevice(device)

            }
        }
    }

    private fun onClickListenerSignedAdpater(): View.OnClickListener {
        return object : View.OnClickListener {
            override fun onClick(v: View?) {

                var vh=v!!.tag as BluetoothDeviceAdapter.BluetoothViewHolder
                var position=vh.adapterPosition
                if(position==-1){
                    return
                }
                var device=mRecyclerViewSignDevicesAdapter.deviceList[position]
                ReplaceItemBetweenRecyclerViews(mRecyclerViewSignDevicesAdapter,mRecyclerViewUnSignedAdapter,
                    position
                )
                mDataBaseAdapter.deleteBluetoothDeviceByAdress(device)


            }
        }
    }


    private fun ReplaceItemBetweenRecyclerViews(adapterA :BluetoothDeviceAdapter , adapterB:BluetoothDeviceAdapter,position: Int) {
        var device=adapterA.deviceList[position]
        adapterA.deviceList.remove(device)
        adapterA.notifyItemRemoved(position)
        adapterB.deviceList.add(device)
        adapterB.notifyItemInserted(adapterB.deviceList.size - 1)
    }


    fun InitiateRecyclerView( recyclerView: RecyclerView,devices:ArrayList<DeviceProperties>,onClickListener: View.OnClickListener): BluetoothDeviceAdapter {
        var adapter = BluetoothDeviceAdapter(devices,onClickListener)
        var lm = LinearLayoutManager(this)
        lm.orientation=RecyclerView.VERTICAL
        recyclerView.apply {
            layoutManager=lm
            this.adapter=adapter
            hasFixedSize()
        }
        return adapter
    }
}
