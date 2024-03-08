package ucl.student.meterbuddy.viewmodel

import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading

data class MainPageState (
    val meters: List<Meter> = emptyList(),
    val lastReading: Map<Int, List<MeterReading>> = emptyMap()
)