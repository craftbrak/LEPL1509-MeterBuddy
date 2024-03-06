package ucl.student.meterbuddy.viewmodel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.repository.LocalMeterRepository

class ChartLineModel(meterRepository: LocalMeterRepository): ScreenModel {

    val meterRepository = meterRepository

    fun getValuesFromMeterReadings(meter: List<MeterReading>): List<Float> {
        val values = mutableListOf<Float>()
        meter.forEach { reading -> values.add(reading.value) }
        return values
    }

    @Composable
    fun createXAxis(nbPoints: Int): AxisData {
        return AxisData.Builder()
            .axisStepSize(80.dp)
            .backgroundColor(Color.Transparent)
            .steps(nbPoints)
            .labelData { i -> i.toString() }
            .labelAndAxisLinePadding(15.dp)
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .build()
    }

    @Composable
    fun createYAxis(values: List<Float>): AxisData {
        return AxisData.Builder()
            .steps(values.size)
            .backgroundColor(Color.Transparent)
            .labelAndAxisLinePadding(15.dp)
            .labelData { i -> values.getOrNull(i)?.toString() ?: "" }
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .build()
    }

    @Composable
    // TODO(RETURN TYPE MUST BE : LineChartData)
    fun createChartLine(meter: Meter, height: Int, width: Int) {

        val readings = meterRepository.getMeterReadings(meter.meterID).collectAsState(initial = emptyList()).value
        val values = this.getValuesFromMeterReadings(readings)
        val xData = this.createXAxis(nbPoints = values.size)
        val yData = this.createYAxis(values = values)
        
        Text(text = "Il devrait y avoir un graph qui s'affiche mais y'a des bugs.")
            
        // TODO(ERROR HERE => NoSuchElementException)
        // TODO(To remove the error : Comment the part below and remove the returned type)
        /*
        val lineChartData = LineChartData(
            linePlotData = LinePlotData(
                lines = listOf(
                    Line(
                        dataPoints = values.mapIndexed { idx, it -> Point(idx.toFloat(), it) },
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
                    modifier = Modifier.fillMaxWidth(),
                    lineChartData = lineChartData
                )
            }
        }
        return lineChartData
        */
    }
}