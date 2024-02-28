package ucl.student.meterbuddy.tables.entities.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import ucl.student.meterbuddy.tables.entities.Housing
import ucl.student.meterbuddy.tables.entities.User

data class UserWithHousings (
    @Embedded val user: User,
    @Relation(
        parentColumn = "userID",
        entityColumn = "housingID",
        associateBy = Junction(HousingUserCrossRef::class)
    )
    val housings: List<Housing>
)