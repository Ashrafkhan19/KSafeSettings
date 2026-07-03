package io.github.ashrafkhan19.ksafesettings

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KSafeSettings",
    ) {
        App()
    }
}