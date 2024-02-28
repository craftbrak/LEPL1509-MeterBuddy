package ucl.student.meterbuddy.tables.entities.relations


import androidx.room.Entity
import ucl.student.meterbuddy.tables.entities.Role


@Entity(primaryKeys = ["housingID", "userID"])
data class HousingUserCrossRef (
    val housingID: Int,
    val userID: Int,
    val role: Role
)