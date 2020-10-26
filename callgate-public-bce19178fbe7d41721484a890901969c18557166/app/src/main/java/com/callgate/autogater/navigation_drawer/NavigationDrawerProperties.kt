package com.callgate.autogater.navigation_drawer

import android.app.Activity
import android.content.Context
import android.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.callgate.autogater.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.drawer_layout.view.*

class NavigationDrawerProperties(var activity:Activity) {
var drawerLayout:DrawerLayout
    var navigationView:NavigationView
    var context:Context
    var toolbar:MaterialToolbar
    var className:String
    init {
        className = activity::class.qualifiedName!!
        context =activity.applicationContext
        var view = activity.window.decorView.rootView
        drawerLayout = view.findViewById(R.id.main_drawer_layout)
        toolbar =view.findViewById(R.id.toolbar)
        navigationView =view.findViewById(R.id.navigation_view_main)
    }
}