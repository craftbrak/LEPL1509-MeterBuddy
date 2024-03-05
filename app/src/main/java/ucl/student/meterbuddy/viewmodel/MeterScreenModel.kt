package ucl.student.meterbuddy.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import ucl.student.meterbuddy.data.UserDatabase
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.enums.MeterIcon
import ucl.student.meterbuddy.data.repository.LocalMeterRepository
import java.time.Instant
import java.time.LocalDateTime
import java.util.Date
import android.content.Context
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
data class MeterScreenModel(val meter: Meter, val context: Context): ScreenModel {
    val  meterRepostiory = LocalMeterRepository( UserDatabase.getInstance(context).userDao)


    private val _state = mutableStateOf(MeterState(meter))
    val state :State<MeterState> = _state

    init {
        getAllReadings()
    }
    fun getAllReadings(){
        screenModelScope.launch {
            meterRepostiory.getMeterReadings(meter.meterID).collect {
                _state.value = state.value.copy(
                    readings = it
                )
            }
        }

    }
    fun addReading( reading: Float, date: LocalDateTime, note: String?=null) {
        screenModelScope.launch {
            meterRepostiory.addReading(MeterReading(0, meter.meterID, reading, date, note))
        }
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