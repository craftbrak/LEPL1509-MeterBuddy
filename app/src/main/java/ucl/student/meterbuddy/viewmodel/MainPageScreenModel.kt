package ucl.student.meterbuddy.viewmodel

import android.content.Context
import cafe.adriel.voyager.core.model.ScreenModel
import ucl.student.meterbuddy.data.UserDatabase
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.enums.MeterIcon
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.Unit

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
        return userDao.getMeterReadingFormMeterID(meter.meterID).last()
    }

    suspend fun getLastReadingOfEachMeter(meter: Meter): MutableList<MeterReading> {
        val lastReadings = mutableListOf<MeterReading>()
        listMeter.forEach { meter ->
            lastReadings.add(getLastReadingOfMeter(meter))
        }
        return lastReadings
    }

    // Edit What ?
    fun editMeter(meter: Meter) {
        // TODO
    }
}