package ucl.student.meterbuddy.viewmodel

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import java.math.BigDecimal
import java.math.RoundingMode

object ChartLineModel: ScreenModel {

    @Composable
    private fun getPointsFromMeterReadings(meterReadings: List<MeterReading>): List<Point>? {

        if (meterReadings.isEmpty()) { return null }

        val meterReadingsSorted = meterReadings.sortedBy { it.date.dayOfYear }

        val values = mutableListOf<Point>()
        val xValues = mutableSetOf<Float>()
        meterReadingsSorted.forEach {reading ->

            // TODO ( Implémenter un moyen de rendre la date en Float )
            val x = reading.date.dayOfYear.toFloat()
            var y = reading.value

            if (x in xValues) {
                y += values.last().y
                values.removeLast()
            } else { xValues.add(x) }

            values.add(Point(x, y))
        }
        return values.sortedBy { it.x }.sortedBy { it.y }
    }

    private fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    private fun roundNb(nombre: Double): Double {
        return BigDecimal(nombre).setScale(-1, RoundingMode.HALF_UP).toDouble()
    }


    @Composable
    private fun createXAxis(nbPoints: Int, minMaxTime: List<Float>): AxisData {

        val screenWidth = LocalContext.current.resources.displayMetrics.widthPixels
        val screenWidthFactor = 0.9

        val min = roundNb(0.9 * minMaxTime[0])
        val max = roundNb(1.1 * minMaxTime[1])

        val nbSteps = 2 + nbPoints
        val realStep = (max - min) / nbSteps
        // val stepSize = (max - min) / nbSteps
        // val stepSize = 200
        val stepSize = screenWidthFactor * screenWidth / (10 * nbSteps)

        return AxisData.Builder()
            .steps(nbSteps)
            .axisStepSize(stepSize.dp)
            .labelData { i ->
                (min + (i / nbPoints) * realStep).toInt().toString()
            }
            .labelAndAxisLinePadding(15.dp)
            .backgroundColor(Color.Transparent)
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelDescription { _ -> "Time (dayOfYear) []" }
            .build()
    }

    @Composable
    private fun createYAxis(minMaxValue: List<Float>, labelAxis: String): AxisData {
        val nbSteps = 8
        val min = 0.9 * minMaxValue[0]
        val max = 1.1 * minMaxValue[1]
        val range = max - min
        val stepSize = range / nbSteps
        return AxisData.Builder()
            .steps(nbSteps)
            .axisStepSize(stepSize.dp)
            .labelData { i -> ((min + i * stepSize).toInt()).toString() }
            .labelAndAxisLinePadding(15.dp)
            .backgroundColor(Color.Transparent)
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelDescription { _ -> labelAxis }
            .build()
    }

    @Composable
    fun DisplayChartLine(graph: LineChartData, width: Int, height: Int) {
        LineChart(modifier = Modifier
            .fillMaxWidth()
            .height(height.dp),
                  lineChartData = graph
        )
    }

    private fun getMinMaxTime(values: List<Point>): List<Float> {
        var minTime = Float.MAX_VALUE
        var maxTime = Float.MIN_VALUE
        values.forEach { point ->
            if (point.x < minTime) { minTime = point.x }
            if (maxTime < point.x) { maxTime = point.x }
        }
        return listOf(minTime, maxTime)
    }

    private fun getMinMaxValue(values: List<Point>): List<Float> {
        var minValue = Float.MAX_VALUE
        var maxValue = Float.MIN_VALUE
        values.forEach { point ->
            if (point.y < minValue) { minValue = point.y }
            if (maxValue < point.y) { maxValue = point.y }
        }
        return listOf(minValue, maxValue)
    }

    @Composable
    fun createChartLine(readings: List<MeterReading>, type: MeterType, meterUnit: MeterUnit): LineChartData? {

        val values = this.getPointsFromMeterReadings(readings) ?: return null
        val minMaxValue = this.getMinMaxValue(values)
        val minMaxTime = this.getMinMaxTime(values)
        val xData = this.createXAxis(values.size, minMaxTime)
        val yData = this.createYAxis(minMaxValue, "$type Consumption [ $meterUnit ]")

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