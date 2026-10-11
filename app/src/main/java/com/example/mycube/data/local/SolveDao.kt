package com.example.mycube.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SolveDao {
    @Insert
    suspend fun insertSolve(solve: SolveEntity)

    @Query("SELECT * FROM solves WHERE cubeSize = :size ORDER BY timestamp DESC")
    fun getSolvesBySize(size: Int): Flow<List<SolveEntity>>

    @Query("SELECT * FROM solves ORDER BY timestamp DESC")
    fun getAllSolves(): Flow<List<SolveEntity>>

    @Delete
    suspend fun deleteSolve(solve: SolveEntity)

    @Query("DELETE FROM solves")
    suspend fun clearHistory()
}
