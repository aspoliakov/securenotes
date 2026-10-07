package com.aspoliakov.securenotes

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.aspoliakov.securenotes.di.AppDI
import org.koin.android.ext.koin.androidContext

/**
 * Project SecureNotes
 */

class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppDI(
                    appDeclaration = {
                        androidContext(this@AppActivity)
                    }
            ) {
                MainAppComposable()
            }
        }
    }
}
