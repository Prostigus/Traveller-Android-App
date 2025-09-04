package com.divine.traveller.data

import android.content.Context
import androidx.room.Room
import com.divine.traveller.data.dao.TripDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TravellerDatabase {
        return Room.databaseBuilder(
            context,
            TravellerDatabase::class.java,
            "traveller_database"
        ).build()
    }

    @Provides
    fun provideTripDao(database: TravellerDatabase): TripDao {
        return database.tripDao()
    }
}