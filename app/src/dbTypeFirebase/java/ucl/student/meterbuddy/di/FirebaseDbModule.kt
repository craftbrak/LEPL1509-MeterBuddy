package ucl.student.meterbuddy.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ucl.student.meterbuddy.data.repository.FireBaseMeterRepository
import ucl.student.meterbuddy.data.repository.MeterRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseDbModule{
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }
    @Provides
    @Singleton
    fun provideFirebaseMeterRepository(db: FirebaseFirestore): MeterRepository {
        return  FireBaseMeterRepository(db)
    }
}