package com.divine.traveller.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.divine.traveller.data.dao.BudgetItemDao
import com.divine.traveller.data.dao.DocumentDao
import com.divine.traveller.data.dao.FlightDao
import com.divine.traveller.data.dao.HotelDao
import com.divine.traveller.data.dao.ItineraryItemDao
import com.divine.traveller.data.dao.TripDao
import com.divine.traveller.data.entity.BudgetItemEntity
import com.divine.traveller.data.entity.DocumentEntity
import com.divine.traveller.data.entity.FlightEntity
import com.divine.traveller.data.entity.HotelEntity
import com.divine.traveller.data.entity.ItineraryItemEntity
import com.divine.traveller.data.entity.TripEntity

@Database(
    entities = [TripEntity::class, ItineraryItemEntity::class, FlightEntity::class, HotelEntity::class, BudgetItemEntity::class, DocumentEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class TravellerDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao
    abstract fun itineraryItemDao(): ItineraryItemDao
    abstract fun flightDao(): FlightDao
    abstract fun hotelDao(): HotelDao
    abstract fun budgetItemDao(): BudgetItemDao
    abstract fun documentDao(): DocumentDao

    companion object {
        @Volatile
        private var INSTANCE: TravellerDatabase? = null

        fun getDatabase(context: Context): TravellerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        TravellerDatabase::class.java,
                        "traveller_database",
                    ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}