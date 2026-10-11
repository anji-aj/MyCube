package com.example.mycube.domain.model

import com.example.mycube.utils.Matrix4
import com.example.mycube.utils.Vector3Int

data class Cubie(
    val id: Int,
    val logicalPos: Vector3Int,
    val rotation: Matrix4 = Matrix4.identity(),
    val colors: Map<Face, CubeColor>
) {
    fun getVisualPosition(cubeletSize: Float, gap: Float): com.example.mycube.utils.Vector3 {
        return com.example.mycube.utils.Vector3(
            logicalPos.x * (cubeletSize + gap),
            logicalPos.y * (cubeletSize + gap),
            logicalPos.z * (cubeletSize + gap)
        )
    }
}
