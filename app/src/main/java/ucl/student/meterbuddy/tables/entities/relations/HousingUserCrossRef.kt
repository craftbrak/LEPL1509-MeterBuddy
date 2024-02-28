package ucl.student.meterbuddy.tables.entities.relations

import androidx.compose.ui.semantics.Role
import androidx.room.Entity

enum class Role {
    // TODO
}

@Entity(primaryKeys = ["housingID", "userID"])
data class HousingUserCrossRef (
    val housingID: Int,
    val userID: Int,
    val role: Role
)