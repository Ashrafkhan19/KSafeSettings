package io.github.ashrafkhan19.ksafesettings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.anifantakis.lib.ksafe.KSafe
import io.github.ashrafkhan19.ksafesettings.compose.composeState
import io.github.ashrafkhan19.ksafesettings.compose.rememberComposeState
import androidx.lifecycle.lifecycleScope

/**
 * Demo activity showcasing all KSafeSettings features:
 * - Plain (unencrypted) settings
 * - Secure (AES-256-GCM encrypted) settings
 * - Reactive StateFlow
 * - Compose state integration
 */
class MainActivity : ComponentActivity() {

    private lateinit var settings: KSafeSettings

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Create KSafe + KSafeSettings — do this once per process, ideally via DI (Koin / Hilt)
        val ksafe = KSafe(this)
        settings = KSafeSettings(ksafe, lifecycleScope)

        setContent {
            MaterialTheme {
                Scaffold(
                    topBar = { TopAppBar(title = { Text("KSafeSettings Demo") }) },
                ) { innerPadding ->
                    KSafeSettingsDemo(
                        settings = settings,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
fun KSafeSettingsDemo(settings: KSafeSettings, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PlainSettingsCard(settings)
        SecureSettingsCard(settings)
        ReactiveSettingsCard(settings)
        ComposeStateCard(settings)
        UtilitiesCard(settings)
    }
}

// ── Plain settings ──────────────────────────────────────────────────────────

@Composable
private fun PlainSettingsCard(settings: KSafeSettings) {
    var isDarkMode by settings.bool("dark_mode", default = false)
    var fontSize by settings.int("font_size", default = 16)

    DemoCard("Plain Settings (unencrypted)") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Dark mode", modifier = Modifier.weight(1f))
            Switch(checked = isDarkMode, onCheckedChange = { isDarkMode = it })
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Font size: $fontSize", modifier = Modifier.weight(1f))
            Button(onClick = { fontSize = (fontSize + 1).coerceAtMost(32) }) { Text("+") }
            Button(onClick = { fontSize = (fontSize - 1).coerceAtLeast(10) }) { Text("−") }
        }
    }
}

// ── Secure settings ──────────────────────────────────────────────────────────

@Composable
private fun SecureSettingsCard(settings: KSafeSettings) {
    var authToken by settings.secureString("auth_token")

    DemoCard("Secure Settings (AES-256-GCM)") {
        Text("Token: ${if (authToken.isEmpty()) "(not set)" else "***${authToken.takeLast(4)}"}")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { authToken = "Bearer eyJ_${System.currentTimeMillis()}" }) {
                Text("Set token")
            }
            Button(onClick = { authToken = "" }) { Text("Clear") }
        }
    }
}

// ── Reactive (StateFlow) settings ────────────────────────────────────────────

@Composable
private fun ReactiveSettingsCard(settings: KSafeSettings) {
    val themeFlow = settings.flow("theme", "system")
    val theme by themeFlow.collectAsState()
    var themeDelegate by settings.string("theme", "system")

    DemoCard("Reactive StateFlow") {
        Text("Current theme (from flow): $theme")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("system", "light", "dark").forEach { t ->
                Button(onClick = { themeDelegate = t }) { Text(t) }
            }
        }
    }
}

// ── Compose state ────────────────────────────────────────────────────────────

@Composable
private fun ComposeStateCard(settings: KSafeSettings) {
    // rememberComposeState — persists across process death, no ViewModel needed
    var selectedTab by settings.rememberComposeState("selected_tab", 0)

    DemoCard("Compose State (rememberComposeState)") {
        Text("Last selected tab (survives kill): $selectedTab")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (0..2).forEach { i ->
                Button(onClick = { selectedTab = i }) { Text("Tab $i") }
            }
        }
    }
}

// ── Utilities ────────────────────────────────────────────────────────────────

@Composable
private fun UtilitiesCard(settings: KSafeSettings) {
    var username by settings.string("username")

    DemoCard("Utilities") {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username (persisted)") },
            modifier = Modifier.fillMaxWidth(),
        )
        Text("contains(\"username\"): ${settings.contains("username")}")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { settings.remove("username"); username = "" }) {
                Text("Remove key")
            }
            Button(onClick = { settings.clear() }) {
                Text("Clear all")
            }
        }
    }
}

@Composable
private fun DemoCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(4.dp))
            content()
        }
    }
}
