package com.example.mycube.data.repository

import com.example.mycube.data.local.SolveDao
import com.example.mycube.data.local.SolveEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SolveRepositoryImpl @Inject constructor(
    private val solveDao: SolveDao
) : SolveRepository {
    override fun getAllSolves(): Flow<List<SolveEntity>> = solveDao.getAllSolves()
    override fun getSolvesBySize(size: Int): Flow<List<SolveEntity>> = solveDao.getSolvesBySize(size)
    override suspend fun saveSolve(solve: SolveEntity) = solveDao.insertSolve(solve)
    override suspend fun deleteSolve(solve: SolveEntity) = solveDao.deleteSolve(solve)
}
