package de.hundesportteam.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.android.AndroidEntryPoint
import de.hundesportteam.app.data.preferences.PreferencesManager
import de.hundesportteam.app.ui.navigation.AppNavigation
import de.hundesportteam.app.ui.theme.HundesportteamAppTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Immer Dark Mode verwenden
            HundesportteamAppTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(preferencesManager = preferencesManager)
                }
            }
        }
    }
}

@Composable
private fun ThemedApp(preferencesManager: PreferencesManager) {
    // Collect the flow here. The initial value will be used until the
    // actual value is read from DataStore asynchronously.
    val darkMode by preferencesManager.darkModeFlow.collectAsState(initial = false)

    HundesportteamAppTheme(darkTheme = darkMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavigation(preferencesManager = preferencesManager)
        }
    }
}

