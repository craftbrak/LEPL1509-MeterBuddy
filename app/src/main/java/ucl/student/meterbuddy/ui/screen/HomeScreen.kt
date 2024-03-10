package ucl.student.meterbuddy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator

object HomeScreen : Screen {

    private lateinit var mainPageScreenModel: MainPageScreenModel

    @Composable
    override fun Content() {
        val context = LocalContext.current
        mainPageScreenModel = rememberScreenModel { MainPageScreenModel(context) }
        MeterListScreen(mainPageScreenModel).Content()
    }

    @Composable
    fun BottomTabBar() {
        BottomAppBar(modifier = Modifier.fillMaxWidth()) {
            val navigator = LocalNavigator.current
            val currentScreen = navigator?.lastItem
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                TabButtons(
                    onClick = {
                        if (currentScreen?.javaClass != MeterListScreen::class.java || currentScreen.javaClass != HomeScreen::class.java)
                            navigator?.push(MeterListScreen(mainPageScreenModel = mainPageScreenModel))
                    },
                    icon = Icons.Default.Home,
                    contentDescription = "Home",
                    currentScreen?.javaClass == MeterListScreen::class.java || currentScreen?.javaClass == HomeScreen::class.java
                )
                TabButtons(
                    onClick = {
                        if (currentScreen != LineChartsScreen)
                            navigator?.push(LineChartsScreen)
                    },
                    icon = Icons.Default.ThumbUp,
                    contentDescription = "Stats",
                    currentScreen?.javaClass == LineChartsScreen::class.java
                )

            }
        }
    }

    @Composable
    fun TabButtons(
        onClick: () -> Unit,
        icon: ImageVector,
        contentDescription: String,
        selected: Boolean = false
    ) {
        val color =
            if (selected) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.onSurface
        Column(
            modifier = Modifier
                .clickable { onClick() }
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = contentDescription, tint = color)
            Text(text = contentDescription, style = MaterialTheme.typography.bodySmall, color = color)
        }
    }
}