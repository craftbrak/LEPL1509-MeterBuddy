package ucl.student.meterbuddy.data.model

import androidx.room.TypeConverter
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.Currency
import ucl.student.meterbuddy.data.model.enums.HousingType
import ucl.student.meterbuddy.data.model.enums.MeterIcon
import ucl.student.meterbuddy.data.model.enums.Role
import ucl.student.meterbuddy.data.model.enums.Unit
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


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
    fun dateFromTimeStamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneId.systemDefault()) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }

    @TypeConverter
    fun fromUnit(unit: Unit): String {
        return unit.unit
    }

    @TypeConverter
    fun toUnit(unit: String): Unit {
        return when (unit) {
            "Kilowatt Hour" -> Unit.KILO_WATT_HOUR
            "Cubic Meter" -> Unit.CUBIC_METER
            "Liter" -> Unit.LITER
            "Gallon" -> Unit.GALLON
            "Gigajoule" -> Unit.GIGA_JOULE
            "Megawatt" -> Unit.MEGA_WATT_HOUR
            "Hour" -> Unit.HOUR
            "Centimeter" -> Unit.CENTIMETER
            "Kilogram" -> Unit.KILOGRAM
            "Stair" -> Unit.STAIR
            "Megabyte" -> Unit.MEGABYTE
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
}