package ucl.student.meterbuddy

/*
Bugs TODO :
- Ajout d'un utilisateur ou delete d'un utilisateur (NullPointerException), j'imagine que c'est à cause d'une ancienne adresse supprimée
- Pour les ajouts d'utilisateurs, ça met les adresses mail et parfois les noms d'utilisateurs ? Mettre le pseudo + un # avec un numéro unique, ça serait bien
- Delete les maisons ? Pour l'instant, je peux add qqn et me degager mais ça créer des bugs de fou => Creation de pleins de nvlle maisons,..
- A la création d'un nouveau compte, ça créer tjr automatiquement une maison.
- Bug visuel : "Bangalow", le "w" se met en-dessous chez moi et donc la case est trop grande par rapport aux autres
- Quand je change de maison ou autres, le temps de chargements, plusieurs écrans apparaisent (les mauvais) pendant un très court instant.
- Graphs:
    - on a la possibilité de mettre que du 2024
    - bien caser le graphs dans la width du tél
    - Faire en sorte que les abcisses, on sait les lire (mettre en diagonale ou quoi les labels)
    - Mettre des titres aux axes (pas réussis)
- La page de graphs complète est buggé et rien marche
 */

//import androidx.navigation.navigation
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import ucl.student.meterbuddy.data.utils.AuthException
import ucl.student.meterbuddy.data.utils.Resource
import ucl.student.meterbuddy.ui.component.MetersListTab
import ucl.student.meterbuddy.ui.screen.HomeScreen
import ucl.student.meterbuddy.ui.screen.LineChartsScreen
import ucl.student.meterbuddy.ui.screen.LoginScreen
import ucl.student.meterbuddy.ui.theme.MeterBuddyTheme
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val localScreenContext = compositionLocalOf<Context> { error("No Context provided") }
    private val mainPageScreenModel: MainPageScreenModel by viewModels()

        private lateinit var analytics: FirebaseAnalytics
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics = Firebase.analytics
        installSplashScreen().apply {}
        setContent {
            val navController = rememberNavController()
            MeterBuddyTheme{
                CompositionLocalProvider(localScreenContext provides this) {
                    HomeScreen.Content()
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = "loading") {
                        composable("auth"){
                            Auth()
                        }
                        composable("loading"){
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ){
                                Image(
                                    painter = painterResource(id = R.drawable.meter_budy_logo),
                                    contentDescription = "App Logo",
                                    modifier = Modifier
                                        .size(150.dp)
                                        .clip(CircleShape)
                                )
                            }

                        }
                        composable("home"){
                            MainComp()
                        }
                    }

                }
            }

            LaunchedEffect(key1 = mainPageScreenModel.state.value.currentUser) {
                mainPageScreenModel.state.value.currentUser.collect{
                    when(it){
                        is Resource.Error -> {
                            when (it.error)
                            {
                                AuthException.BAD_CREDENTIALS -> Log.i("Bad Cred","bad cred")
                                AuthException.NO_NETWORK -> Log.i("No Network","cool")
                                AuthException.NO_CURRENT_USER -> Log.i("No Current User","nobody connected")
                                AuthException.TO_MANY_ATTEMPT -> Log.i("MainActivity","ToMAnyAttempt")
                                AuthException.PASSWORD_TO0_SHORT -> Log.i("PASSWORD_TO0_SHORT", "PASSWORD_TO0_SHORT")
                                AuthException.EMAIL_BAD_FORMATTED -> Log.i("EMAIL_BAD_FORMATTED", "EMAIL_BAD_FORMATTED")
                                AuthException.EMAIL_ALREADY_TAKEN -> Log.i("EMAIL_ALREADY_TAKEN", "EMAIL_ALREADY_TAKEN")
                                AuthException.UNKNOWN_ERROR -> Log.i("HAAAAAAAAAAAAAAAAAAa","merde")
                            }
                            // Clear the back stack before navigating to the login screen
                            Log.w("MainActivity","auth error")
                            navController.navigate("auth"){
                                popUpTo(navController.graph.id)
                            }
                        }
                        is Resource.Loading -> {
//                            navController.navigate("loading")
//                            Log.i("Loading please wait", "wait")
                        }
                        is Resource.Success -> {
                            navController.navigate("home"){
                                popUpTo(navController.graph.id)
                            }
                        }
                    }
                }
            }
        }
    }
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    private fun MainComp(){
        TabNavigator(tab = MetersListTab) {
            Scaffold(
                content = {
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
    @Composable
    private fun Auth(){
        Navigator(screen = LoginScreen()) {
            SlideTransition(navigator = it)
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