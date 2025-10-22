package com.f1calendar.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.f1calendar.data.local.dao.RaceDao
import com.f1calendar.data.model.Constructor
import com.f1calendar.data.model.Driver
import com.f1calendar.data.model.Race

@Database(
    entities = [Race::class, Driver::class, Constructor::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class F1Database : RoomDatabase() {
    abstract fun raceDao(): RaceDao
}
