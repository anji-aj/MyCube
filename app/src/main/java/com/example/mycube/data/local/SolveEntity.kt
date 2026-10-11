package com.example.mycube.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solves")
data class SolveEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timeMillis: Long,
    val movesCount: Int,
    val movesSequence: String,
    val scramble: String,
    val cubeSize: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val isDNF: Boolean = false,
    val isPlusTwo: Boolean = false
)
