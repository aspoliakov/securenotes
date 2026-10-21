package com.aspoliakov.securenotes

import androidx.compose.ui.window.ComposeUIViewController
import com.aspoliakov.securenotes.di.AppDI

fun mainViewController() = ComposeUIViewController {
    AppDI(appModule) {
        MainAppComposable()
    }
}
