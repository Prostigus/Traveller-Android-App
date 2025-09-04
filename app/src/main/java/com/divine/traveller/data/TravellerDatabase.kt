package com.divine.traveller.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.divine.traveller.data.dao.TripDao
import com.divine.traveller.data.entity.TripEntity

@Database(
    entities = [TripEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class TravellerDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao

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