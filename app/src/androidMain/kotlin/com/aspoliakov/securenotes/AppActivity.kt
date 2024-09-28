package com.aspoliakov.securenotes

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.aspoliakov.securenotes.di.AppDI
import org.koin.android.ext.koin.androidContext

/**
 * Project SecureNotes
 */

class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()

        splashScreen.setKeepOnScreenCondition { false }

        enableEdgeToEdge()

        setContent {
            AppDI(
                    appDeclaration = {
                        androidContext(this@AppActivity)
                    }
            ) {
                val darkTheme = isSystemInDarkTheme()
                DisposableEffect(darkTheme) {
                    enableEdgeToEdge(
                            statusBarStyle = SystemBarStyle.auto(
                                    Color.TRANSPARENT,
                                    Color.TRANSPARENT,
                            ) { darkTheme },
                            navigationBarStyle = SystemBarStyle.auto(
                                    lightScrim,
                                    darkScrim,
                            ) { darkTheme },
                    )
                    onDispose {}
                }
                MainAppComposable()
            }
        }
    }
}

private val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

private val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
