package ucl.student.meterbuddy.data.model.enums


enum class Role(val role: String, val canAddMembers: Boolean, val write: Boolean, val read: Boolean){
    ADMIN("Admin", true, true, true),
    Member("Member", false, true, true),
    Viewer("Viewer", false, false, true)
}