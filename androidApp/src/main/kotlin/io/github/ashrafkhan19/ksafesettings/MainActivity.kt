package io.github.ashrafkhan19.ksafesettings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.ashrafkhan19.ksafesettings.compose.KSafeSettingsProvider

/**
 * Android entry point for the KSafeSettings demo.
 *
 * [KSafeSettingsProvider] handles KSafe initialisation internally using
 * [LocalContext][androidx.compose.ui.platform.LocalContext] — no manual
 * KSafe or CoroutineScope wiring required.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            KSafeSettingsProvider { App() }
        }
    }
}
