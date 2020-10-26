package com.callgate.autogater.activities

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.callgate.autogater.Helpers.LanguageHelper
import com.callgate.autogater.R
import com.callgate.autogater.navigation_drawer.NavigationDrawerMannager
import com.google.android.material.appbar.MaterialToolbar

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.settings,
                SettingsFragment()
            )
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        var toolbar :MaterialToolbar = findViewById(R.id.toolbar)
        toolbar.setTitle(R.string.settings)
        setSupportActionBar(toolbar)
        NavigationDrawerMannager(this)
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                println("key is:$key")
                if(key=="language"){
                    var options =ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                    startActivity(Intent(this,SettingsActivity::class.java),options)
                }
            })
    }
    override fun attachBaseContext(context: Context) {
        var perfs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        var language= perfs.getString("language","reply") as String
        var neoContext=context
        println("check language is : " + language)
        if(language!="reply"){

            neoContext=LanguageHelper.setLanguage(language,context)
        }
        super.attachBaseContext(neoContext)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,MainActivity::class.java))

    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onPreferenceTreeClick(preference: Preference?): Boolean {
            println(
                "preference: ${preference}"
            )
            return super.onPreferenceTreeClick(preference)
        }

        override fun onResume() {
            println("resumed")
            super.onResume()
        }
    }
}