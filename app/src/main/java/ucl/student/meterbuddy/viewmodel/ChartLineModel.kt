package ucl.student.meterbuddy.viewmodel

import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.util.Log
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import ucl.student.meterbuddy.data.model.TypeConverters
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.MeterUnit
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.floor

object ChartLineModel: ScreenModel {
    val converter = TypeConverters()
    @Composable
    private fun getPointsFromMeterReadings(
        meterReadings: List<MeterReading>,
        maxWidth: Dp
    ): List<Point> {
        if (meterReadings.isEmpty()) {
            return listOf(Point(0f, 0f))
        }
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val meterReadingsSorted = meterReadings.sortedBy { it.date }

        val values = mutableListOf<Point>()
        val scale = getScale(meterReadingsSorted, maxWidth)
        meterReadingsSorted.forEachIndexed { index, reading ->
            // Normalize the X position based on the index and the size of the dataset
            val x = (scale.value * index)
            val y = reading.value
            values.add(Point(x, y, description = "$y, date: ${reading.date.format(formatter)}"))
        }

        return values.sortedBy { it.y }.sortedBy { it.x }
    }

    fun determineUnit(startDate: LocalDateTime, endDate: LocalDateTime, maxWidth: Int): ChronoUnit {
        val daysDelta = ChronoUnit.DAYS.between(startDate, endDate)
        if (daysDelta > maxWidth / 2) {
            val weeksDelta = ChronoUnit.WEEKS.between(startDate, endDate)
            if (weeksDelta > maxWidth / 2) {
                return ChronoUnit.MONTHS
            } else {
                return ChronoUnit.WEEKS
            }
        } else {
            return ChronoUnit.DAYS
        }
    }

    fun calculateDelta(startDate: LocalDateTime, endDate: LocalDateTime, unit: ChronoUnit): Long {
        return unit.between(startDate, endDate)
    }

    private fun getScale(values: List<MeterReading>, maxWidth: Dp): Dp {
        val sorted = values.sortedBy { it.date }
        val unit = determineUnit(sorted.first().date, sorted.last().date, maxWidth.value.toInt())
        val delta = calculateDelta(sorted.first().date, sorted.last().date, unit)
        Log.d("ChartLineModel", "Delta = $delta")
        Log.d(
            "ChartLineModel",
            "Proposed scale = ${(maxWidth.value.toInt() / (values.size)).dp / 4}"
        )
//        if (values.size == 2){
//            return ((maxWidth.value.toInt()) / (values.size*9)).dp
//        }
//        if (values.size == 3){
//            return ((maxWidth.value.toInt()) / (values.size*8)).dp
//        }
//
//        return ((maxWidth.value.toInt()) / (values.size* values.size)).dp
        return 8.dp
    }

    @Composable
    private fun createXAxis(values: List<MeterReading>, maxWidth: Dp): AxisData {
        val nbSteps = if (values.size > 2) values.size - 1 else 1
        // val nbSteps = if (values.size > 3) values.size - 1 else 2
        val stepSize = getScale(values, maxWidth - 200.dp)
        val sortedReadings = values.sortedBy { it.date }
        return AxisData.Builder()
            .steps(nbSteps)
            .axisStepSize(stepSize)
            .axisLabelAngle(10f)
            .labelData { a -> getXLabel(a, sortedReadings, nbSteps, stepSize) }
            .labelAndAxisLinePadding(15.dp)
            .backgroundColor(Color.Transparent)
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelDescription { "Time []" }
            .build()
    }

    @Composable
    private fun createYAxis(values: List<MeterReading>, labelAxis: String): AxisData {
        val longestLabelLength = values.maxByOrNull { it.value }?.value.toString().length
        val padding = (longestLabelLength * 6).dp
        val nbSteps = values.size
        val maxHeight = 200
        val stepSize = maxHeight / nbSteps

        return AxisData.Builder()
            .steps(nbSteps)
            .axisStepSize(stepSize.dp)
            .labelData { i -> getYLabel(i, values, nbSteps) }
            .labelAndAxisLinePadding(padding)
            .backgroundColor(Color.Transparent)
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelDescription { labelAxis }
            .build()
    }

    private fun getXLabel(a: Int, values: List<MeterReading>, nbSteps: Int, scale: Dp): String {
        val i = a / scale.value.toInt()  // Normalize the X position based on the axis step size
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        if (i < values.size)
        {
            val formattedDate = values[i].date.format(formatter)
            return formattedDate.toString()
        }
        else
        {
            val formattedDate = values.last().date.format(formatter)
            return formattedDate
        }
    }

    private fun getYLabel(i: Int, values: List<MeterReading>, nbSteps: Int): String {
        if (values.size == 1)
        {
            if (i == 0)      { return "0"} else if (i == 1) {
                return floor(values[0].value).toString()
            } else {
                return floor(values[0].value * 2).toString()
            }
        }
        else
        {
            val yScale = values.maxBy { it.value }.value - values.minBy { it.value }.value
            return floor(values.minBy { it.value }.value + (i * (yScale / nbSteps))).toString()
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
                        val popUpText = identifiedPoint.description
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
        val values = this.getPointsFromMeterReadings(readings, maxWidth)
        val xData = this.createXAxis(values = readings, maxWidth = maxWidth - 50.dp)
        val yData =
            this.createYAxis(values = readings, labelAxis = "$type Consumption [ $meterUnit ]")
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
        val valuesConsumption = this.getPointsFromMeterReadings(
            readingsConsumption,
            maxWidth
        ).toMutableList()
        val valuesProduction = this.getPointsFromMeterReadings(
            readingsProduction,
            maxWidth
        ).toMutableList()
        val values = readingsConsumption + readingsProduction
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