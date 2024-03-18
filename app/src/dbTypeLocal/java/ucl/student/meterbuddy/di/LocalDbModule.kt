package ucl.student.meterbuddy.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ucl.student.meterbuddy.data.UserDatabase
import ucl.student.meterbuddy.data.repository.LocalMeterRepository
import ucl.student.meterbuddy.data.repository.MeterRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModuleObject{
    @Provides
    @Singleton
    fun provideLocalMeterRepository(db: UserDatabase): MeterRepository {
        return LocalMeterRepository(db.userDao)
    }

    @Provides
    @Singleton
    fun provideUserDatabase(app: Application): UserDatabase {
        return Room.databaseBuilder(
            app,
            UserDatabase::class.java,
            UserDatabase.DATABASE_NAME
        ).build()
    }

}