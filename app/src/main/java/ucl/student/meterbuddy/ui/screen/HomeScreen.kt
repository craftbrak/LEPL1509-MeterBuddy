package ucl.student.meterbuddy.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ucl.student.meterbuddy.ui.pages.MainPage
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel
import androidx.compose.ui.platform.LocalContext

object HomeScreen: Screen {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val screenMainPageScreenModel = rememberScreenModel { MainPageScreenModel(context) }
        MainPage(screenMainPageScreenModel)
    }
}