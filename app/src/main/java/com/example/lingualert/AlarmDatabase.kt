package com.example.lingualert

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/*
* main access point for app to access db
* */

@Database(
    entities = [Alarm::class],
    version = 6,
    exportSchema = false // don't keep history of database
)

abstract class AlarmDatabase: RoomDatabase() {
    abstract fun dao(): AlarmDao

    // everything in here will be visible to other classes
    companion object {
        // created as a singleton to have one instance only
        @Volatile // writes are made visible immediately to other threads
        private var INSTANCE: AlarmDatabase? = null

        fun getDatabase(context: Context): AlarmDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) { // everything is protected from concurrent execution by multiple threads?
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AlarmDatabase::class.java,
                    "alarm_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}