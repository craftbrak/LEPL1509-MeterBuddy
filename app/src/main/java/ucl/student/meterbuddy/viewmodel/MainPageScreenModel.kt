package ucl.student.meterbuddy.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import ucl.student.meterbuddy.data.model.entity.Housing
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.entity.User
import ucl.student.meterbuddy.data.model.enums.Currency
import ucl.student.meterbuddy.data.model.enums.HousingType
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.MeterUnit
import ucl.student.meterbuddy.data.repository.AuthRepository
import ucl.student.meterbuddy.data.repository.MeterRepository
import ucl.student.meterbuddy.data.utils.AuthException
import ucl.student.meterbuddy.data.utils.DataException
import ucl.student.meterbuddy.data.utils.DataException.BAD_REQUEST
import ucl.student.meterbuddy.data.utils.DataException.NO_DATA
import ucl.student.meterbuddy.data.utils.DataException.NO_NETWORK
import ucl.student.meterbuddy.data.utils.DataException.UNAUTHORIZED
import ucl.student.meterbuddy.data.utils.DataException.UNKNONW_ERROR
import ucl.student.meterbuddy.data.utils.Resource
import javax.inject.Inject

@HiltViewModel
class MainPageScreenModel @Inject constructor(
    private val meterRepository: MeterRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val tag = "MainPageScreenModel: "
    private val _state = mutableStateOf(MainPageState())
    val state: State<MainPageState> = _state
    val auth = Firebase.auth

    init {
        try {
            updateState()
        } catch (e: Throwable) {
            e.message?.let { Log.e(tag + "update", it) }
        }
    }

    private fun updateState() {
        viewModelScope.launch {
            authRepository.getUser().collect { firebaseUserResource ->
                _state.value.currentUser.emit(firebaseUserResource)
                when (firebaseUserResource) {
                    is Resource.Error -> {
                        Log.w(tag, "No User")
                    }

                    is Resource.Loading -> {
                        Log.d(tag, "Loading")
                    }

                    is Resource.Success -> {
                        Log.d(tag, "User: ${firebaseUserResource.data.email}")
                        meterRepository.setHomeCollection(
                            firebaseUserResource.data.uid.hashCode().toString()
                        )
                        viewModelScope.launch {
                            val u = meterRepository.getUser(
                                firebaseUserResource.data.uid.hashCode().toString()
                            )
                            _state.value = _state.value.copy(
                                currentUserData = when (u) {
                                    is Resource.Error, is Resource.Loading -> {
                                        Log.e(tag + "User Data", "No custom user data retrieved")
                                        null
                                    }

                                    is Resource.Success -> {
                                        Log.d(tag + "User Data", u.data.toString())
                                        u.data
                                    }
                                }
                            )

                        }
                        meterRepository.getHousing().collect { housingRessource ->
                            when (housingRessource) {
                                is Resource.Error -> {
                                    _state.value = _state.value.copy(
                                        housings = emptyList()
                                    )
                                    when (housingRessource.error) {

                                        NO_NETWORK -> TODO()
                                        BAD_REQUEST -> TODO()
                                        UNAUTHORIZED -> TODO()
                                        UNKNONW_ERROR -> TODO()
                                        NO_DATA -> { //TODO make onboarding process
//                                            Log.d("Housing", "No Housing")
//                                            _state.value = state.value.copy(
//                                                selectedHousing = Resource.Error(NO_DATA)
//                                            )
                                            saveHousing(
                                                Housing()
                                            )
                                            Log.wtf(tag + "UpdateState", "No housings")
                                            _state.value = state.value.copy(
                                                selectedHousing = Resource.Error(NO_DATA),
                                                housings = emptyList()
                                            )
                                        }
                                    }

                                }

                                is Resource.Loading -> {
                                    Log.wtf(tag + "UpdateState", "Loading housings")
                                    _state.value = _state.value.copy(
                                        housings = emptyList()
                                    )
                                }

                                is Resource.Success -> {
                                    _state.value = _state.value.copy(
                                        housings = emptyList()
                                    )
                                    if (housingRessource.data.isNotEmpty()) {
                                        //TODO: store selected housing ID in localpref and if set use this housing by default
                                        val selectedHousing = when (state.value.selectedHousing) {
                                            is Resource.Error -> housingRessource.data.first()
                                            is Resource.Loading -> housingRessource.data.first()
                                            is Resource.Success -> (state.value.selectedHousing as Resource.Success<Housing, DataException>).data
                                        }
                                        val housingUsers = when (val users =
                                            meterRepository.getHousingMember(selectedHousing)) {
                                            is Resource.Error, is Resource.Loading -> emptyList()
                                            is Resource.Success -> {
                                                users.data
                                            }
                                        }
                                        _state.value = state.value.copy(
                                            selectedHousing = Resource.Success(selectedHousing),
                                            housings = housingRessource.data,
                                            housingUsers = housingUsers
                                        )
                                        meterRepository.setHomeAndUser(
                                            selectedHousing,
                                            firebaseUserResource.data.uid.hashCode().toString()
                                        )
                                        viewModelScope.launch {
                                            meterRepository.getUsersResource().collect {
                                                when (it) {
                                                    is Resource.Error -> {
                                                        Log.e(tag + "GetUser", it.error.toString())
                                                    }

                                                    is Resource.Loading -> {

                                                    }

                                                    is Resource.Success -> {
                                                        Log.d(tag + "GetUsers", it.data.toString())
                                                        _state.value = _state.value.copy(
                                                            users = it.data
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        viewModelScope.launch {
                                            meterRepository.getMeterAndReadings(selectedHousing)
                                                .collect {
                                                    Log.wtf(tag + "Readings", it.toString())
                                                    _state.value = state.value.copy(
                                                        meters = it.keys.toList(),
                                                        lastReading = it.map { (meter, readings) ->
                                                            meter.meterID to readings
                                                        }.toMap()
                                                    )
                                                }
                                        }
                                    } else {
                                        Log.d(tag + "Housing", "No Housing")
                                        _state.value = state.value.copy(
                                            selectedHousing = Resource.Error(NO_DATA)
                                        )
                                    }
                                }

                            }
                        }
                    }
                }
            }

        }
        viewModelScope.launch {
            authRepository.getUser().collect {
                _state.value.currentUser.emit(it)  //Don't ask me why this is duplicated but it work and it does not if removed
            }
        }
    }


    fun updateMeter(meter: Meter) {
        viewModelScope.launch {
            meterRepository.updateMeter(meter)
        }
    }

    fun addMeter(metre: Meter) {
        when (state.value.selectedHousing) {
            is Resource.Error -> {}
            is Resource.Loading -> {}
            is Resource.Success -> {
                val meter = metre.copy(
                    housingID = (state.value.selectedHousing as Resource.Success<Housing, DataException>).data.housingID
                )
                viewModelScope.launch {
                    meterRepository.addMeter(meter)
                }
            }
        }

    }

    private suspend fun getLastReadingOfMeter(meter: Meter): MeterReading {
        return meterRepository.getMeterReadings(meter.meterID).last().last()
    }

    @Composable
    fun filterMetersByType(type: MeterType): List<Meter> {
        return meterRepository.getMeters()
            .collectAsState(initial = emptyList()).value.filter { meter -> meter.meterType == type }
    }

    @Composable
    fun getMeterReadings(meter: Meter): List<MeterReading> {
        return state.value.lastReading[meter.meterID] ?: emptyList()
    }

    @Composable
    fun getMetersReadings(meters: List<Meter>): List<MeterReading> {
        val readings = mutableListOf<MeterReading>()
        meters.forEach { meter ->
            readings += getMeterReadings(meter)
        }
        return readings
    }

    fun convertUnitReadings(
        readings: List<MeterReading>,
        meterUnit: MeterUnit,
        finalMeterUnit: MeterUnit,
        typeMeter: MeterType
    ): List<MeterReading> {
        val readingsConverted = mutableListOf<MeterReading>()
        readings.forEach { reading ->
            val factor = getFactorUnitConversion(meterUnit, finalMeterUnit, typeMeter)
            val newMeterReading = MeterReading(
                reading.readingID,
                reading.meterID,
                reading.value * factor,
                reading.date,
                reading.note
            )
            readingsConverted.add(newMeterReading)
        }
        return readingsConverted
    }

    @Throws(Error::class)
    private fun getFactorUnitConversion(meterUnit: MeterUnit, finalMeterUnit: MeterUnit, typeMeter: MeterType): Float {
        println(typeMeter)
        return when (typeMeter) {
            MeterType.WATER, MeterType.HOT_WATER -> {
                when (meterUnit) {
                    MeterUnit.CUBIC_METER -> {
                        when (finalMeterUnit) {
                            MeterUnit.CUBIC_METER -> 1.0f
                            MeterUnit.LITER -> 1000.0f
                            else -> throw Error("Bad Unit")
                        }
                    }

                    else -> {
                        when (finalMeterUnit) {
                            MeterUnit.CUBIC_METER -> 0.001f
                            MeterUnit.LITER -> 1.0f
                            else -> throw Error("Bad Unit")
                        }
                    }
                }
            }

            MeterType.CAR -> {
                val factor_kWh_per_km = 0.15f
                val factor_kWh_per_liter = 10.0f
                when (meterUnit) {
                    MeterUnit.KILO_WATT_HOUR -> {
                        when (finalMeterUnit) {
                            MeterUnit.KILO_WATT_HOUR -> 1.0f
                            MeterUnit.KILO_METER -> 1 / factor_kWh_per_km
                            MeterUnit.LITER -> 1 / factor_kWh_per_liter
                            else -> throw Error("Bad Unit")
                        }
                    }

                    MeterUnit.KILO_METER -> {
                        when (finalMeterUnit) {
                            MeterUnit.KILO_WATT_HOUR -> factor_kWh_per_km
                            MeterUnit.KILO_METER -> 1.0f
                            MeterUnit.LITER -> factor_kWh_per_km / factor_kWh_per_liter
                            else -> throw Error("Bad Unit")
                        }
                    }

                    else -> {
                        when (finalMeterUnit) {
                            MeterUnit.KILO_WATT_HOUR -> factor_kWh_per_liter
                            MeterUnit.KILO_METER -> factor_kWh_per_liter / factor_kWh_per_km
                            MeterUnit.LITER -> 1.0f
                            else -> throw Error("Bad Unit")
                        }
                    }
                }
            }

            MeterType.GAS -> {
                val factor_kWh_per_cubic_meter = 8.9f
                when (meterUnit) {
                    MeterUnit.KILO_WATT_HOUR -> {
                        when (finalMeterUnit) {
                            MeterUnit.KILO_WATT_HOUR -> 1.0f
                            MeterUnit.CUBIC_METER -> factor_kWh_per_cubic_meter
                            MeterUnit.LITER -> factor_kWh_per_cubic_meter / 1000
                            else -> throw Error("Bad Unit")
                        }
                    }

                    MeterUnit.CUBIC_METER -> {
                        when (finalMeterUnit) {
                            MeterUnit.KILO_WATT_HOUR -> 1 / factor_kWh_per_cubic_meter
                            MeterUnit.CUBIC_METER -> 1.0f
                            MeterUnit.LITER -> 1000.0f
                            else -> throw Error("Bad Unit")
                        }
                    }

                    else -> {
                        when (finalMeterUnit) {
                            MeterUnit.KILO_WATT_HOUR -> 1000 / factor_kWh_per_cubic_meter
                            MeterUnit.CUBIC_METER -> 0.001f
                            MeterUnit.LITER -> 1.0f
                            else -> throw Error("Bad Unit")
                        }
                    }
                }
            }

            else -> 1.0f // Trop complexe
        }
    }

    fun getMeterDetails(meter: Meter): String {
        return "Meter details: name = ${meter.meterName}, unit = ${meter.meterUnit}, icon = ${meter.meterIcon}, type = ${meter.meterType}, housingID = ${meter.housingID}, cost = ${meter.meterCost}, additive = ${meter.additiveMeter}"
    }

    fun isMeterReadingAboveThreshold(meterReading: MeterReading, threshold: Double): Boolean {
        return meterReading.value > threshold
    }

    fun getUsers(): List<User> {
        return when (val users = meterRepository.getUsers()) {
            is Resource.Error, is Resource.Loading -> emptyList()
            is Resource.Success -> {
                users.data
            }
        }
    }

    fun deleteMeter(meter: Meter) {
        viewModelScope.launch {
            meterRepository.deleteMeter(meter)
        }
    }
    fun deleteHousing(housing: Housing, user:User){
        viewModelScope.launch {
            meterRepository.removeUserFromHousing(housing, user)
            meterRepository.deleteHousing(housing)
            selectHousing(state.value.housings.filter { h -> h != housing }.get(0))
            updateState()
        }
    }
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            authRepository.loginUser(email, password).collect {
                when (it) {
                    is Resource.Error -> {
                        Log.e("Login Error", it.error.toString())
                        _state.value.currentUser.emit(Resource.Error(it.error))
                    }

                    is Resource.Loading -> {
                        _state.value.currentUser.value = Resource.Loading()
                    }

                    is Resource.Success -> {
                        _state.value.currentUser.emit(Resource.Success(it.data.user!!))
                        Log.i("Login", "Logged in as ${it.data.user?.email}")
                    }
                }
            }
        }
    }

    fun registerUser(
        email: String,
        password: String,
        username: String,
        selectedCurrency: Currency
    ) {
        viewModelScope.launch {
            authRepository.registerUser(email, password).collect {
                when (it) {
                    is Resource.Error -> {
                        Log.e(tag + "Login", it.error.toString())
                        _state.value.currentUser.emit(Resource.Error(it.error))
                    }

                    is Resource.Loading -> {
                        _state.value.currentUser.value = Resource.Loading()
                    }

                    is Resource.Success -> {
                        _state.value.currentUser.emit(Resource.Success(it.data.user!!))
                        val userData =
                            User(it.data.user!!.uid.hashCode(), username, selectedCurrency)
                        meterRepository.addUserData(userData)
                        val defaultHousing = Housing(
                            0,
                            "My Home",
                            HousingType.House,
                            50f,
                            2
                        )
                        meterRepository.addHousing(defaultHousing, userData)
                        Log.i(tag + "Register", "Logged in as ${it.data.user?.email}")
                    }
                }
            }
        }

    }

    fun logout() {
        authRepository.logout()
        viewModelScope.launch {
            state.value.currentUser.emit(Resource.Error(AuthException.NO_CURRENT_USER))
        }
        Log.d(tag + "Logout", "Loggued out")
    }

    fun selectHousing(housing: Housing) {
        _state.value = _state.value.copy(
            selectedHousing = Resource.Success(housing),
            meters = emptyList()
        )
        updateState()
    }

    fun saveHousing(housing: Housing) {
        // Try-Catch when the user is new and the housing is therefore 'null'
        try {
            if (housing.housingID == 0) {
                meterRepository.addHousing(housing, state.value.currentUserData!!)
                updateState()
            } else {
                meterRepository.updateHousing(housing)
                updateState()
                selectHousing(housing)
            }
        } catch(e: NullPointerException) { }
    }

    fun deleteUserFromHousing(user: User, value: Housing) {
        meterRepository.removeUserFromHousing(value, user)
        updateState()
    }

    fun addUserToHousing(user: User, value: Housing) {
        meterRepository.addUserToHousing(value, user)
        updateState()
    }
//    fun filterMeterByUnit(unit: Unit): MutableList<Meter> {
//        return meters.filter { meter ->
//            meter.meterUnit == unit
//        }.toMutableList()
//    }
//
//    fun getTotalConsumption(): Double {
//        return meters.sumOf { meter ->
//            meter.meterCost
//        }
//    }
//
//    fun isMeterExists(name: String): Boolean {
//        return meters.any { meter ->
//            meter.meterName == name }
//    }
//
//    suspend fun getMeterReadingsForDateRange(startDate: Date, endDate: Date): List<MeterReading> {
//        return getAllMeterReadings().filter { meterReading ->
//            meterReading.date.after(startDate) and meterReading.date.before(endDate)
//        }
//    }
//
//    suspend fun getAllMeterReadings(): List<MeterReading> {
//        var allMeterReadingsList = mutableListOf<MeterReading>()
//        meters.forEach { meter ->
//            allMeterReadingsList += meterRepository.getMeterReadings(meter.meterID).last()
//        }
//        return allMeterReadingsList
//    }
//
//    fun sortMeterByName(): List<Meter> {
//        return meters.sortedBy { it.meterName }
//    }
}