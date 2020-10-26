package com.callgate.autogater.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.callgate.autogater.ListAdapters.GateListMode
import com.callgate.autogater.PropertiesObjects.GateProperties
import com.callgate.autogater.R
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

class DynamicLinksReciver : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynmaic_links_reciver)
        var gatelist = ArrayList<GateProperties>()


        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener {
            if(it!=null){
                var url =  it.link.toString()
                var shareinent = Intent(this,SharePage::class.java)
                shareinent.putExtra(SharePage.GATE_LIST_MODE,GateListMode.reciveSharing)
                shareinent.putExtra(SharePage.DYNAMIC_URL,url)
                    //        startActivity(intent)
                println("recived link")
                startActivity(shareinent)


        }
        }.addOnFailureListener{

            println("Err: DynamicLinksReciver: Unbale to recive dynamic link!")
            startActivity(Intent(this,MainActivity::class.java))
        }

    }

}
