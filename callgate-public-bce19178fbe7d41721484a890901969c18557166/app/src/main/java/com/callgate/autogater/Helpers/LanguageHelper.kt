package com.callgate.autogater.Helpers

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import java.util.*

class LanguageHelper {
    companion object LanguageHelperStatics{
        fun setLanguage(language:String,context:Context):Context{
            println("called language helper. language: " + language)
            var neoContext = context
            var myLocale = Locale(language)
            Locale.setDefault(myLocale)
            var config = context.resources.configuration

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
                config.setLocale(myLocale);

                neoContext = neoContext.createConfigurationContext(config)

            }else{
                println("unsupperted sdk")
                config.setLocale(myLocale);
                context.resources.updateConfiguration(config, context.resources.getDisplayMetrics());
            }

            return neoContext

        }

    }

}