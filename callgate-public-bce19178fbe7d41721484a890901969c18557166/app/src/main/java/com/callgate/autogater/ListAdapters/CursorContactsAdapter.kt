package com.callgate.autogater.ListAdapters

import android.content.Context
import android.database.Cursor
import android.opengl.Visibility
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.CursorAdapter
import android.widget.EditText
import com.callgate.autogater.R
import com.google.android.material.textview.MaterialTextView

class CursorContactsAdapter(context:Context,cursor: Cursor) : CursorAdapter(context,cursor,1) {
init {
    println("init cursor")
    while (cursor.moveToNext()){
        var name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

        var number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
        println("name is :${name} number is:${number}")
    }
    cursor.moveToFirst()

}
    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        println("new view")
        var v= LayoutInflater.from(context).inflate(R.layout.contact_layout,parent,false)
        return v
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        println("bindView")
        if(cursor==null||context==null||view==null){
            return
        }

        var name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

        var number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
        println("name is :${name} number is:${number}")

        var nameTextView= view.findViewById<MaterialTextView>(R.id.contact_layout_name_textview)
        var numberTextView = view.findViewById<MaterialTextView>(R.id.contact_layout_phone_textview)
        nameTextView.text=name
        if(number!=""){
            numberTextView.text=number

        }else{
            numberTextView.visibility=View.GONE
        }


    }

}