package com.callgate.autogater.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.callgate.autogater.Helpers.BluetoothHelper
import com.callgate.autogater.Helpers.PermmisonsHelpers.Companion.askAllPermmsions
import com.callgate.autogater.Helpers.SharedPreferencesHelper
import com.callgate.autogater.ListAdapters.BluetoothDeviceAdapter
import com.callgate.autogater.R
import com.callgate.autogater.database.DataBaseAdapter

class SignBluetoothDeviceFirstTime : AppCompatActivity() {
    lateinit var mRecyclerView:RecyclerView
    lateinit var mBluetoothDeviceAdapter: BluetoothDeviceAdapter
    var allPermissionConfirmed =false
    var userClicked = false
    var needToEnableBluetooth= false
    lateinit var mAlertDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_bluetooth_device_first_time)
        if(!BluetoothHelper.checkIfBluetoothTurnedOn()){
         mAlertDialog=   BluetoothHelper.BuildBluetoothIsOfAlertDialog(this,true)
            needToEnableBluetooth= true
            mAlertDialog.show()

        }

        askAllPermmsions(this)
        InitiateReycleView()
    }

    private fun InitiateReycleView() {
        mRecyclerView=findViewById(R.id.sign_first_time_recycler_view)
        var deviceList=BluetoothHelper.BluetoothDeviceToDeviceProperty(
            BluetoothHelper.getBluetothDevicesFromBluetoothManager(this)
        )
        var onClick=getOnClickListener()
        mBluetoothDeviceAdapter=BluetoothDeviceAdapter(deviceList, onClick)
        var lm=LinearLayoutManager(this)
        mRecyclerView.apply {
            adapter=mBluetoothDeviceAdapter
            layoutManager=lm
            hasFixedSize()
        }
    }


    private fun getOnClickListener(): View.OnClickListener {
        return object : View.OnClickListener {
            override fun onClick(v: View?) {
                userClicked=true
                var vh=v!!.tag as BluetoothDeviceAdapter.BluetoothViewHolder
                var device=mBluetoothDeviceAdapter.deviceList[vh.adapterPosition]
                var dataBaseAdapter=DataBaseAdapter(applicationContext)

                dataBaseAdapter.insertBluetoothDevice(device)
                if(allPermissionConfirmed){
                    SharedPreferencesHelper.setFirstTimeLogIn(applicationContext,false)
                    startActivity(Intent(applicationContext, MapsActivity::class.java))
                }else{
                    askAllPermmsions(this@SignBluetoothDeviceFirstTime)
                }

            }
        }
    }

    fun DontHaveBluetoothClick(v:View){
        userClicked=true
        if(allPermissionConfirmed){
            SharedPreferencesHelper.setFirstTimeLogIn(this,false)
            startActivity(Intent(this,MapsActivity::class.java))
        }else{
                askAllPermmsions(this)
            }


    }






    override fun onResume() {
        super.onResume()
        if(needToEnableBluetooth){
            if(BluetoothHelper.checkIfBluetoothTurnedOn()){
                InitiateReycleView()
                mAlertDialog.dismiss()
                needToEnableBluetooth=false
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//

        permissions.forEach {
            if(ContextCompat.checkSelfPermission(this,it)==PackageManager.PERMISSION_DENIED){
                allPermissionConfirmed=false
                return
             }
                if(userClicked){
                    SharedPreferencesHelper.setFirstTimeLogIn(this,false)
                    startActivity(Intent(this,MapsActivity::class.java))
                }
               allPermissionConfirmed=true

            /*
            if(ContextCompat.checkSelfPermission(this,permissionList.get(requestCode))== PackageManager.PERMISSION_GRANTED){
                println("asking the current request code "+ requestCode)
                if(permissionList.get(requestCode).equals(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)){

                }
                if(requestCode==2){
                    println("call for call phone again")
                }
                AskPermmisions()


            }
  */
        }



        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun askCallPhonePermmision( number:String){
        println("make call phone was called 2")
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            println("premmision is not granted")
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CALL_PHONE),2)
            println("request permmision")

        }else{
            //  println("start dial")
            //    val dial = "tel:" + number
            //  startActivity(Intent(Intent.ACTION_CALL,Uri.parse(dial)))
        }

    }
    private fun requestContactPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.READ_CONTACTS
            )
        ) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.READ_CONTACTS),
                MainActivity.REQUEST_READ_CONTACTS
            )
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.READ_CONTACTS
            )
        ) {
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.READ_CONTACTS),
                MainActivity.REQUEST_READ_CONTACTS
            )
        }
    }

    private fun requestBackgroundLocationPermission() {
        if(ContextCompat.checkSelfPermission(applicationContext,android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)== PackageManager.PERMISSION_GRANTED){
            println("there is permmision to acess background location")
        }else{
            println("there is no permmision")
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                MainActivity.REQUEST_BACKGROUND_LOCATION
            )
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                MainActivity.REQUEST_BACKGROUND_LOCATION
            )
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                MainActivity.REQUEST_BACKGROUND_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                MainActivity.REQUEST_BACKGROUND_LOCATION
            )
        }
    }

    fun askLocationPermmisions(){
        val permissionAccessCoarseLocationApproved = ActivityCompat
            .checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

        if (permissionAccessCoarseLocationApproved) {
            val backgroundLocationPermissionApproved = ActivityCompat
                .checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED

            if (backgroundLocationPermissionApproved) {
                // App can access location both in the foreground and in the background.
                // Start your service that doesn't have a foreground service type
                // defined.
            } else {
                // App can only access location in the foreground. Display a dialog
                // warning the user that your app must have all-the-time access to
                // location in order to function properly. Then, request background
                // location.
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    160
                )
            }
        } else {
            // App doesn't have access to the device's location at all. Make full request
            // for permission.
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                161
            )
        }
    }




}
