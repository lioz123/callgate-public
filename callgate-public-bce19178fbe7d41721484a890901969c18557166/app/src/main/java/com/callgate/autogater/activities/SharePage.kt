package com.callgate.autogater.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.callgate.autogater.Helpers.Dynamic_Links_Parsher.Companion.reciveDynamicLink
import com.callgate.autogater.Helpers.LanguageHelper
import com.callgate.autogater.ListAdapters.GateListMode
import com.callgate.autogater.ListAdapters.GateListRecycleAdapter
import com.callgate.autogater.PropertiesObjects.GateProperties
import com.callgate.autogater.PropertiesObjects.GateProperties_Presentation
import com.callgate.autogater.R
import com.callgate.autogater.database.DataBaseAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.checkbox.MaterialCheckBox

class SharePage : AppCompatActivity() {
    companion object SharePageStatics{
        var GATE_LIST_MODE = "GATE_LIST_MODE"
        var DYNAMIC_URL="DYNAMIC_URL"

    }
    lateinit var mRecycleView:RecyclerView
    lateinit var mGateListAdapter:GateListRecycleAdapter
    lateinit var mode:GateListMode
    lateinit var mMainButton: Button
    lateinit var mDataAdapter:DataBaseAdapter
    lateinit var checkAll :MaterialCheckBox
    var allSharedGates = ArrayList<GateProperties>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_page)
         mDataAdapter = DataBaseAdapter(this)
        checkAll=findViewById(R.id.share_page_checkall)
        mode=  intent.getSerializableExtra(GATE_LIST_MODE) as GateListMode
        mMainButton=findViewById(R.id.share_page_sharebutton)
       mRecycleView = findViewById(R.id.share_page_listview)
        allSharedGates = getGateProperties(mode)
        var filteredGates = getFilteredGates(mode,allSharedGates)
        InitiateRecyclView(mode,filteredGates)
        setMainButton(mode)
        var toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.share)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    }


    fun checkAllOnCLick(v:View){
        mGateListAdapter.checkAll()
    }

    fun setMainButton(mode:GateListMode ){
        when(mode){
            GateListMode.shareing->{
                mMainButton.setOnClickListener{
                    println("gate list called to create short link")
                    mGateListAdapter.CreateShortLink(this)
                }
            }
            GateListMode.reciveSharing->{
                mMainButton.setText(R.string.save)
                mMainButton.setOnClickListener{
                    mGateListAdapter.Save(this,allSharedGates)
                    startActivity(Intent(this,MainActivity::class.java))
                }

            }
        }
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


    fun InitiateRecyclView(mode:GateListMode, gates: ArrayList<GateProperties>){
        mRecycleView = findViewById(R.id.share_page_listview)
        var lm  = LinearLayoutManager(this)
        lm.orientation=RecyclerView.VERTICAL
      var filteredGates=   getFilteredGates(mode,gates)
        mGateListAdapter=GateListRecycleAdapter(filteredGates)
        mRecycleView.apply {
            this.adapter=mGateListAdapter
            this.layoutManager=lm
            hasFixedSize()
        }
    }

    fun getFilteredGates(mode:GateListMode,gates:ArrayList<GateProperties>):ArrayList<GateProperties>{
        var filteredGates = ArrayList<GateProperties>()
        if(mode==GateListMode.reciveSharing){
          filteredGates = DataBaseAdapter.filter(gates)
        }else{
            filteredGates=gates
        }
        return filteredGates
    }
    fun getGateProperties(mode:GateListMode ): ArrayList<GateProperties> {

        var gates= ArrayList<GateProperties>()
        when(mode){
            GateListMode.shareing->{
              gates=  mDataAdapter.getGatesGroupByPhoneNumber()
            }
            GateListMode.reciveSharing->{
                var url = intent.getStringExtra(DYNAMIC_URL)
              gates = reciveDynamicLink(url)
            }

        }
        gates.map {
            it.presentation=GateProperties_Presentation.EDIT
        }
        return gates
    }








}
