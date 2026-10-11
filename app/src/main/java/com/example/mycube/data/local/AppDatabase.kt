package com.example.mycube.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SolveEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun solveDao(): SolveDao
}
