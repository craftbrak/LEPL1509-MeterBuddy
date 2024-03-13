package ucl.student.meterbuddy.viewmodel

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import ucl.student.meterbuddy.data.model.enums.MeterUnit

object ChartLineModel: ScreenModel {

    @Composable
    private fun getPointsFromMeterReadings(meterReadings: List<MeterReading>): List<Point>? {

        if (meterReadings.isEmpty()) { return null }

        meterReadings.sortedBy { it.date.dayOfYear.toFloat() }

        val values = mutableListOf<Point>()
        val xValues = mutableListOf<Float>()
        meterReadings.forEach {reading ->

            // TODO ( Implémenter un moyen de rendre la date en Float )
            val x = reading.date.dayOfYear.toFloat()
            var y = reading.value

            if (xValues.contains(x)) {
                y += values.last().y
                values.removeAt(values.size - 1)
            } else { xValues.add(x) }

            values.add(Point(x, y))
        }
        return values.sortedBy { it.x }.sortedBy { it.y }
    }

    @Composable
    private fun createXAxis(values: List<Point>): AxisData {
        val nbSteps = values.size
        val maxWidth = 220
        val stepSize = maxWidth / nbSteps
        return AxisData.Builder()
            .steps(nbSteps)
            .axisStepSize(stepSize.dp)
            .labelData { i -> values.getOrNull(i)?.x.toString() }
            .labelAndAxisLinePadding(15.dp)
            .backgroundColor(Color.Transparent)
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelDescription { "Time (dayOfYear) []" }
            .build()
    }

    @Composable
    private fun createYAxis(values: List<Point>, labelAxis: String): AxisData {
        val nbSteps = values.size
        val maxHeight = 200
        val stepSize = maxHeight / nbSteps
        return AxisData.Builder()
            .steps(nbSteps)
            .axisStepSize(stepSize.dp)
            .labelData { i -> values.getOrNull(i)?.y.toString() }
            .labelAndAxisLinePadding(15.dp)
            .backgroundColor(Color.Transparent)
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelDescription { labelAxis }
            .build()
    }

    @Composable
    fun DisplayChartLine(graph: LineChartData, width: Int, height: Int) {
        LineChart(modifier = Modifier
            .width(width.dp)
            .height(height.dp),
            lineChartData = graph
        )
    }

    @Composable
    fun createChartLine(readings: List<MeterReading>, type: MeterType, meterUnit: MeterUnit): LineChartData? {

        val values = this.getPointsFromMeterReadings(readings) ?: return null

        val xData = this.createXAxis(values = values)
        val yData = this.createYAxis(values = values, labelAxis = "$type Consumption [ $meterUnit ]")

        return LineChartData(
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
    }
}