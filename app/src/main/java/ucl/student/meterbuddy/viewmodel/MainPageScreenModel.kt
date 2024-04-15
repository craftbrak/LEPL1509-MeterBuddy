package ucl.student.meterbuddy.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import ucl.student.meterbuddy.data.UserDatabase
import ucl.student.meterbuddy.data.model.entity.Housing
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.entity.User
import ucl.student.meterbuddy.data.model.enums.Currency
import ucl.student.meterbuddy.data.model.enums.HousingType
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.MeterUnit
import ucl.student.meterbuddy.data.repository.AuthRepository
import ucl.student.meterbuddy.data.repository.LocalMeterRepository
import ucl.student.meterbuddy.data.repository.MeterRepository
import ucl.student.meterbuddy.data.utils.AuthException
import ucl.student.meterbuddy.data.utils.DataException
import ucl.student.meterbuddy.data.utils.DataException.*
import ucl.student.meterbuddy.data.utils.Resource
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MainPageScreenModel @Inject constructor(
    private val meterRepository: MeterRepository,
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = mutableStateOf(MainPageState())
    val state: State<MainPageState> = _state
    val auth = Firebase.auth

    init {
        updateState()
    }

    private fun updateState() {
        viewModelScope.launch {
            authRepository.getUser().collect {
                _state.value.currentUser.emit(it)
                when(it){
                    is Resource.Error -> {
                        Log.w("MainPageScreenModel", "No User")
                    }
                    is Resource.Loading -> {
                        Log.d("MainPageScreenModel", "Loading")
                    }
                    is Resource.Success -> {
                        Log.d("MainPageScreenModel", "User: ${it.data.email}")
                        meterRepository.getHousing().collect { housingRessource ->
                            when (housingRessource) {
                                is Resource.Error -> {
                                    when(housingRessource.error){
                                        NO_NETWORK -> TODO()
                                        BAD_REQUEST -> TODO()
                                        UNAUTHORIZED -> TODO()
                                        UNKNONW_ERROR -> TODO()
                                        NO_DATA -> {
                                            Log.d("Housing", "No Housing")
                                            _state.value = state.value.copy(
                                                selectedHousing = Resource.Error(NO_DATA)
                                            )
                                            state.value.currentUser.collect{
                                                when(it){
                                                    is Resource.Error -> {
                                                        Log.w("MainPageScreenModel", "No User")
                                                    }
                                                    is Resource.Loading -> {
                                                        Log.d("MainPageScreenModel", "Loading")
                                                    }
                                                    is Resource.Success -> {
                                                        val defaultHousing = Housing(housingID = UUID.randomUUID().hashCode())
                                                        meterRepository.addHousing(defaultHousing)
                                                        meterRepository.addUserData(User(it.data.uid.hashCode(), it.data.email!!, Currency.EUR))
                                                        meterRepository.addUserToHousing(defaultHousing, User(it.data.uid.hashCode(), it.data.email!!, Currency.EUR))
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }

                                is Resource.Loading -> {

                                }

                                is Resource.Success -> {
                                    if (housingRessource.data.isNotEmpty()) {
                                        _state.value = state.value.copy(
                                            selectedHousing = Resource.Success(housingRessource.data.first()),
                                            housings = housingRessource.data
                                        )
                                        meterRepository.getMeterAndReadings(housingRessource.data.first()).collect {
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
//        viewModelScope.launch {
//            meterRepository.getMeterAndReadings().collect { metersAndReadings ->
////                Log.i("Meters and Readings", metersAndReadings.keys.toString())
//                _state.value = state.value.copy(
//                    meters = metersAndReadings.keys.toList(),
//                    lastReading = metersAndReadings.map { (meter, readings) ->
//                        meter.meterID to readings
//                    }.toMap()
//                )
//            }
//
//
//        }
        }


    fun updateMeter(meter: Meter) {
        viewModelScope.launch {
            meterRepository.updateMeter(meter)
        }
    }

    fun addMeter(metre: Meter) {
        when(state.value.selectedHousing){
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
            if (finalMeterUnit == MeterUnit.CUBIC_METER) {
                return 1.3f
            } else if (finalMeterUnit == MeterUnit.LITER) {
                return 2.1f
            } else {
                Error("Bad Unit")
            }

        } else if (meterUnit == MeterUnit.CUBIC_METER) {
            if (finalMeterUnit == MeterUnit.CUBIC_METER) {
                return 3.32f
            } else if (finalMeterUnit == MeterUnit.LITER) {
                return 4.0f
            } else {
                Error("Bad Unit")
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
                        Log.w("Login", "Loading")
                        _state.value.currentUser.emit(Resource.Loading())
                    }

                    is Resource.Success -> {
                        _state.value.currentUser.emit(Resource.Success(it.data.user!!))
                        Log.i("Login", "Logged in as ${it.data.user?.email}")
                    }
                }
            }
        }
    }

    fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            authRepository.registerUser(email, password).collect {
                when (it) {
                    is Resource.Error -> {
                        Log.e("Login Error", it.error.toString())
                        _state.value.currentUser.emit(Resource.Error(it.error))
                    }

                    is Resource.Loading -> {
                        Log.w("Login", "Loading")
                    }

                    is Resource.Success -> {
                        _state.value.currentUser.emit(Resource.Success(it.data.user!!))
                        val userData = User(it.data.user!!.uid.hashCode(), email, Currency.EUR)
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
//        _state.value.currentUser.value = Resource.Error(AuthException.NO_CURRENT_USER)
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