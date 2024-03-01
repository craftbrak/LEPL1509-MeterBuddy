package ucl.student.meterbuddy.data.model.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import ucl.student.meterbuddy.data.model.entity.Housing
import ucl.student.meterbuddy.data.model.entity.User

data class HousingWithUsers (
    @Embedded val housing: Housing,
    @Relation(
        parentColumn = "housingID",
        entityColumn = "userID",
        associateBy = Junction(HousingUserCrossRef::class)
    )
    val users: List<User>
)