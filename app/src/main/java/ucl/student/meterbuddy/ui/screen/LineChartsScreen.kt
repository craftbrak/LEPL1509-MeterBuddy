package ucl.student.meterbuddy.ui.screen

import android.content.res.Resources
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import ucl.student.meterbuddy.data.UserDatabase
import ucl.student.meterbuddy.data.repository.LocalMeterRepository
import ucl.student.meterbuddy.viewmodel.ChartLineModel

class LineChartsScreen(lineModel: ChartLineModel): Screen {

    val lineModel = lineModel

    @Composable
    override fun Content() {

        val context = LocalContext.current
        val resources: Resources = context.resources
        val screenWidth: Int = resources.displayMetrics.widthPixels
        val screenHeight: Int = resources.displayMetrics.heightPixels
        val paddingBetweenCharts = 100
        val meterRepository = LocalMeterRepository(UserDatabase.getInstance(context).userDao)
        val meters = meterRepository.getMeters().collectAsState(initial = emptyList()).value
        // meters.forEach { meter ->
        //     lineModel.createChartLine(meter = meter, height = screenHeight / 10, width = screenWidth)
        //     Spacer(modifier = Modifier.height((screenHeight / 10 + paddingBetweenCharts).dp))
        // }
        Text(text = "Bugs for now :/")
    }
}