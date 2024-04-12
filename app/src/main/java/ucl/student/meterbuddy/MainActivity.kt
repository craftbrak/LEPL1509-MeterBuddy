package ucl.student.meterbuddy

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import ucl.student.meterbuddy.data.utils.Resource
import ucl.student.meterbuddy.ui.component.MetersListTab
import ucl.student.meterbuddy.ui.screen.HomeScreen
import ucl.student.meterbuddy.ui.screen.LineChartsScreen
import ucl.student.meterbuddy.ui.theme.MeterBuddyTheme
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val localScreenContext = compositionLocalOf<Context> { error("No Context provided") }
    private val mainPageScreenModel: MainPageScreenModel by viewModels<MainPageScreenModel>()
    //    private lateinit var analytics: FirebaseAnalytics
    //TODO: Make a compose Navigation graph , nest current app in it ,
    // set mainpageScreenModel.state.value.currentUser to bw a flow, listen to the flow and navigate based on it

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {}
        val user = FirebaseAuth.getInstance().currentUser

        when(mainPageScreenModel.state.value.currentUser.value){
            is Resource.Error -> {
                val intent= Intent(this, AuthActivity::class.java)
                startActivity(intent)
            }
            is Resource.Loading -> {
                //TODO: Display Loading screen

            }
            is Resource.Success -> {

            }
        }
        setContent {
            LaunchedEffect(key1 = Unit) {
                mainPageScreenModel.state.value.currentUser.collect{
                    when(it){
                        is Resource.Error -> TODO()
                        is Resource.Loading -> TODO()
                        is Resource.Success -> TODO()
                    }
                }
            }
            MeterBuddyTheme {
                CompositionLocalProvider(localScreenContext provides this) {
                    HomeScreen.Content()
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TabNavigator(tab = MetersListTab) {
                        Scaffold(
                            content = {
                                it != null
                                CurrentTab()
                            },
                            bottomBar = {
                                BottomAppBar {
                                    TabNavigationItem(tab = MetersListTab)
                                    TabNavigationItem(tab = LineChartsScreen())
                                }
                            }
                        )
                    }

                }
            }
        }
    }
    @Composable
    private fun RowScope.TabNavigationItem(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current

        NavigationBarItem(
            selected = tabNavigator.current == tab,
            onClick = { tabNavigator.current = tab },
            icon = {
                tab.options.icon?.let {
                    Icon(
                        painter = it,
                        contentDescription = tab.options.title
                    )
                }
            }
        )
    }

}