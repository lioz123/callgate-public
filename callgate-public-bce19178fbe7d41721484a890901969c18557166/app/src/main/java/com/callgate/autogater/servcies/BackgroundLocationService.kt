package com.callgate.autogater.servcies

import android.Manifest
import android.app.*
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import com.callgate.autogater.activities.MainActivity
import com.callgate.autogater.PropertiesObjects.GateProperties
import com.callgate.autogater.database.DataBaseAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.net.Uri

import androidx.core.content.ContextCompat
import android.R
import android.bluetooth.BluetoothDevice
import android.content.*
import android.location.Location
import android.location.LocationManager
import android.os.*
import android.util.Log
import com.callgate.autogater.PropertiesObjects.GateMannager


class BackgroundLocationService : Service() {
    companion object BackgroundCompanion{
        val ACCOUNT_HANDLER_STRING ="CALLGATE_ADMIN"
        var isServiceRunning = false
        val ACTOIN_TAG="ACTION"
        var ACTION = ACTOIN_TAG
        val SEARCH_GATE_NOTIFICATION_ID=1
        val GPS_DISABLED_ID=2
        lateinit var  bluetoothDevice: BluetoothDevice

        val ACTION_STOP_DRIVING="STOP_DRIVING"

        fun startCall (c:Context,number:String) {
            synchronized(this){
                println("make call phone was called 2")
                if(ContextCompat.checkSelfPermission(c, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){

                }else{

                    val dial = "tel:" + number
                    var intent= Intent(Intent.ACTION_CALL,Uri.parse(dial))



                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                    c.startActivity(intent)
                }


            }

        }

    }
    lateinit var mGpsEnabledReciever: GpsEnabledReciver

    lateinit var mLocationManager :LocationManager
    var LOCATION_INTERVAL = 200L;
    var LOCATION_DISTANCE = 1f;
    val CHANNEL_ID = "BackgroundLocationServiceAutoGate"
    lateinit var  fusedLocationProvider: FusedLocationProviderClient
    var gates = ArrayList<GateProperties>()
    var gatesMannager = ArrayList<GateMannager>()
    val TAG="backgourndlocation"
    lateinit  var context:Context


    override fun onBind(intent: Intent): IBinder {

        TODO("Return the communication channel to the service.")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("on destroy called")
        mLocationManager.removeUpdates(mLocationListeners[0])
        mLocationManager.removeUpdates(mLocationListeners[1])
        isServiceRunning=false
        if(this::mGpsEnabledReciever.isInitialized){
            unregisterReceiver(mGpsEnabledReciever
            )

        }
        stopSelf()

    }

    override fun onCreate() {
        super.onCreate()
        RegisterGpsEnabledReciver()
        var lm = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            SearchGates()
        }else{
            AskToOpenGPS()

        }

    }

    fun AskToOpenGPS(){
        CreateNotification(getString(com.callgate.autogater.R.string.turn_on_gps),GPS_DISABLED_ID)
    }

    fun SearchGates(){

        isServiceRunning=true
        println(" foreground service was called")
        println("called service")
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        var da = DataBaseAdapter(this)
        gates = da.getAllGates()
        context=applicationContext
        CreateNotification(getString(com.callgate.autogater.R.string.searching_for_closed_gate),
            SEARCH_GATE_NOTIFICATION_ID)
        var gatesArrays = ArrayList<ArrayList<GateProperties>>()
        gates.forEach{gate2->
            //    gatesMannager.add(GateMannager(applicationContext,it))
            var foundGate = false
            for(gateslist in gatesArrays){
                for(formerGate in gateslist){
                    if(formerGate.gateNumber.equals(gate2.gateNumber)){
                        println("foremer gate number${formerGate.gateNumber} currentGateNumber:${gate2.gateNumber}")
                        gateslist.add(gate2)
                        foundGate=true
                        break
                    }
                }
                if(foundGate){
                    break
                }
            }
            if(!foundGate){
                gatesArrays.add(arrayListOf(gate2))
            }

        }
        gatesArrays.forEach {
            gatesMannager.add(GateMannager(applicationContext,it))
        }
        initializeLocationManager();

        try {
            mLocationManager.requestLocationUpdates(

                LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                mLocationListeners[0]);
        } catch (ex:java.lang.SecurityException ) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (ex:IllegalArgumentException ) {
            Log.d(TAG, "gps provider does not exist " + ex.printStackTrace());
        }









    }

    fun RegisterGpsEnabledReciver(){
        var filter= IntentFilter()
        filter.addAction("android.location.PROVIDERS_CHANGED")
            mGpsEnabledReciever=GpsEnabledReciver()
        registerReceiver(mGpsEnabledReciever,filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if(intent?.action==BluetoothReciever.BLUETOOTH_DEVICE_ACTION){
            bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) as BluetoothDevice
            //action = intent.action as String
            println("Bluetooth name " + bluetoothDevice.name)
            ACTION=intent.action as  String
        }
        return super.onStartCommand(intent, flags, startId)


    }

    fun CreateNotification(str:String,id:Int){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            var serviceChannel = NotificationChannel(CHANNEL_ID,"auto gater foreground service location",NotificationManager.IMPORTANCE_HIGH)
            var notificationManager= getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(serviceChannel)
        }
        var intent = Intent(this, MainActivity::class.java)
        var pendingIntent =PendingIntent.getActivity(this,0, intent,0)
        var stopIntent = Intent(this,CloseBackgroundServiceBroadCast::class.java)
        stopIntent.action= CloseBackgroundServiceBroadCast.CLOSE_BACKGROUND_LISTENTER_ACTION

        var pendingStop = PendingIntent.getBroadcast(this,30,stopIntent,0)
        var notification = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("CallGate")
            .setContentText(str)
            .setSmallIcon(com.callgate.autogater.R.drawable.appicon)
            //   .addAction(com.example.autogater.R.drawable.dismiss_test,"Stop")
            .addAction(R.drawable.ic_menu_search,getString(com.callgate.autogater.R.string.stop),pendingStop)

            .setContentIntent(pendingIntent).build()

        //   notificationManager.notify(0,notification)
        startForeground(1,notification)

    }





    fun askCallPhonePermmision( number:String){
        println("make call phone was called 2")
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){



        }else{
            val dial = "tel:" + number
            var intent= Intent(Intent.ACTION_CALL,Uri.parse(dial))
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

    }
    private inner class LocationListener(provider: String) : android.location.LocationListener {
        internal var mLastLocation: Location
        var mode = Mode.LongRage
        init {
            mLastLocation = Location(provider)

        }

        override fun onLocationChanged(location: Location) {
            println("${GateMannager.TAG} locatoin: ${location}")
            Log.e(TAG, "onLocationChanged: $location")
            gatesMannager.forEach{
                it.updateLocation(location)
            }
            mLastLocation.set(location)
            var da = DataBaseAdapter(applicationContext)
            var gates = da.getAllGates()
            if (gates.size > 0) {
                var closestGate = gates.get(0)
                gates.forEach {
                    if(it.isActive){
                        var k = distance(
                            location.latitude,
                            location.longitude,
                            it.latlng.latitude,
                            it.latlng.longitude,
                            "K"
                        )
                        if (k < distance(
                                location.latitude,
                                location.longitude,
                                closestGate.latlng.latitude,
                                closestGate.latlng.longitude,
                                "K"
                            )
                        ) {
                            closestGate = it

                        }
                    }
                }
                var bestdistance = distance(
                    location.latitude,
                    location.longitude,
                    closestGate.latlng.latitude,
                    closestGate.latlng.longitude,
                    "K"
                )

                if(bestdistance<3){
                    println("the distance is less  tehn 2 kilomiters")
                    if(mode!=Mode.MedumeRange){
                        mLocationManager.removeUpdates(mLocationListeners[0])
                        mLocationManager.removeUpdates(mLocationListeners[1])
                        mode = Mode.MedumeRange
                        try {
                            mLocationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                                mLocationListeners[0]);
                        } catch (ex:java.lang.SecurityException ) {
                            Log.i(TAG, "fail to request location update, ignore", ex);
                        } catch (ex:IllegalArgumentException ) {
                            Log.d(TAG, "gps provider does not exist " + ex.printStackTrace());
                        }

                    }



                }else{
                    println("the distance is hieght then 2 kilomiteers")

                    var interval = ((bestdistance.toDouble()-1.5)*20*1000).toLong()

                    mLocationManager.removeUpdates(mLocationListeners[0])

                    mLocationManager.removeUpdates(mLocationListeners[1])
                    mode = Mode.LongRage

                    try {
                        mLocationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, interval, bestdistance.toFloat()*500F,
                            mLocationListeners[0]);
                    } catch (ex:java.lang.SecurityException ) {
                        Log.i(TAG, "fail to request location update, ignore", ex);
                    } catch (ex:IllegalArgumentException ) {
                        Log.d(TAG, "gps provider does not exist " + ex.printStackTrace());
                    }


                }
                println("bestdistance is  + " + bestdistance)


            }
        }


        fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double, unit: String): Double {
            if (lat1 == lat2 && lon1 == lon2) {
                return 0.0
            } else {
                val theta = lon1 - lon2
                var dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(
                    Math.toRadians(lat1)
                ) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta))
                dist = Math.acos(dist)
                dist = Math.toDegrees(dist)
                dist = dist * 60.0 * 1.1515
                if (unit == "K") {
                    dist = dist * 1.609344
                } else if (unit == "N") {
                    dist = dist * 0.8684
                }
                return dist
            }
        }


        override fun onProviderDisabled(provider: String) {
            Log.e(TAG, "onProviderDisabled: $provider")
        }

        override fun onProviderEnabled(provider: String) {
            Log.e(TAG, "onProviderEnabled: $provider")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            Log.e(TAG, "onStatusChanged: $provider")
        }
    }




    private var mLocationListeners =  arrayOf(LocationListener(LocationManager.GPS_PROVIDER),
        LocationListener(LocationManager.NETWORK_PROVIDER))

    fun initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");

        mLocationManager = getApplicationContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

    }

    fun initializeBluetoothReciever(){
        var filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
//        registerReceiver(BluetoothReciever(),filter)
    }

     inner class GpsEnabledReciver:BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            var lm = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                SearchGates()
            }else{
                mLocationManager.removeUpdates(mLocationListeners[0])
                mLocationManager.removeUpdates(mLocationListeners[1])
                AskToOpenGPS()

            }

        }
    }




}