package ucl.student.meterbuddy.tables.entities


enum class Role(val role: String, val canAddMembers: Boolean, val write: Boolean, val read: Boolean){
    ADMIN("Admin", true, true, true),
    Member("Member", false, true, true),
    Viewer("Viewer", false, false, true)
}