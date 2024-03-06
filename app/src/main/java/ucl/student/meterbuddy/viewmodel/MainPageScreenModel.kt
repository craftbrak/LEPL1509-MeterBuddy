package ucl.student.meterbuddy.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import ucl.student.meterbuddy.data.UserDatabase
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.repository.LocalMeterRepository


class MainPageScreenModel(context: Context): ScreenModel {

    private val meterRepository = LocalMeterRepository( UserDatabase.getInstance(context).userDao)
    private val _state = mutableStateOf(MainPageState())
    val state: State<MainPageState> = _state

    init { getAllMeters() }

    fun addMeter(metre:Meter) {
        screenModelScope.launch {
            meterRepository.addMeter(metre)
            Log.i("Add Meter", metre.toString())
        }
    }

    fun getAllMeters() {
        screenModelScope.launch {
            meterRepository.getMeters().collect {
                Log.i("getMeter","Get meter Call $it")
                it.toMutableList()
                _state.value = state.value.copy(
                    listMeter = it
                )
            }
        }

    }

    suspend fun getLastReadingOfMeter(meter: Meter): MeterReading {
        return meterRepository.getMeterReadings(meter.meterID).last().last()
    }

//    suspend fun getLastReadingOfEachMeter(meter: Meter): MutableList<MeterReading> {
//        val lastReadings = mutableListOf<MeterReading>()
//        return meterRepository.getMeterReadings(meter.meterID).last()
//        meters.collect { meter ->
//            lastReadings.add(getLastReadingOfMeter(meter))
//        }
//        return lastReadings
//    }
//    fun filterMetersByType(type: MeterType): MutableList<Meter> {
//        return meters.filter { meter ->
//            meter.meterType == type
//        }.toMutableList()
//    }
//
//    fun filterMeterByUnit(unit: Unit): MutableList<Meter> {
//        return meters.filter { meter ->
//            meter.meterUnit == unit
//        }.toMutableList()
//    }

    fun getMeterDetails(meter: Meter): String {
        return "Meter details: name = ${meter.meterName}, unit = ${meter.meterUnit}, icon = ${meter.meterIcon}, type = ${meter.meterType}, housingID = ${meter.housingID}, cost = ${meter.meterCost}, additive = ${meter.additiveMeter}"
    }

//    fun getTotalConsumption(): Double {
//        return meters.sumOf { meter ->
//            meter.meterCost
//        }
//    }
//
//    fun removeMeter(meter: Meter) {
//        meters.remove(meter)
//    }
//
//    fun isMeterExists(name: String): Boolean {
//        return meters.any { meter ->
//            meter.meterName == name }
//    }

//    suspend fun getMeterReadingsForDateRange(startDate: Date, endDate: Date): List<MeterReading> {
//        return getAllMeterReadings().filter { meterReading ->
//            meterReading.date.after(startDate) and meterReading.date.before(endDate)
//        }
//    }

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
//
//    fun getTotalMeterCount(): Int {
//        return meters.size
//    }

    fun isMeterReadingAboveThreshold(meterReading: MeterReading, threshold: Double): Boolean {
        return meterReading.value > threshold
    }

    fun addMeterReading(meter: Meter, reading: MeterReading) {
        // TODO(not implemented yet)
    }

    fun editMeter(meter: Meter) {
        // TODO(not implemented yet)
    }
}