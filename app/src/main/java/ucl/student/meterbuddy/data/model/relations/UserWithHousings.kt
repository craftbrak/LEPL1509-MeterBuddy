package ucl.student.meterbuddy.data.model.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import ucl.student.meterbuddy.data.model.entity.Housing
import ucl.student.meterbuddy.data.model.entity.User


data class UserWithHousings (
    @Embedded val user: User,
    @Relation(
        parentColumn = "userID",
        entityColumn = "housingID",
        associateBy = Junction(HousingUserCrossRef::class)
    )
    val housings: List<Housing>
)