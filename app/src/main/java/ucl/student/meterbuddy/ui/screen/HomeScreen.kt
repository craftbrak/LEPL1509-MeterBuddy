package ucl.student.meterbuddy.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import ucl.student.meterbuddy.ui.pages.MainPage

object HomeScreen: Screen {
    @Composable
    override fun Content() {
        MainPage()
    }
}