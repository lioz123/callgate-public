package com.callgate.autogater.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.callgate.autogater.PropertiesObjects.DeviceProperties
import com.callgate.autogater.PropertiesObjects.GateMannager
import com.callgate.autogater.PropertiesObjects.GateProperties
import com.callgate.autogater.PropertiesObjects.TimeProperties
import com.callgate.autogater.database.DataHelperGateProperties.TableProperties
import com.google.android.gms.maps.model.LatLng


class DataBaseAdapter{
    companion object DataAdapterTags{
        val TAG = "DataAdapterGateProperties"
        val gatePropertiescolumns = arrayOf(TableProperties.UID,TableProperties.GATE_NAME,TableProperties.PHONE_NUMBER,TableProperties.DISTANCEFROMOPENING,TableProperties.ISACTIVE,TableProperties.LATITUDE,TableProperties.LONGTITUDE,TableProperties.TIME_CONFUCATION)

        fun filter(gatelist:ArrayList<GateProperties>):ArrayList<GateProperties>{
            var closedNumbers = ArrayList<String>()
            var filteredlist = arrayListOf<GateProperties>()
            gatelist.forEach {
                if(!closedNumbers.contains(it.gateNumber)){
                    filteredlist.add(it)
                    closedNumbers.add(it.gateNumber)
                }
            }
            println("gate list is : " + gatelist)
            return filteredlist
        }

        fun getGateMannagers(
            arrayList: ArrayList<GateProperties>,
            context: Context
        ):ArrayList<GateMannager>{
            var gateArrays= HashMap<String,ArrayList<GateProperties>>()
            arrayList.forEach {gate->
               if(gateArrays.containsKey(gate.gateNumber)){
                   gateArrays[gate.gateNumber]!!.add(gate)
               }else{
                   gateArrays.put(gate.gateNumber, arrayListOf(gate))
               }
            }
            var gateMannagers = ArrayList<GateMannager>()
            gateArrays.forEach {
                gateMannagers.add(GateMannager(context,it.value))
            }
            return gateMannagers
        }

    }
     var gatesPropertiesDataHelper:DataHelperGateProperties
     var context: Context
    var bluetoothDH : DatahelperBluetooth
    constructor(context: Context){
        this.context=context
        gatesPropertiesDataHelper= DataHelperGateProperties(context)
        bluetoothDH = DatahelperBluetooth(context)
    }
    fun getGatesGroupByPhoneNumber():ArrayList<GateProperties>{
        var db = gatesPropertiesDataHelper.writableDatabase
        var cursor = db.query(true,TableProperties.TABLE_NAME,gatePropertiescolumns,null,null,"${DataHelperGateProperties.PHONE_NUMBER}",null,null,null)
        return getGatePropertiesListFromCursor(cursor)
    }
    fun getGatePropertiesListFromCursor(cursor: Cursor):ArrayList<GateProperties>{
        var gates = ArrayList<GateProperties>()
        while(cursor.moveToNext()){
            var gateName =  cursor.getString(cursor.getColumnIndex(TableProperties.GATE_NAME))
            var gatePhone =  cursor.getString(cursor.getColumnIndex(TableProperties.PHONE_NUMBER))
            var longtitude =  cursor.getDouble(cursor.getColumnIndex(TableProperties.LONGTITUDE))
            var latitude =  cursor.getDouble(cursor.getColumnIndex(TableProperties.LATITUDE))
            var uid =  cursor.getInt(cursor.getColumnIndex(TableProperties.UID))
            var distance =  cursor.getInt(cursor.getColumnIndex(TableProperties.DISTANCEFROMOPENING))
            var active = cursor.getInt(cursor.getColumnIndex(TableProperties.ISACTIVE))
            var isactive= false
            if(active==1){
                isactive=true
            }
            var tpList = TimeProperties.getTimePropertiesListFromString(cursor.getString(cursor.getColumnIndex(TableProperties.TIME_CONFUCATION)))
            var gate = GateProperties(gateName, gatePhone, LatLng(latitude, longtitude), uid, distance, isactive)
            gate.tpList=tpList
            println("tplist Str:${gate.timeconfigurationToString()}")
            gates.add(gate)

        }
        return gates
    }
    fun addGate(gateProperties: GateProperties): Long{
        var db = gatesPropertiesDataHelper.writableDatabase
        return db.insert(TableProperties.TABLE_NAME,null,getGateContentValues(gateProperties))

    }

    fun getGateContentValues(gateProperties:GateProperties):ContentValues{
        var contentValues =ContentValues()

        var active =0
        if(gateProperties.isActive){
            active=1
        }
        println("update:${gateProperties.timeconfigurationToString()}")
        contentValues.put(TableProperties.GATE_NAME,gateProperties.gateName)
        contentValues.put(TableProperties.PHONE_NUMBER,gateProperties.gateNumber.toString())
        contentValues.put(TableProperties.LATITUDE,gateProperties.latlng.latitude)
        contentValues.put(TableProperties.LONGTITUDE,gateProperties.latlng.longitude)
        contentValues.put(TableProperties.DISTANCEFROMOPENING,gateProperties.distance)
        contentValues.put(TableProperties.ISACTIVE,active)

        contentValues.put(TableProperties.TIME_CONFUCATION,gateProperties.timeconfigurationToString())
        return contentValues
    }
    fun updateGate(gateProperties: GateProperties){
        var db = gatesPropertiesDataHelper.writableDatabase
        val whereArgs = arrayOf(gateProperties.uid.toString() + "")
        var colname = TableProperties.UID
        colname+=" = ?"
         db.update(TableProperties.TABLE_NAME, getGateContentValues(gateProperties), colname, whereArgs)
            .toLong()

    }
    fun updateGateFromNumber(gateProperties: GateProperties){
        var db = gatesPropertiesDataHelper.writableDatabase
        val whereArgs = arrayOf("${gateProperties.gateNumber}")
        var colname = TableProperties.PHONE_NUMBER
        colname+=" = ?"
        db.update(TableProperties.TABLE_NAME, getGateContentValues(gateProperties), colname, whereArgs)
            .toLong()

    }

    fun getAllGates():ArrayList<GateProperties>{
        println(TAG + " getAllGates was called")
        var gates = ArrayList<GateProperties>()
        var db = gatesPropertiesDataHelper.writableDatabase
        var cursor = db.query(TableProperties.TABLE_NAME,gatePropertiescolumns,null,null,null,null,null)
        gates = getGatePropertiesListFromCursor(cursor)

        return gates

    }


    fun deleteGate(uid: Int) {
        val db = gatesPropertiesDataHelper.writableDatabase
        var colname = TableProperties.UID
        val whereArgs = arrayOf(uid.toString() + "")
        colname +=" = ?"
        db.delete(TableProperties.TABLE_NAME, colname, whereArgs)
    }

    fun getGate(id: Int): GateProperties {
        println(TAG + " getAllGates was called")
        var db = gatesPropertiesDataHelper.writableDatabase
        var whereArgs = "${DataHelperGateProperties.UID} = ?"
        var args = arrayOf("$id")
        var cursor = db.query(TableProperties.TABLE_NAME,
            gatePropertiescolumns,whereArgs,args,null,null,null,"1")
        var gates = getGatePropertiesListFromCursor(cursor)
        var defaultGate =if(gates.size==0) {
            GateProperties("defualt","def",LatLng(10.0,10.0),-1,-1,false)
        }else{
            gates[0]
        }
        return defaultGate

    }

    fun getAllBluetoothDevices():ArrayList<DeviceProperties>{
        var db = bluetoothDH.writableDatabase
        var columns = arrayOf(DatahelperBluetooth.UID,DatahelperBluetooth.DEVICE_NAME,DatahelperBluetooth.DEVICE_ADRESS)
        var cursor = db.query(DatahelperBluetooth.BLUETOOTH_TABLE_NAME, columns,null,null , null,null,null)
        var deviceArr = arrayListOf<DeviceProperties>()
        while(cursor.moveToNext()){
            var uid = cursor.getInt(cursor.getColumnIndex(DatahelperBluetooth.UID))
            var deviceName = cursor.getString(cursor.getColumnIndex(DatahelperBluetooth.DEVICE_NAME))
            var deviceAdress =cursor.getString(cursor.getColumnIndex(DatahelperBluetooth.DEVICE_ADRESS))
            deviceArr.add(DeviceProperties(uid,deviceName,deviceAdress))

        }
        return deviceArr
    }

    fun deleteBluetoothDeviceByAdress(device:DeviceProperties){
        var db = bluetoothDH.writableDatabase
        var whereArgs =DatahelperBluetooth.DEVICE_ADRESS  + " = ?"
        db.delete(DatahelperBluetooth.BLUETOOTH_TABLE_NAME,whereArgs,arrayOf("${device.adress}"))

    }
    fun deleteBluetoothDevice(device:DeviceProperties){
        var db = bluetoothDH.writableDatabase
        var whereArgs =DatahelperBluetooth.UID  + " = ?"
        db.delete(DatahelperBluetooth.BLUETOOTH_TABLE_NAME,whereArgs,arrayOf("${device.uid}"))

    }

    fun updateBluetoothDevice(device: DeviceProperties){
        var db = bluetoothDH.writableDatabase
        var contentValues = ContentValues()
        contentValues.put(DatahelperBluetooth.DEVICE_NAME,device.name)
        contentValues.put(DatahelperBluetooth.DEVICE_ADRESS,device.adress)
        var whereArgs = DatahelperBluetooth.UID + " = ?"
        db.update(DatahelperBluetooth.BLUETOOTH_TABLE_NAME, contentValues ,whereArgs,arrayOf("${device.uid}"))

    }

    fun insertBluetoothDevice(device: DeviceProperties){
        var db = bluetoothDH.writableDatabase
        var contentValues = ContentValues()
        contentValues.put(DatahelperBluetooth.DEVICE_NAME,device.name)
        contentValues.put(DatahelperBluetooth.DEVICE_ADRESS,device.adress)
        db.insert(DatahelperBluetooth.BLUETOOTH_TABLE_NAME,null,contentValues)

    }

    fun deleteGatesByNumbers(gateNumber: String) {
        val db = gatesPropertiesDataHelper.writableDatabase
        var colname = TableProperties.PHONE_NUMBER
        val whereArgs = arrayOf(  "$gateNumber")
        colname +=" = ?"
        db.delete(TableProperties.TABLE_NAME, colname, whereArgs)

    }

    fun getGatesByPhoneNumber(gateNumber :String ):ArrayList<GateProperties>{
        val db = gatesPropertiesDataHelper.writableDatabase
        var where = TableProperties.PHONE_NUMBER
        val whereArgs = arrayOf(  "$gateNumber")
        where +=" = ?"
       var c= db.query(TableProperties.TABLE_NAME,gatePropertiescolumns,where,whereArgs,null,null,null,null )

        return getGatePropertiesListFromCursor(c)
    }

    fun addGates(removedGates: ArrayList<GateProperties>) {
        var db = gatesPropertiesDataHelper.writableDatabase

            removedGates.forEach {gate->
                db.insert(TableProperties.TABLE_NAME,null,getGateContentValues(gate))

            }

    }

}