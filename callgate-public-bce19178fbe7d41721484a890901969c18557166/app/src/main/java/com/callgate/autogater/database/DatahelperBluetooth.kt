package com.callgate.autogater.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.lang.Exception


class DatahelperBluetooth : SQLiteOpenHelper {
 companion object DataHelperBluetoothStatics{
     val BLUETOOTH_TABLE_NAME = "BLUEOOTH_TABLE_NAME1"
     val BLUETOOTH_DATA_BASE_NAME="BLUETOOTH_DATA_BASE_NAME1 "
     val UID = "_ID"
     val DEVICE_NAME="_DEVICE_NAME"
     val DEVICE_ADRESS="_DEVICE_ADRESS"
     val DATA_BASE_VERSION=2
     val CREATE_TABLE =  "CREATE TABLE ${BLUETOOTH_TABLE_NAME}" +
             " (${UID} INTEGER PRIMARY KEY AUTOINCREMENT, $DEVICE_NAME TEXT,$DEVICE_ADRESS TEXT);"
     val DROP_TABLE= "DROP TABLE IF EXISTS $BLUETOOTH_TABLE_NAME"
 }


    var context:Context


    constructor(context: Context) : super(context,
        BLUETOOTH_DATA_BASE_NAME,null,
       DATA_BASE_VERSION) {

        this.context=context
    }


    override fun onCreate(db: SQLiteDatabase?) {
        try {
            db!!.execSQL(CREATE_TABLE)

        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            try {
                db!!.execSQL(DROP_TABLE)
                onCreate(db)
            }catch (e:Exception){
                e.printStackTrace()
            }
    }
}