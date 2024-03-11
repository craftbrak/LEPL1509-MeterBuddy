package ucl.student.meterbuddy.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ucl.student.meterbuddy.R
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.ui.component.MeterFormDialog
import ucl.student.meterbuddy.ui.component.MeterOverviewCard
import ucl.student.meterbuddy.ui.component.MetersListTab

object HomeScreen : Screen {

    lateinit var mainPageScreenModel: MainPageScreenModel

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val context = LocalContext.current
        mainPageScreenModel = rememberScreenModel { MainPageScreenModel(context) }

        TabNavigator(tab = MetersListTab){
            CurrentTab()
        }
    }
    @Composable
    private fun RowScope.TabNavigationItem(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current

        NavigationBarItem(
            selected = tabNavigator.current == tab,
            onClick = { tabNavigator.current = tab },
            icon = { tab.options.icon?.let { Icon(painter = it, contentDescription = tab.options.title) } }
        )
    }


    @Composable
    private fun SwiperToLeft(navigator: Navigator?, scope: CoroutineScope): Modifier {
        return Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { _, delta ->
                    if (delta.y > 0) {
                        scope.launch {
                            navigator?.push(LineChartsScreen(mainPageScreenModel))
                        }
                    }
                }
            }
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
                        if (currentScreen?.javaClass != HomeScreen::class.java)
                            navigator?.pop()
                    },
                    icon = Icons.Default.Home,
                    contentDescription = "Home",
                    currentScreen?.javaClass == HomeScreen::class.java
                )
                TabButtons(
                    onClick = {
                        if (currentScreen != LineChartsScreen(mainPageScreenModel))
                            navigator?.push(LineChartsScreen(mainPageScreenModel))
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
            Text(
                text = contentDescription,
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
        }
    }

}
