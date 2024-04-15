package ucl.student.meterbuddy.data.model.enums

enum class Currency (val symbol: String, val currencyCode: String) {
    EUR("€", "EUR"),
    USD("$", "USD"),
    AUD("$", "AUD"),
    GBP("£", "GBP"),
    JPY("¥", "JPY"),
    CNY("¥", "CNY"),
}