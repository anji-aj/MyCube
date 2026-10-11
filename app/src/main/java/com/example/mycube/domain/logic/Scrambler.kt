package com.example.mycube.domain.logic

import com.example.mycube.domain.model.*
import kotlin.random.Random

object Scrambler {
    fun generateScramble(size: Int): List<Move> {
        val length = when (size) {
            2 -> 9
            3 -> 21
            4 -> 40
            5 -> 60
            else -> 20
        }
        
        val scramble = mutableListOf<Move>()
        var lastFace: Face? = null
        
        repeat(length) {
            var face: Face
            do {
                face = Face.values().random()
            } while (face == lastFace)
            
            val direction = MoveDirection.values().random()
            val layers = if (size > 3 && Random.nextBoolean()) 2 else 1
            
            val move = Move(face = face, direction = direction, layers = layers)
            scramble.add(move)
            lastFace = face
        }
        
        return scramble
    }
}
