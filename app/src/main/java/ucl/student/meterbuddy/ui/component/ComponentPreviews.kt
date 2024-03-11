package ucl.student.meterbuddy.ui.component

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ucl.student.meterbuddy.ui.screen.HomeScreen

@Preview(name= "TabButtons")
@Composable
fun TabButtonPreview(){
    HomeScreen.TabButtons(onClick = {Log.i("TabButtonPreview", "On click")},icon= Icons.Default.Add,"add", true)
}
@Preview(name = "BottomTabBar")
@Composable
fun BottomTabBarPreview(){
    HomeScreen.BottomTabBar()
}
