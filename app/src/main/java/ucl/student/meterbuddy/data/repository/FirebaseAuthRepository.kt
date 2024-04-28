package ucl.student.meterbuddy.data.repository

import android.util.Log
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import ucl.student.meterbuddy.data.utils.AuthException
import ucl.student.meterbuddy.data.utils.Resource
import java.util.Optional
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    override fun currentUser(): Optional<FirebaseUser> {
        return if (firebaseAuth.currentUser != null) Optional.of(firebaseAuth.currentUser!!)
        else Optional.empty()
    }

    override fun loginUser(
        email: String,
        password: String
    ): Flow<Resource<AuthResult, AuthException>> {
        return flow {
            emit(Resource.Loading<AuthResult, AuthException>(true))
            val res = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit(Resource.Success<AuthResult, AuthException>(res))
        }.catch { throwable ->
            throwable.message?.let { Log.e("AuthRepo", it) }
            when (throwable.message) {
                ("Invalid Credentials") -> emit(Resource.Error(AuthException.BAD_CREDENTIALS))
                ("The supplied auth credential is incorrect, malformed or has expired.")-> emit(Resource.Error(AuthException.BAD_CREDENTIALS))
                ("We have blocked all requests from this device due to unusual activity. Try again later. [ Access to this account has been temporarily disabled due to many failed login attempts. You can immediately restore it by resetting your password or you can try again later. ]")-> emit(Resource.Error(AuthException.TO_MANY_ATTEMPT))
                ("No network") -> emit(Resource.Error(AuthException.NO_NETWORK))
                ("A network error (such as timeout, interrupted connection or unreachable host) has occurred.") -> emit(Resource.Error(AuthException.NO_NETWORK))
                ("The given password is invalid. [ Password should be at least 6 characters ]") -> emit(Resource.Error(AuthException.PASSWORD_TO0_SHORT))
                ("The email address is badly formatted.") -> emit(Resource.Error(AuthException.EMAIL_BAD_FORMATTED))
                else -> emit(Resource.Error(AuthException.UNKNOWN_ERROR))
            }
        }
    }

    override fun registerUser(
        email: String,
        password: String
    ): Flow<Resource<AuthResult, AuthException>> {
        return flow {
            emit(Resource.Loading<AuthResult, AuthException>(true))
            val res = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            emit(Resource.Success(res))
        }.catch {
            when (it.message) {
                ("Invalid Credentials") -> emit(Resource.Error(AuthException.BAD_CREDENTIALS))
                ("Initial task failed for action RecaptchaAction(action=signUpPassword)with exception - A network error (such as timeout, interrupted connection or unreachable host) has occurred.") -> emit(Resource.Error(AuthException.NO_NETWORK))
                ("We have blocked all requests from this device due to unusual activity. Try again later. [ Access to this account has been temporarily disabled due to many failed login attempts. You can immediately restore it by resetting your password or you can try again later. ]")-> emit(Resource.Error(AuthException.TO_MANY_ATTEMPT))
                ("A network error (such as timeout, interrupted connection or unreachable host) has occurred.") -> emit(Resource.Error(AuthException.NO_NETWORK))
                ("The given password is invalid. [ Password should be at least 6 characters ]") -> emit(Resource.Error(AuthException.PASSWORD_TO0_SHORT))
                ("The email address is already in use by another account.") -> emit(Resource.Error(AuthException.EMAIL_ALREADY_TAKEN))
                ("The email address is badly formatted.") -> emit(Resource.Error(AuthException.EMAIL_BAD_FORMATTED))
                else -> emit(Resource.Error(AuthException.UNKNOWN_ERROR))
            }
        }
    }

    override fun getUser(): Flow<Resource<FirebaseUser, AuthException>> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener {
            val currentUser = it.currentUser
            if (currentUser != null) {
                trySend(Resource.Success(currentUser))
            } else {
                trySend(Resource.Error(AuthException.NO_CURRENT_USER))
            }
        }

        firebaseAuth.addAuthStateListener(authStateListener)

        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}