package ucl.student.meterbuddy.viewmodel

data class AuthState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""
)
