package ucl.student.meterbuddy.di

import android.app.Application
import androidx.room.Room
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import ucl.student.meterbuddy.data.UserDatabase
import ucl.student.meterbuddy.data.repository.LocalMeterRepository
import ucl.student.meterbuddy.data.repository.MeterRepository
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel
import ucl.student.meterbuddy.viewmodel.MeterScreenModel
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
abstract class AppModule {
    @Binds
    @IntoMap
    @ScreenModelKey(MainPageScreenModel::class)
    abstract fun bindMainPageScreenModel(mainPageScreenModel: MainPageScreenModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(MeterScreenModel.Factory::class)
    abstract fun bindMeterPageScreenModel(meterScreenModelFactory: MeterScreenModel.Factory):ScreenModelFactory


}
@Module
@InstallIn(SingletonComponent::class)
object AppModuleObject{
    @Provides
    @Singleton
    fun provideMeterRepository(db: UserDatabase): MeterRepository{
        return LocalMeterRepository(db.userDao)
    }
    @Provides
    @Singleton
    fun provideUserDatabase(app: Application): UserDatabase{
        return Room.databaseBuilder(
            app,
            UserDatabase::class.java,
            UserDatabase.DATABASE_NAME
        ).build()
    }
}