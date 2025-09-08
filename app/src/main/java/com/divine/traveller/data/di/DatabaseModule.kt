package com.divine.traveller.data.di

import android.content.Context
import androidx.room.Room
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
    @Singleton
    fun provideTripDao(database: TravellerDatabase) = database.tripDao()

    @Provides
    @Singleton
    fun provideItineraryItemDao(database: TravellerDatabase) = database.itineraryItemDao()

    @Provides
    @Singleton
    fun provideFlightDao(database: TravellerDatabase) = database.flightDao()

    @Provides
    @Singleton
    fun provideHotelDao(database: TravellerDatabase) = database.hotelDao()

    @Provides
    @Singleton
    fun provideBudgetItemDao(database: TravellerDatabase) = database.budgetItemDao()

    @Provides
    @Singleton
    fun provideDocumentDao(database: TravellerDatabase) = database.documentDao()
}