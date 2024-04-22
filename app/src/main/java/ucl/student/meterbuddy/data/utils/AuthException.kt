package ucl.student.meterbuddy.data.utils

enum class AuthException : MeterBuddyException {
    BAD_CREDENTIALS,
    NO_NETWORK,
    NO_CURRENT_USER,
    TO_MANY_ATTEMPT,
    EMAIL_ALREADY_TAKEN,
    EMAIL_BAD_FORMATTED,
    PASSWORD_TO0_SHORT,
    UNKNOWN_ERROR
}