package com.example.mycube.domain.model

enum class MoveDirection {
    CLOCKWISE, COUNTER_CLOCKWISE, DOUBLE
}

data class Move(
    val face: Face?, // null for rotations x, y, z or slices M, E, S
    val direction: MoveDirection = MoveDirection.CLOCKWISE,
    val layers: Int = 1, // 1 for standard, 2 for wide (Rw), etc.
    val isRotation: Boolean = false,
    val rotationAxis: Axis? = null,
    val isSlice: Boolean = false,
    val sliceType: SliceType? = null
) {
    enum class Axis { X, Y, Z }
    enum class SliceType { M, E, S }

    fun reverse(): Move {
        val newDirection = when (direction) {
            MoveDirection.CLOCKWISE -> MoveDirection.COUNTER_CLOCKWISE
            MoveDirection.COUNTER_CLOCKWISE -> MoveDirection.CLOCKWISE
            MoveDirection.DOUBLE -> MoveDirection.DOUBLE
        }
        return copy(direction = newDirection)
    }

    override fun toString(): String {
        val base = when {
            isRotation -> when (rotationAxis) {
                Axis.X -> "x"
                Axis.Y -> "y"
                Axis.Z -> "z"
                else -> ""
            }
            isSlice -> sliceType?.name ?: ""
            else -> {
                val faceChar = when (face) {
                    Face.UP -> "U"
                    Face.DOWN -> "D"
                    Face.LEFT -> "L"
                    Face.RIGHT -> "R"
                    Face.FRONT -> "F"
                    Face.BACK -> "B"
                    null -> ""
                }
                if (layers > 1) "${faceChar.lowercase()}w" else faceChar
            }
        }
        val suffix = when (direction) {
            MoveDirection.CLOCKWISE -> ""
            MoveDirection.COUNTER_CLOCKWISE -> "'"
            MoveDirection.DOUBLE -> "2"
        }
        return "$base$suffix"
    }
    
    companion object {
        fun fromNotation(notation: String): Move? {
            // Basic parser for WCA notation
            // Implementation will be added as needed or expanded in CubeLogic
            return null 
        }
    }
}
