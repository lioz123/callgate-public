package com.callgate.autogater.activities


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.Layout
import android.text.TextWatcher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

import androidx.preference.PreferenceManager
import com.callgate.autogater.Helpers.GpsHelper

import com.callgate.autogater.Helpers.LanguageHelper
import com.callgate.autogater.ListAdapters.CursorContactsAdapter
import com.callgate.autogater.PropertiesObjects.GateMannager

import com.callgate.autogater.PropertiesObjects.GateProperties
import com.callgate.autogater.R
import com.callgate.autogater.activities.TimeConfiguration.TimeConfigurationStatics.TP_LIST
import com.callgate.autogater.database.DataBaseAdapter
import com.google.android.gms.maps.model.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView

class MapsActivity : AppCompatActivity(), OnMapReadyCallback ,GoogleMap.OnMyLocationButtonClickListener,GoogleMap.OnMyLocationClickListener {
    companion object TAGS{
   //     val GATE_LOCATION_TAG ="GATE_LOCATION_TAG"
     //   val GATENAME="GATENAME"
       // val GATENUMBER="GATENUMBER"
 //       var TP_LIST= ArrayList<TimeProperties>()
   var GATE_ARRAY = ArrayList<GateProperties>()
        var DELTE_GATE_LIST = ArrayList<GateProperties>()
        val NEW_GATE=-1



    }
    val onFocusedChangedCloseKeyboard = object:View.OnFocusChangeListener{
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            if(!hasFocus){
                closeKeyboard(v!!)
            }
        }
    }
    lateinit var  fusedLocationProvider: FusedLocationProviderClient
    lateinit var mNextButton:MaterialButton
    lateinit var mToolbar :MaterialToolbar
    lateinit var latLng: LatLng

    lateinit var mGateNameTextView:AutoCompleteTextView
    lateinit var mGatePhoneNumber:EditText
    var openedConfigurationPage = false
    override fun onMyLocationClick(p0: Location) {
    }

    override fun onMyLocationButtonClick(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return false
        }
        fusedLocationProvider.lastLocation.addOnSuccessListener {
            val latLng = LatLng(it.latitude,it.longitude)
            val cameraPosition = CameraPosition.Builder().target(latLng).zoom(15.5F).build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
        return true
    }


    private lateinit var mMap: GoogleMap
    var permissionList = ArrayList<String>()
     var gateName=""
    var gateNumber=""
    lateinit var gate:GateProperties
    var id=-1
    lateinit var mSeekBar: SeekBar
    lateinit var removeGateButton :ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        requestContactPermission()
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        GATE_ARRAY=ArrayList()
        id = intent.getIntExtra(MainActivity.ID,NEW_GATE)
        mGateNameTextView = findViewById(R.id.add_gate_gate_name_editText)
        InitiateGateNameAdapterSuggestions()
        mGatePhoneNumber = findViewById(R.id.add_gate_phone_number_editText)
        mNextButton = findViewById(R.id.maps_next_button)
        removeGateButton = findViewById(R.id.imageButton_remove_gate)
        mSeekBar = findViewById(R.id.map_seekbar)
        mGatePhoneNumber.setText(gateNumber)
        mGateNameTextView.setText(gateName)
        if(GpsHelper.checksEnabledGPS(this)){
            HandleIntent()
            SetFragmentMap()
            InitateSeekBarChangeListener()
            ConfigureToolbar()
        }

        sendNote()

    }

    fun sendNote(){
        var gates = DataBaseAdapter(this).getAllGates()
        if(gates.size>0){
            var card = findViewById<MaterialCardView>(R.id.close_note_card_view)

            card.visibility=View.GONE
        }
    }

    private fun ConfigureToolbar() {
        mToolbar=findViewById(R.id.toolbar)
        mToolbar.title=getString(R.string.title_activity_maps)

        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    val PROJECTION=arrayOf(
        "_id",
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )
    private fun HandleIntent() {
        if (id != NEW_GATE) {
            gate=DataBaseAdapter(applicationContext).getGate(id)
            mSeekBar.progress=gate.distance / 4
            latLng=gate.latlng
        } else {
            mSeekBar.progress=100
        }
        if (gateName != null && gateName.length > 0) {
            mGatePhoneNumber.setText(gateNumber)
            mGateNameTextView.setText(gateName)

        }
    }



    private fun InitiateGateNameAdapterSuggestions() {
        var permmisionState = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)
        if(permmisionState!=PackageManager.PERMISSION_GRANTED){
            return
        }
        mGateNameTextView.onFocusChangeListener=onFocusedChangedCloseKeyboard
        mGateNameTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                println("textChanged:${s}")
               val selection = "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?"
             val selectoinArgs = arrayOf("%${s.toString()}%")

                    val cursor=contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        PROJECTION,
                        selection,
                        selectoinArgs,
                        "${ContactsContract.Contacts.DISPLAY_NAME} LIMIT 5"


                    )


                    mGateNameTextView.post {
                        val adapter=   CursorContactsAdapter(
                            applicationContext,
                            cursor!!
                      )
                        mGateNameTextView.setAdapter(adapter

                        )
                    }




            }
        })
        mGateNameTextView.setOnItemClickListener { parent, view, position, id ->

            var nameTextView = view.findViewById<MaterialTextView>(R.id.contact_layout_name_textview)
            var phonTextView = view.findViewById<MaterialTextView>(R.id.contact_layout_phone_textview)
            closeKeyboard(currentFocus!!)


            mGateNameTextView.setText(nameTextView.text)
            mGatePhoneNumber.setText(phonTextView.text)
        }
    }

    private fun closeKeyboard(v: View) {
        var keyboard=getSystemService(InputMethodManager::class.java)
        keyboard.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun SetFragmentMap() {
        val mapFragment=supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }



    private fun InitateSeekBarChangeListener() {
        mSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                // Toast.makeText(c,"progress is : " + seekBar.progress.toString(),Toast.LENGTH_LONG).show()
                if (::gate.isInitialized) {

                    // raduiseCircle.radius=seekBar.progress*4.toDouble()
                    gate.distance=seekBar.progress * 4
                    gate.gateRaduis.radius=seekBar.progress * 4.toDouble()
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
                Toast.makeText(
                    applicationContext,
                    applicationContext.resources.getString(R.string.radius_set_to) + " " + seekBar.progress * 4 + " meters!",
                    Toast.LENGTH_LONG
                ).show()


            }
        })
    }

    fun nextButtonOnClick(v:View)=if (!mGateNameTextView.text.toString().equals("")) {
        if (!mGatePhoneNumber.text.toString().equals("")) {
            if (GATE_ARRAY.size > 0) {
                    println("TP size is " + TP_LIST.size)
                GATE_ARRAY.forEach {
                    it.gateName=mGateNameTextView.text.toString()
                    it.gateNumber=mGatePhoneNumber.text.toString()
                }
                    startActivity(Intent(applicationContext, TimeConfiguration::class.java))

            } else {
                Toast.makeText(applicationContext, R.string.click_on_map, Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.choose_phone_number,
                Toast.LENGTH_LONG
            ).show()
        }
    } else {
        Toast.makeText(applicationContext, R.string.please_choose_name, Toast.LENGTH_LONG)
            .show()
    }



    fun removeGateButtonOnClick(v:View) {
            gate.gateCircule.remove()
            gate.gateRaduis.remove()
            GATE_ARRAY.remove(gate)
            DELTE_GATE_LIST.add(gate)
            if (GATE_ARRAY.isNotEmpty()) {
                gate=GATE_ARRAY[0]
                gate.gateRaduis.fillColor=R.color.gray_tranzparent
            }

    }

    override fun onResume() {
        super.onResume()
        /*
        if(ContactListActivity.NAME!=""&&ContactListActivity.PHONE_NUMBER!=""){
            gateName=ContactListActivity.NAME
            gateNumber=ContactListActivity.PHONE_NUMBER

            println("on resumeCalled: " + gateName + " number: " + gateNumber)

            ContactListActivity.NAME=""
            ContactListActivity.PHONE_NUMBER=""
            mGatePhoneNumber.setText(gateNumber)
            mGateNameTextView.setText(gateName)
        }




        if(id!=NEW_GATE){
            var da = DataBaseAdapter(this)
            gateArray.forEach {
                it.tpList= TP_LIST
                da.updateGate(it)
            }
        }

*/
    }



    fun ConfigureTimeOnCLick(v:View){
      val intent=  Intent(this,TimeConfiguration::class.java)
        openedConfigurationPage=true

    startActivity(intent)
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if(id!=NEW_GATE){
            val da =DataBaseAdapter(this)

            GATE_ARRAY=da.getGatesByPhoneNumber(gate.gateNumber)
            TP_LIST=GATE_ARRAY.last().tpList
            VisualizeGatesOnMap()

        }

        focusCameraOnCurrentLocation()

        mapClickListener()

    }

    fun mapClickListener(){
        mMap.setOnMapClickListener {loc->

            val gatesInRange= GATE_ARRAY.filter {
                GateMannager.distance(it.latlng,loc)<(it.distance).toDouble()/1000.0
            }

            if(gatesInRange.isNotEmpty()){
                var closestGate = gatesInRange[0]
                gatesInRange.forEach {
                    if(GateMannager.distance(loc,it.latlng)<GateMannager.distance(loc,closestGate.latlng)){
                        closestGate=it
                    }
                }
                if(GateMannager.distance(loc,closestGate.latlng)<=closestGate.distance){

                    gate.gateRaduis.fillColor=R.color.colorAccentTranzparrent
                    gate =closestGate
                    gate.gateRaduis.fillColor=R.color.gray_tranzparent
                    mSeekBar.progress= (gate.distance/4.0).toInt()
                }
            }else{
                createGate(loc)

            }
        }

    }

    fun createGate(loc:LatLng){
        GATE_ARRAY.forEach {
            it.gateRaduis.fillColor=R.color.colorAccentTranzparrent
            //it.gateCircule.fillColor=R.color.common_google_signin_btn_text_dark
        }
         gate = GateProperties("test","test",loc,NEW_GATE
             ,mSeekBar.progress*4,true)
        gate.setGateVisualy(mMap)
        GATE_ARRAY.add(gate)
    }


    private fun VisualizeGatesOnMap() {
        var first=true
        GATE_ARRAY.forEach {
            if (first) {
                mGateNameTextView.setText(it.gateName)
                mGatePhoneNumber.setText(it.gateNumber)
                first=false
            }
            it.setGateVisualy(mMap)
            println(it)

            if (it.uid != id) {
                it.gateRaduis.fillColor=R.color.colorAccentTranzparrent

            } else {
                gate=it
            }
        }
    }


    private fun requestContactPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_CONTACTS
            )
        ) {
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_CONTACTS),
                MainActivity.REQUEST_READ_CONTACTS
            )
        }
    }

    fun focusCameraOnCurrentLocation() {
        if (id == NEW_GATE) {


            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            mMap.isMyLocationEnabled=true
            mMap.setOnMyLocationButtonClickListener(this);
           // mMap.setOnMyLocationClickListener(this);

            fusedLocationProvider.lastLocation.addOnSuccessListener {
                latLng = LatLng(it.latitude, it.longitude)
                val cameraPosition = CameraPosition.Builder().target(latLng).zoom(15.5F).build()
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }

        }else{
                latLng = LatLng(GATE_ARRAY[0].latlng.latitude, GATE_ARRAY[0].latlng.longitude)
                val cameraPosition = CameraPosition.Builder().target(latLng).zoom(15.5F).build()
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }


    fun AskPermmisions(){
        permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissionList.forEachIndexed {i , it ->
            if(ContextCompat.checkSelfPermission(this,it)!= PackageManager.PERMISSION_GRANTED){
                if( ActivityCompat.shouldShowRequestPermissionRationale(this,it) ){

                }else{
                    ActivityCompat.requestPermissions(this,arrayOf(it),i)
                }
            }
        }
    }
    override fun attachBaseContext(context: Context) {
        var perfs = PreferenceManager.getDefaultSharedPreferences(context)
        var language= perfs.getString("language","reply") as String
        var neoContext=context
        println("check language is : $language")
        if(language!="reply"){

            neoContext=LanguageHelper.setLanguage(language,context)
        }
        super.attachBaseContext(neoContext)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
     if(requestCode<permissions.size) {
         if (ContextCompat.checkSelfPermission(
                 this,
                 permissions[requestCode]
             ) == PackageManager.PERMISSION_GRANTED
         ) {
             mMap.isMyLocationEnabled=true
         }
     }else if(requestCode==MainActivity.REQUEST_READ_CONTACTS){
         if(ContextCompat.checkSelfPermission(applicationContext,Manifest.permission.READ_CONTACTS)==PackageManager.PERMISSION_DENIED){
           //  requestContactPermission()
  //           mBringContacts.visibility=View.GONE
         }else{
//             mBringContacts.visibility=View.VISIBLE
         }
     }



        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBackPressed() {
""
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        TP_LIST= arrayListOf()

        super.onDestroy()
    }

    fun closeNoteOnClick(view: View) {
        var note =findViewById<CardView>(R.id.close_note_card_view)
        note.visibility=View.GONE
    }
}