package com.example.mycube.data.repository

import com.example.mycube.data.local.SolveEntity
import kotlinx.coroutines.flow.Flow

interface SolveRepository {
    fun getAllSolves(): Flow<List<SolveEntity>>
    fun getSolvesBySize(size: Int): Flow<List<SolveEntity>>
    suspend fun saveSolve(solve: SolveEntity)
    suspend fun deleteSolve(solve: SolveEntity)
}
