package io.github.ashrafkhan19.ksafesettings

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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ashrafkhan19.ksafesettings.compose.LocalKSafeSettings
import io.github.ashrafkhan19.ksafesettings.compose.rememberComposeState

/**
 * Root composable shared across Android, iOS, Desktop, and Web.
 *
 * [KSafeSettings] is accessed via [LocalKSafeSettings] — no parameter threading
 * needed. Wrap this composable with [io.github.ashrafkhan19.ksafesettings.compose.KSafeSettingsProvider]
 * at each platform's entry point.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    MaterialTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("KSafeSettings Demo") }) },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PlainSettingsCard()
                SecureSettingsCard()
                ReactiveSettingsCard()
                ComposeStateCard()
                UtilitiesCard()
            }
        }
    }
}

// ── Plain settings ──────────────────────────────────────────────────────────

@Composable
private fun PlainSettingsCard() {
    val settings = LocalKSafeSettings.current
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
private fun SecureSettingsCard() {
    val settings = LocalKSafeSettings.current
    var authToken by settings.secureString("auth_token")

    DemoCard("Secure Settings (AES-256-GCM)") {
        Text("Token: ${if (authToken.isEmpty()) "(not set)" else "***${authToken.takeLast(4)}"}")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { authToken = "tok_live_demo1234" }) { Text("Set token") }
            Button(onClick = { authToken = "" }) { Text("Clear") }
        }
    }
}

// ── Reactive (StateFlow) settings ────────────────────────────────────────────

@Composable
private fun ReactiveSettingsCard() {
    val settings = LocalKSafeSettings.current
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

// ── Compose state ─────────────────────────────────────────────────────────────

@Composable
private fun ComposeStateCard() {
    val settings = LocalKSafeSettings.current
    // rememberComposeState — KSafe-backed state that survives process death
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
private fun UtilitiesCard() {
    val settings = LocalKSafeSettings.current
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
            Button(onClick = { settings.clear() }) { Text("Clear all") }
        }
    }
}

// ── Reusable card shell ───────────────────────────────────────────────────────

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
