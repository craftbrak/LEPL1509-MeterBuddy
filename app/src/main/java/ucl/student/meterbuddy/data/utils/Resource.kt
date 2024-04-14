package ucl.student.meterbuddy.data.utils

typealias RootError = MeterBuddyException
sealed interface Resource<out D, out E:RootError> {
    data class Success<out D , out  E: RootError>(val data: D) : Resource<D,E>
    data class Error<D,E : RootError>(val error: E) : Resource<D,E>
    data class Loading<D,E : RootError>(val loading: Boolean = false) : Resource<D,     E>
}
