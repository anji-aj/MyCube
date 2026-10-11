package com.example.mycube.ui.cube

import android.util.Log
import androidx.compose.ui.geometry.Offset
import com.example.mycube.domain.model.Face
import com.example.mycube.domain.model.Move
import com.example.mycube.domain.model.MoveDirection
import kotlin.math.abs

class GestureController {
    
    fun resolveMove(
        face: Face?,
        logicalX: Int,
        logicalY: Int,
        logicalZ: Int,
        dragVector: Offset?
    ): Move? {
        try {
            if (face == null || dragVector == null) return null
            
            if (logicalX < -1 || logicalX > 1 || 
                logicalY < -1 || logicalY > 1 || 
                logicalZ < -1 || logicalZ > 1) {
                return null
            }

            val threshold = 20f
            if (abs(dragVector.x) < threshold && abs(dragVector.y) < threshold) {
                return null
            }

            val isHorizontal = abs(dragVector.x) > abs(dragVector.y)
            val isPositive = if (isHorizontal) dragVector.x > 0 else dragVector.y < 0

            return when (face) {
                Face.FRONT -> resolveFrontMove(logicalX, logicalY, isHorizontal, isPositive)
                Face.BACK -> resolveBackMove(logicalX, logicalY, isHorizontal, isPositive)
                Face.RIGHT -> resolveRightMove(logicalY, logicalZ, isHorizontal, isPositive)
                Face.LEFT -> resolveLeftMove(logicalY, logicalZ, isHorizontal, isPositive)
                Face.UP -> resolveUpMove(logicalX, logicalZ, isHorizontal, isPositive)
                Face.DOWN -> resolveDownMove(logicalX, logicalZ, isHorizontal, isPositive)
            }
        } catch (e: Exception) {
            Log.e("Gesture", "Critical error in resolveMove", e)
            return null
        }
    }

    private fun resolveFrontMove(logicalX: Int, logicalY: Int, isHorizontal: Boolean, isPositive: Boolean): Move? {
        return if (isHorizontal) {
            val moveFace = if (logicalY == 1) Face.UP else if (logicalY == -1) Face.DOWN else null
            if (moveFace == null) return null
            val isCW = if (logicalY == 1) !isPositive else isPositive
            Move(moveFace, if (isCW) MoveDirection.CLOCKWISE else MoveDirection.COUNTER_CLOCKWISE)
        } else {
            val moveFace = if (logicalX == 1) Face.RIGHT else if (logicalX == -1) Face.LEFT else null
            if (moveFace == null) return null
            val isCW = if (logicalX == 1) isPositive else !isPositive
            Move(moveFace, if (isCW) MoveDirection.CLOCKWISE else MoveDirection.COUNTER_CLOCKWISE)
        }
    }

    private fun resolveBackMove(logicalX: Int, logicalY: Int, isHorizontal: Boolean, isPositive: Boolean): Move? {
        // Back is mirror of Front
        return resolveFrontMove(logicalX, logicalY, isHorizontal, !isPositive)
    }

    private fun resolveRightMove(logicalY: Int, logicalZ: Int, isHorizontal: Boolean, isPositive: Boolean): Move? {
        return if (isHorizontal) {
            val moveFace = if (logicalY == 1) Face.UP else if (logicalY == -1) Face.DOWN else null
            if (moveFace == null) return null
            val isCW = if (logicalY == 1) !isPositive else isPositive
            Move(moveFace, if (isCW) MoveDirection.CLOCKWISE else MoveDirection.COUNTER_CLOCKWISE)
        } else {
            val moveFace = if (logicalZ == 1) Face.FRONT else if (logicalZ == -1) Face.BACK else null
            if (moveFace == null) return null
            // F CW is Swipe DOWN (isPositive=false). B CW is Swipe UP (isPositive=true).
            val isCW = if (logicalZ == 1) !isPositive else isPositive
            Move(moveFace, if (isCW) MoveDirection.CLOCKWISE else MoveDirection.COUNTER_CLOCKWISE)
        }
    }

    private fun resolveLeftMove(logicalY: Int, logicalZ: Int, isHorizontal: Boolean, isPositive: Boolean): Move? {
        return resolveRightMove(logicalY, logicalZ, isHorizontal, !isPositive)
    }

    private fun resolveUpMove(logicalX: Int, logicalZ: Int, isHorizontal: Boolean, isPositive: Boolean): Move? {
        return if (isHorizontal) {
            val moveFace = if (logicalZ == 1) Face.FRONT else if (logicalZ == -1) Face.BACK else null
            if (moveFace == null) return null
            val isCW = if (logicalZ == 1) isPositive else !isPositive
            Move(moveFace, if (isCW) MoveDirection.CLOCKWISE else MoveDirection.COUNTER_CLOCKWISE)
        } else {
            val moveFace = if (logicalX == 1) Face.RIGHT else if (logicalX == -1) Face.LEFT else null
            if (moveFace == null) return null
            val isCW = if (logicalX == 1) isPositive else !isPositive
            Move(moveFace, if (isCW) MoveDirection.CLOCKWISE else MoveDirection.COUNTER_CLOCKWISE)
        }
    }

    private fun resolveDownMove(logicalX: Int, logicalZ: Int, isHorizontal: Boolean, isPositive: Boolean): Move? {
        return resolveUpMove(logicalX, logicalZ, isHorizontal, !isPositive)
    }
}
