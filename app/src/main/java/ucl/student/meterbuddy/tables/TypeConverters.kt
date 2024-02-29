package ucl.student.meterbuddy.tables

import androidx.room.TypeConverter
import ucl.student.meterbuddy.tables.entities.Currency
import ucl.student.meterbuddy.tables.entities.HousingType
import ucl.student.meterbuddy.tables.entities.MeterIcon
import ucl.student.meterbuddy.tables.entities.Role
import java.util.Date
import ucl.student.meterbuddy.tables.entities.Unit

class TypeConverters {
    // Converters for the HousingType enum
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

    // Converters for the Currency enum
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
    fun dateFromTimeStamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromUnit(unit: Unit): String {
        return unit.unit
    }
    @TypeConverter
    fun toUnit(unit: String): Unit {
        return when (unit) {
            "kWh" -> {
                Unit.KWH
            }
            "m3" -> {
                Unit.M3
            }
            "L" -> {
                Unit.L
            }
            "G" -> {
                Unit.G
            }
            "Gj" -> {
                Unit.GJ
            }
            "MW" -> {
                Unit.MWh
            }
            "h" -> {
                Unit.H
            }
            "cm" -> {
                Unit.CM
            }
            "Kg" -> {
                Unit.Kg
            }
            "St" -> {
                Unit.ST
            }
            "Mb" -> {
                Unit.MB
            }
            else -> {
                throw IllegalArgumentException("Could not recognize unit")
            }
        }
    }

    @TypeConverter
    fun fromMeterIcon(icon: MeterIcon): String {
        return icon.iconName
    }
    @TypeConverter
    fun toMeterIcon(icon: String): MeterIcon {
        return when (icon) {
            "Electricity" -> {
                MeterIcon.Electricity
            }
            "Gas" -> {
                MeterIcon.Gas
            }
            "Water" -> {
                MeterIcon.Water
            }
            "Heating" -> {
                MeterIcon.Heating
            }
            "Other" -> {
                MeterIcon.Other
            }
            else -> {
                throw IllegalArgumentException("Could not recognize icon")
            }
        }
    }

    @TypeConverter
    fun fromRole(role: Role): String {
        return role.role
    }

    @TypeConverter
    fun toRole(role: String): Role {
        return when (role) {
            "Admin" -> {
                Role.ADMIN
            }
            "Member" -> {
                Role.Member
            }
            "Viewer" -> {
                Role.Viewer
            }
            else -> {
                throw IllegalArgumentException("Could not recognize role")
            }
        }
    }

}