package ucl.student.meterbuddy.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import ucl.student.meterbuddy.data.UserDatabase
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.repository.LocalMeterRepository
import java.time.LocalDateTime
import android.content.Context
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.hilt.ScreenModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import ucl.student.meterbuddy.data.repository.MeterRepository

class MeterScreenModel @AssistedInject constructor (
   @Assisted val meter: Meter,
    private val meterRepository: MeterRepository
): ScreenModel {

    private val _state = mutableStateOf(MeterState(meter))
    val state :State<MeterState> = _state

    init { getReadings() }

    @AssistedFactory
    interface Factory: ScreenModelFactory{
        fun create(meter:Meter): MeterScreenModel
    }

    private fun getReadings() {
        screenModelScope.launch {
            meterRepository.getMeterReadings(meter.meterID).collect {
                _state.value = state.value.copy(
                    readings = it
                )
            }
        }
    }

    fun addReading(reading: Float, date: LocalDateTime, note: String?=null) {
        screenModelScope.launch {
            meterRepository.addReading(MeterReading(0, meter.meterID, reading, date, note))
        }
    }

    fun updateReading( readingId: Int, value: Float, dateLocalDateTime: LocalDateTime, note: String?= null) {
        screenModelScope.launch {
            val reading = state.value.readings.find { it.readingID == readingId }
            reading?.value = value
            reading?.date = dateLocalDateTime
            reading?.note = note
            meterRepository.updateMeterReading(reading!!)
        }
        Log.i("MeterScreenModel", "Updated reading $readingId to meter ${state.value.meter.meterID}: $value at $dateLocalDateTime")
    }

    fun deleteReading(readingId: Int) {
        screenModelScope.launch {
            state.value.readings.find { it.readingID == readingId }
                ?.let { meterRepository.deleteReading(it) }
        }
        Log.i("MeterScreenModel", "Deleted reading $readingId from meter ${state.value.meter.meterID}")
    }
}