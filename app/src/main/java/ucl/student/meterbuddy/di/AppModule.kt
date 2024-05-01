package ucl.student.meterbuddy.di

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import ucl.student.meterbuddy.data.repository.AuthRepository
import ucl.student.meterbuddy.data.repository.FirebaseAuthRepository
import ucl.student.meterbuddy.viewmodel.MainPageState
import ucl.student.meterbuddy.viewmodel.MeterScreenModel
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
abstract class AppModule {
//    @Binds
//    @IntoMap
//    @ScreenModelKey(MainPageScreenModel::class)
//    abstract fun bindMainPageScreenModel(mainPageScreenModel: MainPageScreenModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(MeterScreenModel.Factory::class)
                    abstract fun bindMeterPageScreenModel(meterScreenModelFactory: MeterScreenModel.Factory): ScreenModelFactory

}

@Module
@InstallIn(SingletonComponent::class)
object AppModuleStatic {
    @Provides
    @Singleton
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseAuthRepository(fb: FirebaseAuth): AuthRepository {
        return FirebaseAuthRepository(fb)
    }

    @Provides
    @Singleton
    fun provideMainPageState(): MutableState<MainPageState> {
        return mutableStateOf(MainPageState())
    }
}

