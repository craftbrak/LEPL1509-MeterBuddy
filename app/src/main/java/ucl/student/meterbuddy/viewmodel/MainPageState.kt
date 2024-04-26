package ucl.student.meterbuddy.viewmodel

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import ucl.student.meterbuddy.data.model.entity.Housing
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.entity.User
import ucl.student.meterbuddy.data.utils.AuthException
import ucl.student.meterbuddy.data.utils.DataException
import ucl.student.meterbuddy.data.utils.Resource

data class MainPageState (
    val meters: List<Meter> = emptyList(),
    val housings: List<Housing> = emptyList(),
    val users: List<User> = emptyList(),
    val lastReading: Map<Int, List<MeterReading>> = emptyMap(),
    val selectedHousing: Resource<Housing,DataException> = Resource.Loading(),
    val currentUser : MutableStateFlow<Resource<FirebaseUser, AuthException>> = MutableStateFlow(Resource.Loading()),
    val currentUserData: User? = null,
    val housingUsers: List<User> = emptyList()
)