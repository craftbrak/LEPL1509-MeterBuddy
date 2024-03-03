package ucl.student.meterbuddy.viewmodel

import android.content.Context
import cafe.adriel.voyager.core.model.ScreenModel
import ucl.student.meterbuddy.data.UserDatabase
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.enums.MeterIcon
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.Unit
import java.util.Date

class MainPageScreenModel(context: Context): ScreenModel {

    private val userDao = UserDatabase.getInstance(context).userDao

    private var listMeter = mutableListOf(
        Meter(1, "Electricity", Unit.KILO_WATT_HOUR, MeterIcon.Electricity, MeterType.ELECTRICITY, 1, 4.3, true),
        Meter(2, "Gas", Unit.CUBIC_METER, MeterIcon.Gas, MeterType.GAS, 1, 7.3, false)
    )

    fun addMeter(id: Int, name: String, unit: Unit, icon: MeterIcon, type: MeterType, housingID: Int, cost: Double, additive: Boolean) {
        listMeter.add(Meter(id, name, unit, icon, type, housingID, cost, additive))
    }

    fun listAllMeters(): MutableList<Meter> {
        return listMeter
    }

    suspend fun getLastReadingOfMeter(meter: Meter): MeterReading {
        return userDao.getMeterReadingFromMeterID(meter.meterID).last()
    }

    suspend fun getLastReadingOfEachMeter(meter: Meter): MutableList<MeterReading> {
        val lastReadings = mutableListOf<MeterReading>()
        listMeter.forEach { meter ->
            lastReadings.add(getLastReadingOfMeter(meter))
        }
        return lastReadings
    }

    fun filterMetersByType(type: MeterType): MutableList<Meter> {
        return listMeter.filter { meter ->
            meter.meterType == type
        }.toMutableList()
    }

    fun filterMeterByUnit(unit: Unit): MutableList<Meter> {
        return listMeter.filter { meter ->
            meter.meterUnit == unit
        }.toMutableList()
    }

    fun getMeterDetails(meter: Meter): String {
        return "Meter details: name = ${meter.meterName}, unit = ${meter.meterUnit}, icon = ${meter.meterIcon}, type = ${meter.meterType}, housingID = ${meter.housingID}, cost = ${meter.meterCost}, additive = ${meter.additiveMeter}"
    }

    fun getTotalConsumption(): Double {
        return listMeter.sumOf { meter ->
            meter.meterCost
        }
    }

    fun removeMeter(meter: Meter) {
        listMeter.remove(meter)
    }

    fun isMeterExists(name: String): Boolean {
        return listMeter.any { meter ->
            meter.meterName == name }
    }

    suspend fun getMeterReadingsForDateRange(startDate: Date, endDate: Date): List<MeterReading> {
        return getAllMeterReadings().filter { meterReading ->
            meterReading.date.after(startDate) and meterReading.date.before(endDate)
        }
    }

    suspend fun getAllMeterReadings(): List<MeterReading> {
        var allMeterReadingsList = mutableListOf<MeterReading>()
        listMeter.forEach { meter ->
            allMeterReadingsList += userDao.getMeterReadingFromMeterID(meter.meterID)
        }
        return allMeterReadingsList
    }

    fun sortMeterByName(): List<Meter> {
        return listMeter.sortedBy { it.meterName }
    }

    fun getTotalMeterCount(): Int {
        return listMeter.size
    }

    suspend fun isMeterReadingAboveThreshold(meterReading: MeterReading, threshold: Double): Boolean {
        return meterReading.value > threshold
    }

    // How add a MeterReading
    fun addMeterReading(meter: Meter, reading: MeterReading) {
        // TODO
    }

    // Edit What ?
    fun editMeter(meter: Meter) {
        // TODO
    }
}