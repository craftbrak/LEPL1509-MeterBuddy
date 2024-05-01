package ucl.student.meterbuddy.viewmodel

import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import androidx.compose.foundation.layout.Row
import kotlin.math.floor
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.ScreenModel
import co.yml.charts.axis.AxisData
import co.yml.charts.common.extensions.getTextBackgroundRect
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
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.pow

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

    private fun getScale(values: List<Point>, maxWidth: Dp) : Dp {
        val sorted = values.sortedBy { it.x }
        val first = sorted.first()
        val last = sorted.last()
        return (maxWidth / (last.x - first.x))
    }

    @Composable
    private fun createXAxis(values: List<Point>, maxWidth: Dp): AxisData {
        val nbSteps = if (values.size > 2) values.size - 1 else 1
        // val nbSteps = if (values.size > 3) values.size - 1 else 2
        val stepSize = getScale(values,maxWidth)
        return AxisData.Builder()
            .steps(nbSteps)
            .axisStepSize(stepSize)
            .axisLabelAngle(8f)
            .labelData { a -> getXLabel(a, values, nbSteps) }
            .labelAndAxisLinePadding(15.dp)
            .backgroundColor(Color.Transparent)
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelDescription { "Time []" }
            .build()
    }

    @Composable
    private fun createYAxis(values: List<Point>, labelAxis: String): AxisData {
        val longestLabelLength = values.maxByOrNull { it.x }?.x.toString().length
        val padding = (longestLabelLength * 6).dp
        val nbSteps = 6
        val maxHeight = 200
        val stepSize = maxHeight / nbSteps
        return AxisData.Builder()
            .steps(nbSteps)
            .axisStepSize(stepSize.dp)
            .labelData {i -> getYLabel(i, values, nbSteps) }
            .labelAndAxisLinePadding(padding)
            .backgroundColor(Color.Transparent)
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelDescription { labelAxis }
            .build()
    }

    private fun getXLabel(a : Int, values : List<Point>, nbSteps : Int) : String {
        val i = a / 6 /* Bizarre mais contrairement au labelData de yAxis, i s'incremente de 6 par 6 au lieu de 1. */
        if (i < values.size)
        {
            val dayOfYear = values[i].x.toInt()
            // TODO : Rendre possible les dates > l'année 2024
            println("The day $dayOfYear of the year 2024 is ${LocalDate.ofYearDay(2024, dayOfYear)}")
            return LocalDate.ofYearDay(2024, dayOfYear).toString()
        }
        else
        {
            println(values.last().x.toInt())
            println(i * 7)
            println(values.last().x.toInt() + i * 7)
            val dayOfYear = values.last().x.toInt() + i * 7
            return LocalDate.ofYearDay(2024, dayOfYear).toString()
        }
    }

    private fun getYLabel(i : Int, values : List<Point>, nbSteps : Int) : String {
        if (values.size == 1)
        {
            if (i == 0)      { return "0"}
            else if (i == 1) { return floor(values[0].y).toString() }
            else             { return floor(values[0].y * 2).toString() }
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
    fun createLine(values: List<Point>, typeValues: String): Line {
        if (typeValues == "consumption")
        {
            val colorCircle: Color = MaterialTheme.colorScheme.primary
            return Line(
                dataPoints = values,
                LineStyle(
                    color = Color.Red,
                    lineType = LineType.SmoothCurve(isDotted = false)
                ),
                IntersectionPoint(color = Color.Red),
                SelectionHighlightPoint(color = colorCircle),

                ShadowUnderLine(
                    alpha = 0.5f,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Red.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    )
                ),
                SelectionHighlightPopUp(
                    backgroundColor = Color.White,
                    backgroundAlpha = 0.7f,
                    backgroundCornerRadius = CornerRadius(8f),
                    paddingBetweenPopUpAndPoint = 20.dp,
                    draw = { selectedOffset, identifiedPoint ->
                        // ${LocalDate.ofYearDay(2024, identifiedPoint.x.toInt())}
                        val popUpText = "x: YYYY/DD/MM, y: ${identifiedPoint.y}"
                        val paint = TextPaint().apply {
                            textSize = 14.sp.toPx()
                            color = Color.Black.toArgb()
                            textAlign = Paint.Align.CENTER
                        }
                        val fontMetrics = paint.fontMetrics
                        val textHeight = fontMetrics.descent - fontMetrics.ascent
                        val textWidth = paint.measureText(popUpText)

                        val refY = 40
                        val refX = 10
                        val rect = RectF(
                            selectedOffset.x - (textWidth / 2) - refX,
                            selectedOffset.y - refY - textHeight,
                            selectedOffset.x + (textWidth / 2) + refX,
                            selectedOffset.y - refY + 10
                        )
                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(rect.left, rect.top),
                            size = Size(rect.width(), rect.height()),
                            alpha = 0.7f,
                            cornerRadius = CornerRadius(8f)
                        )
                        drawContext.canvas.nativeCanvas.drawText(
                            popUpText,
                            selectedOffset.x,
                            selectedOffset.y - 40,
                            paint
                        )
                    }
                )
            )
        }
        else if (typeValues == "production")
        {
            val colorCircle: Color = MaterialTheme.colorScheme.primary
            return Line(
                dataPoints = values,
                LineStyle(
                    color = Color.Green,
                    lineType = LineType.SmoothCurve(isDotted = false)
                ),
                IntersectionPoint(color = Color.Green),
                SelectionHighlightPoint(color = colorCircle),

                ShadowUnderLine(
                    alpha = 0.5f,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Green.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    )
                ),
                SelectionHighlightPopUp(
                    backgroundColor = Color.White,
                    backgroundAlpha = 0.7f,
                    backgroundCornerRadius = CornerRadius(8f),
                    paddingBetweenPopUpAndPoint = 20.dp,
                    draw = { selectedOffset, identifiedPoint ->
                        // ${LocalDate.ofYearDay(2024, identifiedPoint.x.toInt())}
                        val popUpText = "x: YYYY/DD/MM, y: ${identifiedPoint.y}"
                        val paint = TextPaint().apply {
                            textSize = 14.sp.toPx()
                            color = Color.Black.toArgb()
                            textAlign = Paint.Align.CENTER
                        }
                        val fontMetrics = paint.fontMetrics
                        val textHeight = fontMetrics.descent - fontMetrics.ascent
                        val textWidth = paint.measureText(popUpText)

                        val refY = 40
                        val refX = 10
                        val rect = RectF(
                            selectedOffset.x - (textWidth / 2) - refX,
                            selectedOffset.y - refY - textHeight,
                            selectedOffset.x + (textWidth / 2) + refX,
                            selectedOffset.y - refY + 10
                        )
                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(rect.left, rect.top),
                            size = Size(rect.width(), rect.height()),
                            alpha = 0.7f,
                            cornerRadius = CornerRadius(8f)
                        )
                        drawContext.canvas.nativeCanvas.drawText(
                            popUpText,
                            selectedOffset.x,
                            selectedOffset.y - 40,
                            paint
                        )
                    }
                )
            )
        }
        else { return Line(values) } // Never reach
    }

    @Composable
    fun createChartLineIndividual(readings: List<MeterReading>, typeValues: String, type: MeterType, meterUnit: MeterUnit, maxWidth: Dp): LineChartData? {
        val values = this.getPointsFromMeterReadings(readings) ?: return null
        val xData = this.createXAxis(values = values, maxWidth = maxWidth)
        val yData = this.createYAxis(values = values, labelAxis = "$type Consumption [ $meterUnit ]")
        return LineChartData(
            linePlotData = LinePlotData(lines = listOf(createLine(values, typeValues))),
            backgroundColor = MaterialTheme.colorScheme.surface,
            xAxisData = xData,
            yAxisData = yData,
            gridLines = GridLines(color = MaterialTheme.colorScheme.outline)
        )
    }

    @Composable
    fun createChartLine(readingsConsumption: List<MeterReading>, readingsProduction: List<MeterReading>, type: MeterType, meterUnit: MeterUnit, maxWidth: Dp): LineChartData? {
        if (readingsConsumption.isEmpty()) { return createChartLineIndividual(readingsProduction, "production", type, meterUnit, maxWidth) }
        if (readingsProduction.isEmpty())  { return createChartLineIndividual(readingsConsumption, "consumption", type, meterUnit, maxWidth) }
        val valuesConsumption = this.getPointsFromMeterReadings(readingsConsumption)?.toMutableList() ?: return null
        val valuesProduction  = this.getPointsFromMeterReadings(readingsProduction)?.toMutableList() ?: return null
        val values = valuesConsumption + valuesProduction
        val xData = this.createXAxis(values = values, maxWidth = maxWidth)
        val yData = this.createYAxis(values = values, labelAxis = "$type Consumption [ $meterUnit ]")
        return LineChartData(
            linePlotData = LinePlotData(
                lines = listOf(
                    createLine(values = valuesConsumption, typeValues = "consumption"),
                    createLine(values = valuesProduction, typeValues = "production")
                )
            ),
            backgroundColor = MaterialTheme.colorScheme.surface,
            xAxisData = xData,
            yAxisData = yData,
            gridLines = GridLines(color = MaterialTheme.colorScheme.outline)
        )
    }
}