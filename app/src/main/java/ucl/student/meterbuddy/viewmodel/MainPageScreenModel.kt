package ucl.student.meterbuddy.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import ucl.student.meterbuddy.data.UserDatabase
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.MeterUnit
import ucl.student.meterbuddy.data.repository.LocalMeterRepository
import ucl.student.meterbuddy.data.repository.MeterRepository
import javax.inject.Inject


class MainPageScreenModel @Inject constructor( private val meterRepository: MeterRepository): ScreenModel {

    private val _state = mutableStateOf(MainPageState())
    val state: State<MainPageState> = _state

    init { updateState() }

    private fun updateState() {
        screenModelScope.launch {
            meterRepository.getMeterAndReadings().collect { metersAndReadings ->
//                Log.i("Meters and Readings", metersAndReadings.keys.toString())
                _state.value = state.value.copy(
                    meters = metersAndReadings.keys.toList(),
                    lastReading = metersAndReadings.map { (meter, readings) ->
                        meter.meterID to readings
                    }.toMap()
                )
            }
        }
    }

    fun updateMeter(meter: Meter) {
        screenModelScope.launch {
            meterRepository.updateMeter(meter)
        }
    }

    fun  addMeter(metre:Meter) {
        screenModelScope.launch {
            meterRepository.addMeter(metre)
        }
    }

    private suspend fun getLastReadingOfMeter(meter: Meter): MeterReading {
        return meterRepository.getMeterReadings(meter.meterID).last().last()
    }

    fun filterMetersByType(type: MeterType): List<Meter> {
        val meters = state.value.meters
        return meters.filter { meter ->
            meter.meterType == type
        }.toList()
    }

    @Composable
    fun getMeterReadings(meter: Meter): List<MeterReading> {
        return meterRepository.getMeterReadings(meter.meterID).collectAsState(initial = emptyList()).value
    }

    @Composable
    fun getMetersReadings(meters: List<Meter>): List<MeterReading> {
        val readings = mutableListOf<MeterReading>()
        meters.forEach { meter ->
            readings += getMeterReadings(meter)
        }
        return readings
    }

    fun convertUnitReadings(readings: List<MeterReading>, meterUnit: MeterUnit, finalMeterUnit: MeterUnit): List<MeterReading> {
        val readingsConverted = mutableListOf<MeterReading>()
        readings.forEach { reading ->
            val factor = getFactorUnitConversion(meterUnit, finalMeterUnit)
            val newMeterReading = MeterReading(reading.readingID, reading.meterID, factor * reading.value, reading.date, reading.note)
            readingsConverted.add(newMeterReading)
        }
        return readingsConverted
    }

    // TODO ( To Implement ! )
    private fun getFactorUnitConversion(meterUnit: MeterUnit, finalMeterUnit: MeterUnit): Float {
        if (meterUnit == MeterUnit.CENTIMETER) {
            if (finalMeterUnit == MeterUnit.CUBIC_METER) { return 1.3f }
            else if (finalMeterUnit == MeterUnit.LITER) { return 2.1f }
            else { Error("Bad Unit") }

        } else if (meterUnit == MeterUnit.CUBIC_METER) {
            if (finalMeterUnit == MeterUnit.CUBIC_METER) { return 3.32f }
            else if (finalMeterUnit == MeterUnit.LITER) { return 4.0f }
            else { Error("Bad Unit") }
        }
        return 1.0f
    }

    fun getMeterDetails(meter: Meter): String {
        return "Meter details: name = ${meter.meterName}, unit = ${meter.meterUnit}, icon = ${meter.meterIcon}, type = ${meter.meterType}, housingID = ${meter.housingID}, cost = ${meter.meterCost}, additive = ${meter.additiveMeter}"
    }

    fun isMeterReadingAboveThreshold(meterReading: MeterReading, threshold: Double): Boolean {
        return meterReading.value > threshold
    }

    fun deleteMeter(meter:Meter) {
        screenModelScope.launch {
            meterRepository.deleteMeter(meter)
        }
    }


//    fun filterMeterByUnit(unit: Unit): MutableList<Meter> {
//        return meters.filter { meter ->
//            meter.meterUnit == unit
//        }.toMutableList()
//    }
//
//    fun getTotalConsumption(): Double {
//        return meters.sumOf { meter ->
//            meter.meterCost
//        }
//    }
//
//    fun isMeterExists(name: String): Boolean {
//        return meters.any { meter ->
//            meter.meterName == name }
//    }
//
//    suspend fun getMeterReadingsForDateRange(startDate: Date, endDate: Date): List<MeterReading> {
//        return getAllMeterReadings().filter { meterReading ->
//            meterReading.date.after(startDate) and meterReading.date.before(endDate)
//        }
//    }
//
//    suspend fun getAllMeterReadings(): List<MeterReading> {
//        var allMeterReadingsList = mutableListOf<MeterReading>()
//        meters.forEach { meter ->
//            allMeterReadingsList += meterRepository.getMeterReadings(meter.meterID).last()
//        }
//        return allMeterReadingsList
//    }
//
//    fun sortMeterByName(): List<Meter> {
//        return meters.sortedBy { it.meterName }
//    }
}