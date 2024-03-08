package ucl.student.meterbuddy.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import ucl.student.meterbuddy.data.UserDatabase
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.Unit
import ucl.student.meterbuddy.data.repository.LocalMeterRepository


class MainPageScreenModel(context: Context): ScreenModel {

    private val meterRepository = LocalMeterRepository( UserDatabase.getInstance(context).userDao)
    private val _state = mutableStateOf(MainPageState())
    val state: State<MainPageState> = _state

    init { updateState() }

    private fun updateState() {
        screenModelScope.launch {
            meterRepository.getMeters().collect { meters ->
                Log.i("getMeter","Get meter Call $meters")
                _state.value = state.value.copy(
                    meters = meters
                )
                val readingJobs = meters.map{ meter ->
                    async(Dispatchers.IO) {
                        meter.meterID to getLastReadingOfMeter(meter)
                    }
                }
                val readingsMap = readingJobs.awaitAll().toMap()
                readingsMap.forEach { (id, reading) ->
                    Log.i("getMeterReading", "Get meterReading Call $reading")
                }
                _state.value = state.value.copy(
                    lastReading = readingsMap
                )
            }
        }
    }

    fun addMeter(metre:Meter) {
        screenModelScope.launch {
            meterRepository.addMeter(metre)
            Log.i("Add Meter", metre.toString())
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

    fun convertUnitReadings(readings: List<MeterReading>, unit: Unit, finalUnit: Unit): List<MeterReading> {
        val readingsConverted = mutableListOf<MeterReading>()
        readings.forEach { reading ->
            val factor = getFactorUnitConversion(unit, finalUnit)
            val newMeterReading = MeterReading(reading.readingID, reading.meterID, factor * reading.value, reading.date, reading.note)
            readingsConverted.add(newMeterReading)
        }
        return readingsConverted
    }

    // TODO ( To Implement ! )
    fun getFactorUnitConversion(unit: Unit, finalUnit: Unit): Float {
        if (unit == Unit.CENTIMETER) {
            if (finalUnit == Unit.CUBIC_METER) { return 1.3f }
            else if (finalUnit == Unit.LITER) { return 2.1f }
            else { Error("Bad Unit") }

        } else if (unit == Unit.CUBIC_METER) {
            if (finalUnit == Unit.CUBIC_METER) { return 3.32f }
            else if (finalUnit == Unit.LITER) { return 4.0f }
            else { Error("Bad Unit") }
        }
        return 0.0f
    }

    fun getMeterDetails(meter: Meter): String {
        return "Meter details: name = ${meter.meterName}, unit = ${meter.meterUnit}, icon = ${meter.meterIcon}, type = ${meter.meterType}, housingID = ${meter.housingID}, cost = ${meter.meterCost}, additive = ${meter.additiveMeter}"
    }

    fun isMeterReadingAboveThreshold(meterReading: MeterReading, threshold: Double): Boolean {
        return meterReading.value > threshold
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