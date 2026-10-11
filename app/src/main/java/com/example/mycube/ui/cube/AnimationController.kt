package com.example.mycube.ui.cube

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import com.example.mycube.domain.model.Move
import com.example.mycube.domain.model.MoveDirection
import com.example.mycube.domain.model.Face
import com.example.mycube.utils.Matrix4
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AnimationController {
    private val _animatingMove = MutableStateFlow<Move?>(null)
    val animatingMove = _animatingMove.asStateFlow()

    val rotationProgress = Animatable(0f)

    suspend fun animateMove(move: Move) {
        _animatingMove.value = move
        rotationProgress.snapTo(0f)
        val targetAngle = when (move.direction) {
            MoveDirection.CLOCKWISE -> 90f
            MoveDirection.COUNTER_CLOCKWISE -> -90f
            MoveDirection.DOUBLE -> 180f
        }
        rotationProgress.animateTo(
            targetValue = targetAngle,
            animationSpec = tween(durationMillis = 300)
        )
        _animatingMove.value = null
    }

    fun getAnimationMatrix(move: Move, progress: Float): Matrix4 {
        val angle = (progress * (Math.PI / 180f)).toFloat()
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
}
