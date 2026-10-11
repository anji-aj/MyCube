package com.example.mycube.ui.cube

import androidx.compose.runtime.*
import com.example.mycube.utils.Matrix4
import kotlin.math.PI

class CameraController {
    var rotationX by mutableStateOf(-25f)
    var rotationY by mutableStateOf(45f)
    var zoom by mutableStateOf(1f)

    fun rotate(dx: Float, dy: Float) {
        rotationY += dx * 0.5f
        rotationX -= dy * 0.5f
    }

    fun adjustZoom(delta: Float) {
        zoom = (zoom * delta).coerceIn(0.5f, 3f)
    }

    fun reset() {
        rotationX = -25f
        rotationY = 45f
        zoom = 1f
    }

    fun getViewMatrix(): Matrix4 {
        return Matrix4.rotationX(rotationX * (PI / 180f).toFloat()) * 
               Matrix4.rotationY(rotationY * (PI / 180f).toFloat())
    }
}
