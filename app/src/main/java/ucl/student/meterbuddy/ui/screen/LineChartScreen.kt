package ucl.student.meterbuddy.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import cafe.adriel.voyager.core.screen.Screen
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.enums.MeterIcon
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.Unit
import java.time.LocalDateTime

class LineChartScreen() : Screen {

    @Composable
    override fun Content() {

        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val screenHeight = configuration.screenHeightDp.dp

        val meter = Meter(1, "Electricity", Unit.KILO_WATT_HOUR, MeterIcon.Electricity, MeterType.ELECTRICITY,1,23121.23,true)
        val readingsData = listOf<MeterReading>(
            MeterReading(1, meter.meterID, 1123.24f, LocalDateTime.now()),
            MeterReading(2, meter.meterID, 2943.1f, LocalDateTime.now()),
            MeterReading(3, meter.meterID, 2951.346f, LocalDateTime.now()),
            MeterReading(4, meter.meterID, 4324.131f, LocalDateTime.now()),
            MeterReading(5, meter.meterID, 9682.123f, LocalDateTime.now())
        )

        val xAxisData = AxisData.Builder()
            .axisStepSize(80.dp)
            .backgroundColor(Color.Transparent)
            .steps(readingsData.size - 1)
            .labelData { i -> i.toString() }
            .labelAndAxisLinePadding(15.dp)
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .build()


        val yAxisData = AxisData.Builder()
            .steps(readingsData.size - 1)
            .backgroundColor(Color.Transparent)
            .labelAndAxisLinePadding(15.dp)
            .labelData { i ->
                readingsData[i].value.toString()
            }
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .build()

        val lineChartData = LineChartData(
            linePlotData = LinePlotData(
                lines = listOf(
                    Line(
                        dataPoints = readingsData.map { it -> Point(it.readingID.toFloat(), it.value) },
                        LineStyle(
                            color = MaterialTheme.colorScheme.tertiary,
                            lineType = LineType.SmoothCurve(isDotted = false)
                        ),
                        IntersectionPoint( color = MaterialTheme.colorScheme.tertiary ),
                        SelectionHighlightPoint( color = MaterialTheme.colorScheme.primary ),
                        ShadowUnderLine(
                            alpha = 0.5f,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.inversePrimary,
                                    Color.Transparent
                                )
                            )
                        ),
                        SelectionHighlightPopUp()
                    )
                ),
            ),
            backgroundColor = MaterialTheme.colorScheme.surface,
            xAxisData = xAxisData,
            yAxisData = yAxisData,
            gridLines = GridLines(color = MaterialTheme.colorScheme.outline)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight / 2)
            ) {
                LineChart(
                    modifier = Modifier.fillMaxWidth(),
                    lineChartData = lineChartData
                )
            }
        }
    }
}