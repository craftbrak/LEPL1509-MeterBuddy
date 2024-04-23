package ucl.student.meterbuddy.viewmodel

import android.view.Gravity
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import kotlin.math.floor
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.ConstraintLayout
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
import java.time.LocalDate

//import java.time.LocalDate

object ChartLineModel: ScreenModel {

    @Composable
    private fun getPointsFromMeterReadings(meterReadings: List<MeterReading>): List<Point>? {

        if (meterReadings.isEmpty()) {
            val values = mutableListOf<Point>()
            values.add(Point(0f,0f))
            return values
        }

        val meterReadingsSorted = meterReadings.sortedBy { it.date.dayOfYear }

        val values = mutableListOf<Point>()
        val xValues = mutableSetOf<Float>()
        meterReadingsSorted.forEach {reading ->
            val x = reading.date.dayOfYear.toFloat()
            var y = reading.value

            if (x in xValues) {
                y += values.last().y
                values.removeLast()
            } else { xValues.add(x) }

            values.add(Point(x, y))
        }
        return values.sortedBy { it.y }.sortedBy { it.x }
    }

    @Composable
    private fun createXAxis(values: List<Point>, maxWidth: Dp): AxisData {
        val nbSteps = if (values.size > 2) values.size - 1 else 1
        val stepSize = maxWidth / nbSteps
        return AxisData.Builder()
            .steps(nbSteps)
            .axisStepSize(stepSize)
            .labelData { i -> getXLabel(i, values, nbSteps) }
            .labelAndAxisLinePadding(15.dp)
            .backgroundColor(Color.Transparent)
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelDescription { "Time (dayOfYear) []" }
            .build()
    }

    @Composable
    private fun createYAxis(values: List<Point>, labelAxis: String): AxisData {
        val nbSteps = if (values.size > 3) values.size - 1 else 2
        val maxHeight = 200
        val stepSize = maxHeight / nbSteps
        return AxisData.Builder()
            .steps(nbSteps)
            .axisStepSize(stepSize.dp)
            .labelData {i -> getYLabel(i, values, nbSteps) }
            .labelAndAxisLinePadding(15.dp)
            .backgroundColor(Color.Transparent)
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelDescription { labelAxis }
            .build()
    }
    private fun getXLabel(i : Int, values : List<Point>, nbSteps : Int) : String {
        val i = i / 6 /* Bizarre mais contrairement au labelData de yAxis, i s'incremente de 6 par 6 au lieu de 1. */
        if (i < values.size)
        {
            val dayOfYear = values[i].x.toInt()
            println("The day $dayOfYear of the year 2024 is ${LocalDate.ofYearDay(2024,dayOfYear)}")
            return LocalDate.ofYearDay(2024,dayOfYear).toString()
        }
        else
        {
            val dayOfYear = values.last().x.toInt() + i*7
            return LocalDate.ofYearDay(2024,dayOfYear).toString()
        }
    }

    private fun getYLabel(i : Int, values : List<Point>, nbSteps : Int) : String {
        if (values.size == 1)
        {
            if (i == 0) { return "0"}
            if (i == 1) { return floor(values[0].y).toString() }
            else { return floor(values[0].y * 2).toString() }
        }
        else
        {
            val yScale = values.maxBy { it.y }.y - values.minBy { it.y }.y
            return floor(values.minBy { it.y }.y +(i * (yScale / nbSteps))).toString()
        }
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
    fun createChartLine(readings: List<MeterReading>, type: MeterType, meterUnit: MeterUnit, maxWidth: Dp): LineChartData? {
        val values = this.getPointsFromMeterReadings(readings) ?: return null
        val xData = this.createXAxis(values = values, maxWidth = maxWidth)
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