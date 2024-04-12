package ucl.student.meterbuddy.data.utils

enum class AuthException : MeterBuddyException {
    BAD_CREDENTIALS,
    NO_NETWORK,
    UNKNOWN_ERROR,
    NO_CURRENT_USER
}