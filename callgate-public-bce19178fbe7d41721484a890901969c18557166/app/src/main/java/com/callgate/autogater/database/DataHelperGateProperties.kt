package com.callgate.autogater.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.callgate.autogater.PropertiesObjects.GateProperties
import com.google.android.gms.maps.model.LatLng
import java.lang.Exception



 class DataHelperGateProperties  : SQLiteOpenHelper{
     companion object TableProperties{
         val DATA_BASE_NAME="DATA_GATE_COLLECTION12"
         val TABLE_NAME="DATA_TABLE_GATE_COLLECTION12"
         val UID="_id1"
         val DATA_BASE_VERSION=4
         val GATE_NAME="GATE_NAME"
         val PHONE_NUMBER="PHONE_NUMBER"
         val LATITUDE ="LATITUDE"
         var SUNDAY="SUNDAY"
         var MONDAY="MONDAY"
         var TUESDAY ="TUESDAY"
         var WEDNESDAY= "WEDNESDAY"
         var FRIDAY="FRIDAY"
         val GATE_TIME_BEHAVIOR="GATE_TIME_BEHAVIOR"
         val CLOSE_AT_NIGHT ="CLOSE_AT_NIGHT"
         val CLOSE_AT_DAY="CLOSE_AT_DAY"
         val OPEN_AT ="OPEN_AT"
         val CLOSE_AT="CLOSE_AT"
         var THURSDAY="THURSDAY"
         var SATURDAY="SATURDAY"
         val  OPEN_ALL_TIME="OPEN_ALL_TIME"
         val CLOSE_ALL_TIME="CLOSE_ALL_TIME"
         val LONGTITUDE ="LONGTITUDE"
         val ISACTIVE="ISACTIVE"
         val TIME_CONFUCATION= "TIME_CONFIGURATION"
         val DISTANCEFROMOPENING="DISTANCEFROMOPENING"
         val CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                 " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + GATE_NAME + " TEXT ," + PHONE_NUMBER + " TEXT, " + DISTANCEFROMOPENING + " INTEGER, " + ISACTIVE + " INTEGER, "  + LATITUDE + " DOUBLE, " + LONGTITUDE + " DOUBLE, "+ TIME_CONFUCATION + " TEXT);"
         val DROP_TABLE ="DROP TABLE IF EXISTS " + TABLE_NAME;
     }
    var gates =  ArrayList<GateProperties>()

      var context: Context
     constructor(context: Context) : super(context, DATA_BASE_NAME,null, DATA_BASE_VERSION) {
        this.context=context
     }

     override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
         try{
             when(oldVersion){
                 3->{
                   gates=  onUpgradeV2TableVersion()
                 }
             }
             db!!.execSQL(DROP_TABLE)
             println(DATA_BASE_NAME + " succeed to drop table")

             onCreate(db)
             println(DATA_BASE_NAME + " succeed to create table")
         }catch (e:Exception){
             println(DATA_BASE_NAME +" did not succeed to create table")
             e.printStackTrace()
         }
     }

     fun onUpgradeV2TableVersion( ):ArrayList<GateProperties>{
        var db=  writableDatabase
         var gates = ArrayList<GateProperties>()

         var columns = arrayOf(TableProperties.UID,TableProperties.GATE_NAME,TableProperties.PHONE_NUMBER,TableProperties.DISTANCEFROMOPENING,TableProperties.ISACTIVE,TableProperties.LATITUDE,TableProperties.LONGTITUDE)
         var cursor = db?.query(TableProperties.TABLE_NAME,columns,null,null,null,null,null)
         while(cursor!!.moveToNext()){
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
             gates.add(
                 GateProperties(
                     gateName,
                     gatePhone,
                     LatLng(latitude, longtitude),
                     uid,
                     distance,
                     isactive
                 )
             )

         }


        return gates
     }

     fun Upgrade3VTable(db :SQLiteDatabase , gates:ArrayList<GateProperties>){
        gates.forEach { gateProperties ->
         var contentValues = ContentValues()

         var active =0
         if(gateProperties.isActive){
             active=1
         }

         contentValues.put(TableProperties.GATE_NAME,gateProperties.gateName)
         contentValues.put(TableProperties.PHONE_NUMBER,gateProperties.gateNumber.toString())
         contentValues.put(TableProperties.LATITUDE,gateProperties.latlng.latitude)
         contentValues.put(TableProperties.LONGTITUDE,gateProperties.latlng.longitude)
         contentValues.put(TableProperties.DISTANCEFROMOPENING,gateProperties.distance)
         contentValues.put(TableProperties.ISACTIVE,active)

         val whereArgs = arrayOf(gateProperties.uid.toString() + "")


         var colname = TableProperties.UID
         colname+=" = ?"

         val id = db.update(TableProperties.TABLE_NAME, contentValues, colname, whereArgs)
             .toLong()

     }

     }

     override fun onCreate(db: SQLiteDatabase?) {
         try{

             db!!.execSQL(CREATE_TABLE)
             println(DATA_BASE_NAME + " succeed to create table")
         }catch (e:Exception){
             println(DATA_BASE_NAME +" did not succeed to create table")
             e.printStackTrace()
         }
         if(db!!.version==3){
          Upgrade3VTable(db, gates)
         }

    }


}
