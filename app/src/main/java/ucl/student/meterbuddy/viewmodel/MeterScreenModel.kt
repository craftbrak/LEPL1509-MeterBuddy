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
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
data class MeterScreenModel(val meter: Meter): ScreenModel {

    var readings = mutableListOf(
        MeterReading(1, meter.meterID, 1.0, Date.from(Instant.now())),
        MeterReading(2, meter.meterID, 2.0, Date.from(Instant.now())),
        MeterReading(3, meter.meterID, 3.0, Date.from(Instant.now())),
        MeterReading(4, meter.meterID, 4.0, Date.from(Instant.now())),
        MeterReading(5, meter.meterID, 5.0, Date.from(Instant.now())),
        MeterReading(6, meter.meterID, 6.0, Date.from(Instant.now())),
        MeterReading(7, meter.meterID, 7.0, Date.from(Instant.now())),
        MeterReading(8, meter.meterID, 8.0, Date.from(Instant.now())),
        MeterReading(9, meter.meterID, 9.0, Date.from(Instant.now())),
        MeterReading(10, meter.meterID, 10.0, Date.from(Instant.now())),
        MeterReading(11, meter.meterID, 11.0, Date.from(Instant.now())),
        MeterReading(12, meter.meterID, 12.0, Date.from(Instant.now()))
    )

    private val _state = mutableStateOf(MeterState(meter, readings))
    val state :State<MeterState> = _state

    fun addReading(meterId: Int, reading: Double, date: String) {
        val mutList = state.value.readings.toMutableList()
        mutList.add(MeterReading(state.value.readings.size+1, meterId, reading, Date.from(Instant.now())))
        _state.value= state.value.copy(
            readings = mutList
        )
        Log.i("MeterScreenModel", "Added reading to meter $meterId: $reading at $date")
    }
}