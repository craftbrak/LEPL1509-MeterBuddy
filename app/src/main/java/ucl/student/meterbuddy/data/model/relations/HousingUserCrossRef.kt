package ucl.student.meterbuddy.data.model.relations

import androidx.room.Entity
import ucl.student.meterbuddy.data.model.enums.Role


@Entity(primaryKeys = ["housingID", "userID"])
data class HousingUserCrossRef (
    val housingID: Int,
    val userID: Int,
    val role: Role
)