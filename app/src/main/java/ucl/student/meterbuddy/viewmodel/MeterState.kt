package ucl.student.meterbuddy.viewmodel

import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading

data class MeterState (
    val meter: Meter,
    val readings: List<MeterReading> = emptyList()
)
