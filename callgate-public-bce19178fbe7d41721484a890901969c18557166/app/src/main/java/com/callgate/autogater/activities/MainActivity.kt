package com.callgate.autogater.activities

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.callgate.autogater.BlankFragment
import com.callgate.autogater.Helpers.GpsHelper
import com.callgate.autogater.Helpers.LanguageHelper
import com.callgate.autogater.Helpers.PermmisonsHelpers.Companion.askAllPermmsions
import com.callgate.autogater.ListAdapters.GateListRecycleAdapter
import com.callgate.autogater.ListAdapters.GateListRecycleAdapterTouchHelper.Companion.createTouchAdapter
import com.callgate.autogater.PropertiesObjects.GateProperties
import com.callgate.autogater.R
import com.callgate.autogater.database.DataBaseAdapter
import com.callgate.autogater.navigation_drawer.NavigationDrawerMannager
import com.callgate.autogater.servcies.BackgroundLocationService
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() ,BlankFragment.ButtonClickListener{
   lateinit var  da :DataBaseAdapter


    lateinit var mGateListAdapter: GateListRecycleAdapter
    lateinit var mStartDrivingButton:MaterialButton
    lateinit var  mRecyclerView:RecyclerView
    var permissionList = ArrayList<String>()
    lateinit var mDrawerMannager:NavigationDrawerMannager
    companion object MainActivityCompanion{
        val ID="ID"
        val TAG = "MainActivity"

        val REQUEST_READ_CONTACTS=43
        val REQUEST_BACKGROUND_LOCATION=45


    }
private var gps_enabled = false
    private   var showStartDrivingMessage=true

    private lateinit   var mGates :ArrayList<GateProperties>


    private lateinit var mToolbar: MaterialToolbar


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        GpsHelper.checksEnabledGPS(this)
        BindPerferences()
        askAllPermmsions(this)
        ConfigureStartDrivingButton()


            da = DataBaseAdapter(applicationContext)
            mGates = da.getGatesGroupByPhoneNumber()

            initateRecycleView()


        mToolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        mToolbar.setTitle(R.string.my_gates)
        setSupportActionBar(mToolbar)

        mDrawerMannager= NavigationDrawerMannager(this)

        if(getDeviceName()!!.startsWith("Xiaomi")){
            BlankFragment().show(supportFragmentManager,"show_tag")
        }

}

    fun getDeviceName(): String? {
        val manufacturer=Build.MANUFACTURER
        val model=Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model
        }
    }


    private fun capitalize(s: String?): String {
        if (s == null || s.length == 0) {
            return ""
        }
        val first=s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first).toString() + s.substring(1)
        }
    }

    override fun onBackPressed() {
        if(!mDrawerMannager.closeDrawerIfOpen()){
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }



    fun ConfigureStartDrivingButton(){
         mStartDrivingButton=findViewById<MaterialButton>(R.id.main_activity_start_driving_service)
        if(BackgroundLocationService.isServiceRunning){
            mStartDrivingButton.backgroundTintList= ContextCompat.getColorStateList(this,R.color._red)
            mStartDrivingButton.setText(R.string.stop_Driving)
        }
    }
    private fun initateRecycleView() {
        mGateListAdapter = GateListRecycleAdapter(mGates)

        mRecyclerView=findViewById(R.id.main_activity_gatelist)
        println("days ${Calendar.SUNDAY},${Calendar.MONDAY},${Calendar.TUESDAY},${Calendar.WEDNESDAY},${Calendar.THURSDAY},${Calendar.FRIDAY},${Calendar.SATURDAY}")
        val lm=LinearLayoutManager(this)
        lm.orientation=RecyclerView.VERTICAL
            mRecyclerView.apply {
                this.adapter=mGateListAdapter
                this.layoutManager=lm
                hasFixedSize()
                val touchAdapter= createTouchAdapter(
                    applicationContext,
                    mGateListAdapter,
                    getSnackbar(),
                  getString(R.string.undo)
                )
                touchAdapter.attachToRecyclerView(mRecyclerView)
            }





    }


    fun getSnackbar():Snackbar{
        var snackbar = Snackbar.make(findViewById(R.id.main_activity_layout),getString(R.string.item_removed),Snackbar.LENGTH_LONG)
        snackbar.setActionTextColor(getColor(R.color.yellow))
        return snackbar
    }


    fun ActionBarOnClick(v: View) {
        var intent = Intent(this@MainActivity, MapsActivity::class.java)
        startActivity(intent)
    }




    fun ServiceButtonOnClick(v:View){
        if(BackgroundLocationService.isServiceRunning){
            StopService()

            var colorAnime= ObjectAnimator.ofInt(v,"backgroundColor",ContextCompat.getColor(this,R.color.darkred),ContextCompat.getColor(this,R.color.darkgreen))
            colorAnime.duration=100

            colorAnime.setEvaluator(ArgbEvaluator())
            colorAnime.start()
            mStartDrivingButton.text=getString(R.string.start_drive)
        }else{
            if(mGates.isEmpty()){
                Toast.makeText(applicationContext,"Please add gate inorder to start the app",Toast.LENGTH_LONG).show()
            }else{
                buildDialog()
            }
        }
    }





fun StartService(){
    println("Start service was called")
    var intent = Intent(this,BackgroundLocationService::class.java)

    intent.putExtra("TEST","hello")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(intent)
    }else{
        startService( intent)

    }
}
fun StopService(){
    println("Start service was called")
    var intent = Intent(this,BackgroundLocationService::class.java)
    stopService(intent)
}







fun BindPerferences(){
    var perfs = PreferenceManager.getDefaultSharedPreferences(this)
    showStartDrivingMessage=     perfs.getBoolean("startBackgroundMessage",true)


}




override fun attachBaseContext(context: Context) {
    var perfs = PreferenceManager.getDefaultSharedPreferences(context)
    var language= perfs.getString("language","reply") as String
    var neoContext=context
    println("check language is : " + language)
    if(language!="reply"){

        neoContext=LanguageHelper.setLanguage(language,context)
    }
    super.attachBaseContext(neoContext)
}

fun localeUpdateResources(context:Context,languageCode:String):Context{
    var newContext = context;

    var locale =  Locale(languageCode);
    Locale.setDefault(locale);

    var resources = context.getResources();
    var config =  Configuration(resources.getConfiguration());

    config.setLocale(locale);
    newContext = context.createConfigurationContext(config);

    return newContext;
}





fun buildDialog(){
        if(showStartDrivingMessage){
            val alertDialog: AlertDialog? =this@MainActivity.let {

                val builder = AlertDialog.Builder(ContextThemeWrapper(it,R.style.myDialog))

                builder.apply {
                    setTitle(R.string.running_in_the_background)
                    setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialog, id ->

                        StartService()
                        finishAffinity()

                        // User clicked OK button
                    })
                    setNegativeButton(R.string.cancel,
                        DialogInterface.OnClickListener { dialog, id ->
                            // User cancelled the dialog
                        })
                }
                // Set other dialog properties

                // Create the AlertDialog
                builder.create()
            }
        alertDialog?.show()
        }else{
            StartService()
            finishAffinity()

        }

}




fun OpenNetworkSettings(){
    val alertDialog: AlertDialog? = this@MainActivity?.let {

        val builder = AlertDialog.Builder(ContextThemeWrapper(it,R.style.myDialog))

        builder.apply {
            setTitle(R.string.turn_on_network)
            setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialog, id ->
                var intent =  Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                startActivity(intent);
            })

        }

        builder.create()
    }
    alertDialog?.show()
}
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onButtonClick(result: Boolean) {
        println("Device name:${getDeviceName()}")
        val intent=Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        //  val uri: Uri=Uri.fromParts("package", packageName, null)
        // intent.data=uri
        val uri=Uri.fromParts("package", packageName, null)
        intent.data=uri
        startActivityForResult(intent,5)    }
}
