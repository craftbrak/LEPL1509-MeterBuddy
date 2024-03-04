package ucl.student.meterbuddy.viewmodel

import ucl.student.meterbuddy.data.model.entity.Meter

data class MainPageState (
    val listMeter: List<Meter> = emptyList()
)