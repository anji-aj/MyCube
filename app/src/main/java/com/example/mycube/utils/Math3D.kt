package com.example.mycube.utils

import android.opengl.Matrix
import kotlin.math.*

data class Vector3(val x: Float, val y: Float, val z: Float) {
    operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Float) = Vector3(x * scalar, y * scalar, z * scalar)
    operator fun div(scalar: Float): Vector3 {
        return if (abs(scalar) > 1e-6f) Vector3(x / scalar, y / scalar, z / scalar) 
        else Vector3(0f, 0f, 0f)
    }
    
    fun length() = sqrt(x * x + y * y + z * z)
    fun normalized(): Vector3 {
        val l = length()
        return if (l > 1e-6f) this * (1f / l) else Vector3(0f, 0f, 0f)
    }
    
    fun dot(other: Vector3) = x * other.x + y * other.y + z * other.z
    fun cross(other: Vector3) = Vector3(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )
}

data class Vector3Int(val x: Int, val y: Int, val z: Int) {
    fun toVector3() = Vector3(x.toFloat(), y.toFloat(), z.toFloat())
}

/**
 * A wrapper around android.opengl.Matrix for robust 4x4 operations.
 */
class Matrix4(val data: FloatArray = FloatArray(16)) {
    init {
        if (data.size != 16) throw IllegalArgumentException("Matrix4 must have 16 elements")
    }

    companion object {
        fun identity(): Matrix4 {
            val m = Matrix4()
            Matrix.setIdentityM(m.data, 0)
            return m
        }

        fun translation(x: Float, y: Float, z: Float): Matrix4 {
            val m = identity()
            Matrix.translateM(m.data, 0, x, y, z)
            return m
        }

        fun rotationX(angleRad: Float): Matrix4 {
            val m = identity()
            Matrix.rotateM(m.data, 0, angleRad * 180f / PI.toFloat(), 1f, 0f, 0f)
            return m
        }

        fun rotationY(angleRad: Float): Matrix4 {
            val m = identity()
            Matrix.rotateM(m.data, 0, angleRad * 180f / PI.toFloat(), 0f, 1f, 0f)
            return m
        }

        fun rotationZ(angleRad: Float): Matrix4 {
            val m = identity()
            Matrix.rotateM(m.data, 0, angleRad * 180f / PI.toFloat(), 0f, 0f, 1f)
            return m
        }
        
        fun perspective(fov: Float, aspect: Float, near: Float, far: Float): Matrix4 {
            val m = Matrix4()
            if (aspect > 0 && near < far) {
                Matrix.perspectiveM(m.data, 0, fov * 180f / PI.toFloat(), aspect, near, far)
            } else {
                Matrix.setIdentityM(m.data, 0)
            }
            return m
        }
    }

    operator fun times(other: Matrix4): Matrix4 {
        val res = Matrix4()
        Matrix.multiplyMM(res.data, 0, this.data, 0, other.data, 0)
        return res
    }

    /**
     * Safely inverts the matrix. Returns null if non-invertible.
     */
    fun invert(): Matrix4? {
        val inv = Matrix4()
        val success = Matrix.invertM(inv.data, 0, this.data, 0)
        return if (success) inv else null
    }

    /**
     * Projects a Vector3 using the current matrix, with near-plane clipping.
     */
    fun project(v: Vector3): Vector3? {
        val x = v.x * data[0] + v.y * data[4] + v.z * data[8] + data[12]
        val y = v.x * data[1] + v.y * data[5] + v.z * data[9] + data[13]
        val z = v.x * data[2] + v.y * data[6] + v.z * data[10] + data[14]
        val w = v.x * data[3] + v.y * data[7] + v.z * data[11] + data[15]
        
        if (w <= 0.1f) return null // Near-plane clipping
        
        return Vector3(x / w, y / w, z / w)
    }
    
    fun transform(v: Vector3): Vector3 {
        val x = v.x * data[0] + v.y * data[4] + v.z * data[8] + data[12]
        val y = v.x * data[1] + v.y * data[5] + v.z * data[9] + data[13]
        val z = v.x * data[2] + v.y * data[6] + v.z * data[10] + data[14]
        return Vector3(x, y, z)
    }

    /**
     * Re-orthogonalizes the rotation component of the matrix.
     * Prevents shearing/squishing caused by floating point accumulation.
     */
    fun orthonormalize(): Matrix4 {
        val x = Vector3(data[0], data[1], data[2]).normalized()
        val y = Vector3(data[4], data[5], data[6])
        val z = x.cross(y).normalized()
        val newY = z.cross(x).normalized()

        data[0] = x.x; data[1] = x.y; data[2] = x.z
        data[4] = newY.x; data[5] = newY.y; data[6] = newY.z
        data[8] = z.x; data[9] = z.y; data[10] = z.z
        return this
    }
}
