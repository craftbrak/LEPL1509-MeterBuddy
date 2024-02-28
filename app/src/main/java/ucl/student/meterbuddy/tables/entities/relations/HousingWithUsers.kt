package ucl.student.meterbuddy.tables.entities.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import ucl.student.meterbuddy.tables.entities.Housing
import ucl.student.meterbuddy.tables.entities.User

data class HousingWithUsers (
    @Embedded val housing: Housing,
    @Relation(
        parentColumn = "housingID",
        entityColumn = "userID",
        associateBy = Junction(HousingUserCrossRef::class)
    )
    val users: List<User>
)