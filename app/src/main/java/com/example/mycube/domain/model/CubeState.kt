package com.example.mycube.domain.model

import com.example.mycube.utils.Matrix4
import com.example.mycube.utils.Vector3Int
import kotlin.math.roundToInt

class CubeState(
    val size: Int = 3,
    val cubies: List<Cubie> = emptyList()
) {
    init {
        if (cubies.isEmpty()) {
            // Initial reset logic if no cubies provided
        }
    }

    companion object {
        fun createStandard(size: Int): CubeState {
            val newCubies = mutableListOf<Cubie>()
            val offset = (size - 1) / 2
            var id = 0
            for (x in 0 until size) {
                for (y in 0 until size) {
                    for (z in 0 until size) {
                        val logicalX = x - offset
                        val logicalY = y - offset
                        val logicalZ = z - offset
                        
                        val colors = mutableMapOf<Face, CubeColor>()
                        if (logicalY == offset) colors[Face.UP] = CubeColor.WHITE
                        if (logicalY == -offset) colors[Face.DOWN] = CubeColor.YELLOW
                        if (logicalZ == offset) colors[Face.FRONT] = CubeColor.GREEN
                        if (logicalZ == -offset) colors[Face.BACK] = CubeColor.BLUE
                        if (logicalX == -offset) colors[Face.LEFT] = CubeColor.ORANGE
                        if (logicalX == offset) colors[Face.RIGHT] = CubeColor.RED
                        
                        newCubies.add(Cubie(id++, Vector3Int(logicalX, logicalY, logicalZ), Matrix4.identity(), colors))
                    }
                }
            }
            return CubeState(size, newCubies)
        }
    }

    fun applyMove(move: Move): CubeState {
        val rotationMatrix = getRotationMatrix(move)
        val offset = (size - 1) / 2
        
        val newCubies = cubies.map { cubie ->
            val isAffected = when (move.face) {
                Face.UP -> cubie.logicalPos.y == offset
                Face.DOWN -> cubie.logicalPos.y == -offset
                Face.LEFT -> cubie.logicalPos.x == -offset
                Face.RIGHT -> cubie.logicalPos.x == offset
                Face.FRONT -> cubie.logicalPos.z == offset
                Face.BACK -> cubie.logicalPos.z == -offset
                null -> false
            }

            if (isAffected) {
                val oldPos = cubie.logicalPos
                val rotatedPos = rotationMatrix.transform(com.example.mycube.utils.Vector3(oldPos.x.toFloat(), oldPos.y.toFloat(), oldPos.z.toFloat()))
                val newLogicalPos = Vector3Int(
                    rotatedPos.x.roundToInt(),
                    rotatedPos.y.roundToInt(),
                    rotatedPos.z.roundToInt()
                )
                cubie.copy(
                    logicalPos = newLogicalPos,
                    rotation = (rotationMatrix * cubie.rotation).orthonormalize()
                )
            } else {
                cubie
            }
        }
        return CubeState(size, newCubies)
    }

    private fun getRotationMatrix(move: Move): Matrix4 {
        val angle = when (move.direction) {
            MoveDirection.CLOCKWISE -> -Math.PI / 2
            MoveDirection.COUNTER_CLOCKWISE -> Math.PI / 2
            MoveDirection.DOUBLE -> Math.PI
        }.toFloat()
        
        return when (move.face) {
            Face.UP -> Matrix4.rotationY(-angle)
            Face.DOWN -> Matrix4.rotationY(angle)
            Face.LEFT -> Matrix4.rotationX(angle)
            Face.RIGHT -> Matrix4.rotationX(-angle)
            Face.FRONT -> Matrix4.rotationZ(-angle)
            Face.BACK -> Matrix4.rotationZ(angle)
            null -> Matrix4.identity()
        }
    }

    fun isSolved(): Boolean {
        for (face in Face.entries) {
            val offset = (size - 1) / 2
            val faceCubies = cubies.filter { cubie ->
                when (face) {
                    Face.UP -> cubie.logicalPos.y == offset
                    Face.DOWN -> cubie.logicalPos.y == -offset
                    Face.LEFT -> cubie.logicalPos.x == -offset
                    Face.RIGHT -> cubie.logicalPos.x == offset
                    Face.FRONT -> cubie.logicalPos.z == offset
                    Face.BACK -> cubie.logicalPos.z == -offset
                }
            }
            
            val colors = faceCubies.mapNotNull { cubie ->
                cubie.colors.entries.find { (origFace, _) ->
                    val normal = getNormal(origFace)
                    val transformedNormal = cubie.rotation.transform(normal)
                    transformedNormal.isCloseTo(getNormal(face))
                }?.value
            }
            if (colors.size < (size * size) || colors.distinct().size > 1) return false
        }
        return true
    }

    private fun getNormal(face: Face) = when (face) {
        Face.UP -> com.example.mycube.utils.Vector3(0f, 1f, 0f)
        Face.DOWN -> com.example.mycube.utils.Vector3(0f, -1f, 0f)
        Face.LEFT -> com.example.mycube.utils.Vector3(-1f, 0f, 0f)
        Face.RIGHT -> com.example.mycube.utils.Vector3(1f, 0f, 0f)
        Face.FRONT -> com.example.mycube.utils.Vector3(0f, 0f, 1f)
        Face.BACK -> com.example.mycube.utils.Vector3(0f, 0f, -1f)
    }

    private fun com.example.mycube.utils.Vector3.isCloseTo(other: com.example.mycube.utils.Vector3): Boolean {
        return Math.abs(x - other.x) < 0.1f && Math.abs(y - other.y) < 0.1f && Math.abs(z - other.z) < 0.1f
    }
}
