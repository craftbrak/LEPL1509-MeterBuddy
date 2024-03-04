package ucl.student.meterbuddy.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.enums.MeterIcon
import java.time.Instant
import java.time.LocalDateTime
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
data class MeterScreenModel(val meter: Meter): ScreenModel {

    var readings = mutableListOf(
        MeterReading(1, meter.meterID, 1123.24f, LocalDateTime.now()),
        MeterReading(2, meter.meterID, 2343.1f,LocalDateTime.now()),
        MeterReading(3, meter.meterID, 3231.346f, LocalDateTime.now()),
        MeterReading(4, meter.meterID, 4324.131f, LocalDateTime.now()),
        MeterReading(5, meter.meterID, 5682.123f, LocalDateTime.now()),
    )

    private val _state = mutableStateOf(MeterState(meter, readings))
    val state :State<MeterState> = _state

    fun addReading( reading: Float, date: LocalDateTime, note: String?=null) {
        val mutList = state.value.readings.toMutableList()
        mutList.add(MeterReading(state.value.readings.size+1, state.value.meter.meterID, reading, date,note))
        _state.value= state.value.copy(
            readings = mutList
        )
        Log.i("MeterScreenModel", "Added reading to meter ${state.value.meter.meterID}: $reading at $date")
    }
    fun updateReading( readingId: Int, value: Float, dateLocalDateTime: LocalDateTime, note: String?= null){
        val mutList = state.value.readings.toMutableList()
        mutList[readingId]= mutList[readingId].copy(
            value= value,
            date = dateLocalDateTime,
            note = note
        )
        _state.value= state.value.copy(
            readings = mutList
        )
        Log.i("MeterScreenModel", "Updated reading $readingId to meter ${state.value.meter.meterID}: $value at $dateLocalDateTime")
    }
}