package ucl.student.meterbuddy.viewmodel

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
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
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
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.Unit

object ChartLineModel: ScreenModel {

    private fun getPointsFromMeterReadings(meterReadings: List<MeterReading>): List<Point>? {

        if (meterReadings.isEmpty()) { return null }

        val values = mutableListOf<Point>()
        meterReadings.forEach {reading ->
            // TODO ( Implémenter un moyen de rendre la date en Float )
            val x = reading.date.dayOfYear.toFloat()
            val y = reading.value
            values.add(Point(x, y))
        }
        return values.sortedBy { it.x }.sortedBy { it.y }
    }

    @Composable
    private fun createXAxis(values: List<Point>): AxisData {
        return AxisData.Builder()
            .steps(values.size)
            .axisStepSize(40.dp)
            .backgroundColor(Color.Transparent)
            .labelAndAxisLinePadding(15.dp)
            .labelData { i -> values.getOrNull(i)?.x.toString() }
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelDescription { "Time (dayOfYear) []" }
            .shouldDrawAxisLineTillEnd(true)
            .build()
    }

    @Composable
    private fun createYAxis(values: List<Point>, labelAxis: String): AxisData {
        return AxisData.Builder()
            .steps(values.size)
            .backgroundColor(Color.Transparent)
            .labelAndAxisLinePadding(15.dp)
            .labelData { i -> values.getOrNull(i)?.y.toString() }
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelDescription { labelAxis}
            .shouldDrawAxisLineTillEnd(true)
            .build()
    }

    @Composable
    fun CreateChartLine(readings: List<MeterReading>, type: MeterType, unit: Unit, height: Int, width: Int) {

        val values = this.getPointsFromMeterReadings(readings)

        if (values != null) {
            val xData = this.createXAxis(values = values)
            val yData = this.createYAxis(values = values, labelAxis = "$type Consumption [ $unit ]")

            val lineChartData = LineChartData(
                linePlotData = LinePlotData(
                    lines = listOf(
                        Line(
                            dataPoints = values,
                            LineStyle(
                                color = MaterialTheme.colorScheme.tertiary,
                                lineType = LineType.SmoothCurve(isDotted = false)
                            ),
                            IntersectionPoint(color = MaterialTheme.colorScheme.tertiary),
                            SelectionHighlightPoint(color = MaterialTheme.colorScheme.primary),
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
                xAxisData = xData,
                yAxisData = yData,
                gridLines = GridLines(color = MaterialTheme.colorScheme.outline)
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(width.dp)
                        .height(height.dp)
                ) {
                    LineChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        lineChartData = lineChartData
                    )
                }
            }
        }
    }
}