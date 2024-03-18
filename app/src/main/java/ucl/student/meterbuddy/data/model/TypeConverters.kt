package ucl.student.meterbuddy.data.model

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.Currency
import ucl.student.meterbuddy.data.model.enums.HousingType
import ucl.student.meterbuddy.data.model.enums.MeterIcon
import ucl.student.meterbuddy.data.model.enums.Role
import ucl.student.meterbuddy.data.model.enums.MeterUnit
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date


class TypeConverters {

    @TypeConverter
    fun fromHousingType(housingType: HousingType): String {
        return housingType.type
    }

    @TypeConverter
    fun toHousingType(housingType: String): HousingType {
        return when (housingType) {
            "House" -> HousingType.House
            "Flat" -> HousingType.Flat
            "Bungalow" -> HousingType.Bungalow
            "Other" -> HousingType.Other
            else -> throw IllegalArgumentException("Could not recognize housing type")
        }
    }

    @TypeConverter
    fun fromCurrency(currency: Currency): String {
        return currency.symbol
    }

    @TypeConverter
    fun toCurrency(currency: String): Currency {
        return when (currency) {
            "€" -> Currency.EUR
            "$" -> Currency.USD
            "£" -> Currency.GBP
            else -> throw IllegalArgumentException("Could not recognize currency")
        }
    }

    @TypeConverter
    fun dateFromLong(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneId.systemDefault()) }
    }

    @TypeConverter
    fun dateToLong(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }

    @TypeConverter
    fun fromUnit(meterUnit: MeterUnit): String {
        return meterUnit.unit
    }

    @TypeConverter
    fun toUnit(unit: String): MeterUnit {
        return when (unit) {
            "Kilowatt Hour" -> MeterUnit.KILO_WATT_HOUR
            "Cubic Meter" -> MeterUnit.CUBIC_METER
            "Liter" -> MeterUnit.LITER
            "Gallon" -> MeterUnit.GALLON
            "Gigajoule" -> MeterUnit.GIGA_JOULE
            "Megawatt" -> MeterUnit.MEGA_WATT_HOUR
            "Hour" -> MeterUnit.HOUR
            "Centimeter" -> MeterUnit.CENTIMETER
            "Kilogram" -> MeterUnit.KILOGRAM
            "Stair" -> MeterUnit.STAIR
            "Megabyte" -> MeterUnit.MEGABYTE
            "Kilometer" -> MeterUnit.KILO_METER
            else -> throw IllegalArgumentException("Could not recognize unit $unit")
        }
    }

    @TypeConverter
    fun fromMeterIcon(icon: MeterIcon): String {
        return icon.iconName
    }

    @TypeConverter
    fun toMeterIcon(icon: String): MeterIcon {

        return when (icon) {
            "Electricity" -> MeterIcon.Electricity
            "Gas" -> MeterIcon.Gas
            "Water" -> MeterIcon.Water
            "Heating" -> MeterIcon.Heating
            "Other" -> MeterIcon.Other
            "Car" -> MeterIcon.Car
            "Hot Water" ->MeterIcon.HotWater
            else -> throw IllegalArgumentException("Could not recognize icon")
        }
    }

    @TypeConverter
    fun fromRole(role: Role): String {
        return role.role
    }

    @TypeConverter
    fun toRole(role: String): Role {
        return when (role) {
            "Admin" -> Role.ADMIN
            "Member" -> Role.Member
            "Viewer" -> Role.Viewer
            else -> throw IllegalArgumentException("Could not recognize role")
        }
    }
    @TypeConverter
    fun fromMeterType(meterType: MeterType): String {
        return meterType.type
    }

    @TypeConverter
    fun toMeterType(meterType: String): MeterType {
        return when(meterType) {
            "Electricity" -> MeterType.ELECTRICITY
            "Gas" -> MeterType.GAS
            "Water" -> MeterType.WATER
            "Car" -> MeterType.CAR
            "Hot Water" -> MeterType.HOT_WATER
            else -> throw IllegalArgumentException("Could not recognise MeterType")
        }
    }

    fun dateToTimestamp(date: LocalDateTime): Timestamp {
        return Timestamp(Date.from(date.toInstant(ZoneId.systemDefault().rules.getOffset(Instant.now()))))
    }

    fun timestampToDate(timestamp: Timestamp): LocalDateTime {
        return timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    }
    fun meterReadingToMap(reading: MeterReading): Map<String, *> {
        return mapOf(
            "readingID" to reading.readingID,
            "meterID" to reading.meterID,
            "value" to reading.value,
            "date" to TypeConverters().dateToTimestamp(reading.date),
            "note" to reading.note
        )
    }

    fun mapToMeterReading(map: Map<String, Any>): MeterReading {
        val date = map["date"]
        val timestamp = if (date is Timestamp) {
            timestampToDate(date)
        } else {
            // Handle the case where date is not a Timestamp
            // For example, you can use a default value or throw an exception
            LocalDateTime.now()
        }
        return MeterReading(
            readingID = (map["readingID"] as Long).toInt(),
            meterID = (map["meterID"] as Long).toInt(),
            value = (map["value"] as Double).toFloat(),
            date = timestamp,
            note = map["note"] as String?
        )
    }
}