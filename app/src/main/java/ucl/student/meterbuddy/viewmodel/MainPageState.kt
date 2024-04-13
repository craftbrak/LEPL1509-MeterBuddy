package ucl.student.meterbuddy.viewmodel

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.utils.AuthException
import ucl.student.meterbuddy.data.utils.Resource

data class MainPageState (
    val meters: List<Meter> = emptyList(),
    val lastReading: Map<Int, List<MeterReading>> = emptyMap(),
    val currentUser : MutableStateFlow<Resource<FirebaseUser, AuthException>> = MutableStateFlow(Resource.Loading())
)