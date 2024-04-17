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

    private val _state = mutableStateOf(MainPageState())
    val state: State<MainPageState> = _state
    val auth = Firebase.auth

    init {
        try {
            updateState()
        } catch (e: Throwable) {
            e.message?.let { Log.e("MainPageScreenModel", it) }
        }
    }

    private fun updateState() {
        viewModelScope.launch {
            authRepository.getUser().collect { firebaseUserResource ->
                _state.value.currentUser.emit(firebaseUserResource)
                when (firebaseUserResource) {
                    is Resource.Error -> {
                        Log.w("MainPageScreenModel", "No User")
                    }

                    is Resource.Loading -> {
                        Log.d("MainPageScreenModel", "Loading")
                    }

                    is Resource.Success -> {
                        Log.d("MainPageScreenModel", "User: ${firebaseUserResource.data.email}")
                        meterRepository.setHomeCollection(
                            firebaseUserResource.data.uid.hashCode().toString()
                        )
                        meterRepository.getHousing().collect { housingRessource ->
                            when (housingRessource) {
                                is Resource.Error -> {
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
//                                            val defaultHousing =
//                                                Housing(housingID = UUID.randomUUID().hashCode())
//                                            meterRepository.addHousing(defaultHousing)
//                                            meterRepository.addUserData(
//                                                User(
//                                                    firebaseUserResource.data.uid.hashCode(),
//                                                    firebaseUserResource.data.email!!,
//                                                    Currency.EUR
//                                                )
//                                            )
//                                            meterRepository.addUserToHousing(
//                                                defaultHousing,
//                                                User(
//                                                    firebaseUserResource.data.uid.hashCode(),
//                                                    firebaseUserResource.data.email!!,
//                                                    Currency.EUR
//                                                )
//                                            )
                                            Log.wtf("UpdateState", "No housings")
                                            _state.value = state.value.copy(
                                                selectedHousing = Resource.Error(NO_DATA),
                                                housings = emptyList()
                                            )
                                        }
                                    }

                                }

                                is Resource.Loading -> {
                                    Log.wtf("UpdateState", "Loading housings")
                                }

                                is Resource.Success -> {
                                    if (housingRessource.data.isNotEmpty()) {
                                        //TODO: store selected housing ID in localpref and if set use this housing by default
                                        val selectedHousing = when(state.value.selectedHousing){
                                            is Resource.Error -> housingRessource.data.first()
                                            is Resource.Loading -> housingRessource.data.first()
                                            is Resource.Success -> (state.value.selectedHousing as Resource.Success<Housing, DataException>).data
                                        }
                                        _state.value = state.value.copy(
                                                selectedHousing = Resource.Success(selectedHousing),
                                        housings = housingRessource.data
                                        )
                                        meterRepository.setHomeAndUser(
                                            selectedHousing,
                                            firebaseUserResource.data.uid.hashCode().toString()
                                        )
                                        meterRepository.getMeterAndReadings(housingRessource.data.first())
                                            .collect {
                                                _state.value = state.value.copy(
                                                    meters = it.keys.toList(),
                                                    lastReading = it.map { (meter, readings) ->
                                                        meter.meterID to readings
                                                    }.toMap()
                                                )
                                            }
                                    } else {
                                        Log.d("Housing", "No Housing")
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
            is Resource.Error -> TODO()
            is Resource.Loading -> TODO()
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

    fun filterMetersByType(type: MeterType): List<Meter> {
        val meters = state.value.meters
        return meters.filter { meter ->
            meter.meterType == type
        }.toList()
    }

    @Composable
    fun getMeterReadings(meter: Meter): List<MeterReading> {
        return meterRepository.getMeterReadings(meter.meterID)
            .collectAsState(initial = emptyList()).value
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
        finalMeterUnit: MeterUnit
    ): List<MeterReading> {
        val readingsConverted = mutableListOf<MeterReading>()
        readings.forEach { reading ->
            val factor = getFactorUnitConversion(meterUnit, finalMeterUnit)
            val newMeterReading = MeterReading(
                reading.readingID,
                reading.meterID,
                factor * reading.value,
                reading.date,
                reading.note
            )
            readingsConverted.add(newMeterReading)
        }
        return readingsConverted
    }

    private fun getFactorUnitConversion(meterUnit: MeterUnit, finalMeterUnit: MeterUnit): Float {
        if (meterUnit == MeterUnit.CENTIMETER) {
            when (finalMeterUnit) {
                MeterUnit.CUBIC_METER -> {
                    return 1.3f
                }
                MeterUnit.LITER -> {
                    return 2.1f
                }
                else -> {
                    Error("Bad Unit")
                }
            }

        } else if (meterUnit == MeterUnit.CUBIC_METER) {
            when (finalMeterUnit) {
                MeterUnit.CUBIC_METER -> {
                    return 3.32f
                }
                MeterUnit.LITER -> {
                    return 4.0f
                }
                else -> {
                    Error("Bad Unit")
                }
            }
        }
        return 1.0f
    }

    fun getMeterDetails(meter: Meter): String {
        return "Meter details: name = ${meter.meterName}, unit = ${meter.meterUnit}, icon = ${meter.meterIcon}, type = ${meter.meterType}, housingID = ${meter.housingID}, cost = ${meter.meterCost}, additive = ${meter.additiveMeter}"
    }

    fun isMeterReadingAboveThreshold(meterReading: MeterReading, threshold: Double): Boolean {
        return meterReading.value > threshold
    }

    fun deleteMeter(meter: Meter) {
        viewModelScope.launch {
            meterRepository.deleteMeter(meter)
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
                        Log.e("Main Page Screen Model Login Error", it.error.toString())
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
                            50,
                            2
                        )
                        meterRepository.addHousing(defaultHousing)
                        meterRepository.addUserToHousing(defaultHousing, userData)
                        Log.i("Register", "Logged in as ${it.data.user?.email}")
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
        Log.d("MainScreenModel", "Loggued out")
    }

    fun selectHousing(housing: Housing) {
        _state.value=_state.value.copy(
            selectedHousing = Resource.Success(housing)
        )
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