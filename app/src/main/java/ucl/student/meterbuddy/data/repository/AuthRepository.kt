package ucl.student.meterbuddy.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import ucl.student.meterbuddy.data.model.entity.User
import ucl.student.meterbuddy.data.utils.AuthException
import ucl.student.meterbuddy.data.utils.Resource
import java.util.Optional

interface AuthRepository {
    fun currentUser():Optional<FirebaseUser>
    fun loginUser(email: String, password : String):Flow<Resource<AuthResult, AuthException>>
    fun registerUser(
        email: String,
        password: String,
        user: User = User()
    ): Flow<Resource<AuthResult, AuthException>>
    fun logout()
    fun getUser():Flow<Resource<FirebaseUser,AuthException>>
}