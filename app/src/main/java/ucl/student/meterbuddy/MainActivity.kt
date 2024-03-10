package ucl.student.meterbuddy

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cafe.adriel.voyager.navigator.Navigator
import ucl.student.meterbuddy.ui.screen.HomeScreen
import ucl.student.meterbuddy.ui.theme.MeterBuddyTheme

class MainActivity : ComponentActivity() {

    private val localScreenContext = compositionLocalOf<Context> { error("No Context provided") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
        }
        setContent {
            MeterBuddyTheme {
                CompositionLocalProvider(localScreenContext provides this) {
                    HomeScreen.Content()
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigator (HomeScreen)
                }
            }
        }
    }
}