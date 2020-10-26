package com.callgate.autogater.navigation_drawer

import android.app.Activity
import android.content.Intent
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.callgate.autogater.ListAdapters.GateListMode
import com.callgate.autogater.R
import com.callgate.autogater.activities.MainActivity
import com.callgate.autogater.activities.SettingsActivity
import com.callgate.autogater.activities.SharePage
import com.callgate.autogater.activities.SignBluetoothDevices
import com.google.android.material.navigation.NavigationView

class NavigationDrawerMannager(var activity: AppCompatActivity) :
    NavigationView.OnNavigationItemSelectedListener {
    var navigationDrawerProperties:NavigationDrawerProperties
    init {
        navigationDrawerProperties= NavigationDrawerProperties(activity)
        var toogle = ActionBarDrawerToggle(activity,navigationDrawerProperties.drawerLayout,navigationDrawerProperties.toolbar,
            R.string.nav_app_bar_open_drawer_description,
            R.string.close_drawer
        )
        navigationDrawerProperties.drawerLayout.addDrawerListener(toogle)
        navigationDrawerProperties.navigationView.setNavigationItemSelectedListener(this)
        navigationDrawerProperties.navigationView.bringToFront()
        navigationDrawerProperties.navigationView.setCheckedItem(getItemIdFromClassName(navigationDrawerProperties.className))
        toogle.syncState()

    }

    fun closeDrawerIfOpen():Boolean{
        if(navigationDrawerProperties.drawerLayout.isDrawerOpen(GravityCompat.START)){
            navigationDrawerProperties.drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }
        return false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        println("navigation Item selected")
        var activityToOpen = getClassNameFromSelectedItem(item)
        if(activityToOpen!=""&&activityToOpen!=navigationDrawerProperties.className){
            var intent = Intent(activity, Class.forName(activityToOpen))
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(SharePage.GATE_LIST_MODE, GateListMode.shareing)

            activity.startActivity(intent)
        }else{
            navigationDrawerProperties.drawerLayout.closeDrawer(GravityCompat.START)
        }

        return true
    }
    fun getClassNameFromSelectedItem(item:MenuItem):String{
        when(item.itemId){
            R.id.drawer_menu_main_activity-> return MainActivity::class.qualifiedName!!
            R.id.drawer_menu_bluetooth_acitivty-> return SignBluetoothDevices::class.qualifiedName!!
            R.id.drawer_menu_share_activity-> return SharePage::class.qualifiedName!!
            R.id.settings-> return SettingsActivity::class.qualifiedName!!

        }
        return ""
    }
    fun getItemIdFromClassName(className:String):Int{
        println("classs name :${className}")
        when(className){
            MainActivity::class.qualifiedName-> return R.id.drawer_menu_main_activity
            SignBluetoothDevices::class.qualifiedName!!  -> return   R.id.drawer_menu_bluetooth_acitivty
           SharePage::class.qualifiedName   -> return R.id.drawer_menu_share_activity
            SettingsActivity::class.qualifiedName!!  -> return R.id.settings

        }
        return -1
    }

}