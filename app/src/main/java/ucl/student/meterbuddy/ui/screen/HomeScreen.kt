package ucl.student.meterbuddy.ui.screen

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ucl.student.meterbuddy.ui.pages.MainPage

object HomeScreen: Screen {
    @Composable
    override fun Content() {
        MainPage()
    }
}