package com.example.mycube.di

import android.content.Context
import androidx.room.Room
import com.example.mycube.data.local.AppDatabase
import com.example.mycube.data.local.SolveDao
import com.example.mycube.data.repository.SolveRepository
import com.example.mycube.data.repository.SolveRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mycube_db"
        ).build()
    }

    @Provides
    fun provideSolveDao(db: AppDatabase): SolveDao = db.solveDao()

    @Provides
    @Singleton
    fun provideSolveRepository(solveDao: SolveDao): SolveRepository {
        return SolveRepositoryImpl(solveDao)
    }
}
