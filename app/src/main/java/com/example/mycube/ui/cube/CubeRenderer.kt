package com.example.mycube.ui.cube

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.*
import com.example.mycube.domain.model.*
import com.example.mycube.ui.viewmodel.PlayViewModel
import com.example.mycube.utils.*
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.*

private const val TAG = "CubeTouch"

enum class InteractionMode { NONE, CAMERA, LAYER }

@Composable
fun CubeView(
    viewModel: PlayViewModel,
    modifier: Modifier = Modifier
) {
    val cubeState by viewModel.cubeState.collectAsState()
    val cameraController = remember { CameraController() }
    val animationController = viewModel.animationController
    val animatingMove by animationController.animatingMove.collectAsState()
    val progress = animationController.rotationProgress.value
    val gestureController = remember { GestureController() }
    val scope = rememberCoroutineScope()

    var interactionMode by remember { mutableStateOf(InteractionMode.NONE) }
    var touchedSticker by remember { mutableStateOf<StickerFace?>(null) }
    var startPosition by remember { mutableStateOf(Offset.Zero) }
    
    var dragMove by remember { mutableStateOf<Move?>(null) }
    val dragAngle = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "floatingOffset"
    )
    val currentFloatY = sin(floatingOffset) * 0.5f

    val pickingFaces = remember { AtomicReference<List<StickerFace>>(emptyList()) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                if (size.width <= 0 || size.height <= 0) return@pointerInput
                
                awaitPointerEventScope {
                    while (true) {
                        try {
                            val event = awaitPointerEvent()
                            
                            if (event.changes.size >= 2) {
                                interactionMode = InteractionMode.NONE
                                val zoom = calculateZoomFactor(event)
                                cameraController.adjustZoom(zoom)
                                event.changes.forEach { it.consume() }
                                continue
                            }

                            val change = event.changes.firstOrNull() ?: continue
                            
                            if (change.changedToDown()) {
                                startPosition = change.position
                                val canvasCenter = Offset(size.width / 2f, size.height / 2f)
                                val currentRenderScale = min(size.width, size.height) * 0.7f 
                                
                                val currentFaces = pickingFaces.get() ?: emptyList()
                                val hit = currentFaces.findLast { it.containsPoint(change.position, canvasCenter, currentRenderScale) }
                                
                                if (hit != null) {
                                    interactionMode = InteractionMode.LAYER
                                    touchedSticker = hit
                                } else {
                                    interactionMode = InteractionMode.CAMERA
                                    touchedSticker = null
                                }
                                dragMove = null
                                scope.launch { dragAngle.snapTo(0f) }
                            }

                            if (change.pressed) {
                                val delta = change.position - startPosition
                                if (interactionMode == InteractionMode.LAYER && touchedSticker != null) {
                                    if (dragMove == null && delta.getDistance() > 20f) {
                                        dragMove = gestureController.resolveMove(
                                            face = touchedSticker?.origFace,
                                            logicalX = touchedSticker?.logicalPos?.x ?: 0,
                                            logicalY = touchedSticker?.logicalPos?.y ?: 0,
                                            logicalZ = touchedSticker?.logicalPos?.z ?: 0,
                                            dragVector = delta
                                        )
                                    }
                                    
                                    if (dragMove != null) {
                                        val angle = calculateDragAngle(dragMove!!, delta)
                                        scope.launch { dragAngle.snapTo(angle) }
                                    }
                                } else if (interactionMode == InteractionMode.CAMERA) {
                                    val moveDelta = change.positionChange()
                                    cameraController.rotate(moveDelta.x, moveDelta.y)
                                    change.consume()
                                }
                            }

                            if (change.changedToUp()) {
                                if (interactionMode == InteractionMode.LAYER && dragMove != null) {
                                    val finalAngle = dragAngle.value
                                    scope.launch {
                                        if (abs(finalAngle) > 45f) {
                                            val move = if (finalAngle < 0) dragMove!!.reverse() else dragMove!!
                                            viewModel.applyMove(move)
                                        }
                                        dragMove = null
                                        dragAngle.snapTo(0f)
                                    }
                                }
                                interactionMode = InteractionMode.NONE
                                touchedSticker = null
                                dragMove = null
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Critical error in touch handler", e)
                        }
                    }
                }
            }
    ) {
        val aspect = if (size.height > 0) size.width / size.height else 1f
        val projectionMatrix = Matrix4.perspective(
            fov = (45f * PI / 180f).toFloat(),
            aspect = aspect,
            near = 1f,
            far = 100f
        )
        
        val viewMatrix = cameraController.getViewMatrix()
        val cameraTranslation = Matrix4.translation(0f, currentFloatY, -14f)
        val vpMatrix = projectionMatrix * cameraTranslation * viewMatrix

        val cubieSize = 1.0f
        val gap = 0.05f
        val facesToDraw = mutableListOf<StickerFace>()
        
        cubeState.cubies.forEach { cubie ->
            var modelRotation = cubie.rotation
            
            // Apply layer animations
            animatingMove?.let { move ->
                if (isCubieInLayer(cubie, move, cubeState.size)) {
                    val animMatrix = animationController.getAnimationMatrix(move, progress)
                    modelRotation = animMatrix * modelRotation
                }
            }
            
            dragMove?.let { move ->
                if (isCubieInLayer(cubie, move, cubeState.size)) {
                    val dragMatrix = animationController.getAnimationMatrix(move, dragAngle.value)
                    modelRotation = dragMatrix * modelRotation
                }
            }
            
            val modelTranslation = Matrix4.translation(
                cubie.logicalPos.x * (cubieSize + gap),
                cubie.logicalPos.y * (cubieSize + gap),
                cubie.logicalPos.z * (cubieSize + gap)
            )
            
            val mvpMatrix = vpMatrix * modelTranslation * modelRotation

            Face.entries.forEach { face ->
                val color = cubie.colors[face] ?: CubeColor.NONE
                if (color != CubeColor.NONE) {
                    addSticker(facesToDraw, face, cubie.logicalPos, cubieSize, color, mvpMatrix)
                }
            }
        }

        facesToDraw.sortBy { it.averageZ }
        pickingFaces.set(ArrayList(facesToDraw))

        val renderScale = min(size.width, size.height) * 0.7f 
        facesToDraw.forEach { sticker ->
            drawSticker(sticker, renderScale)
        }
    }
}

private fun calculateDragAngle(move: Move, delta: Offset): Float {
    val sensitivity = 0.6f
    return when (move.face) {
        Face.UP -> -delta.x * sensitivity
        Face.DOWN -> delta.x * sensitivity
        Face.LEFT -> delta.y * sensitivity
        Face.RIGHT -> -delta.y * sensitivity
        Face.FRONT -> delta.x * sensitivity 
        Face.BACK -> -delta.x * sensitivity
        else -> 0f
    }
}

private fun calculateZoomFactor(event: PointerEvent): Float {
    val before = event.changes[0].previousPosition - event.changes[1].previousPosition
    val after = event.changes[0].position - event.changes[1].position
    return if (before.getDistance() > 0) after.getDistance() / before.getDistance() else 1f
}

private fun isCubieInLayer(cubie: Cubie, move: Move, size: Int): Boolean {
    val offset = (size - 1) / 2
    return when (move.face) {
        Face.UP -> cubie.logicalPos.y == offset
        Face.DOWN -> cubie.logicalPos.y == -offset
        Face.LEFT -> cubie.logicalPos.x == -offset
        Face.RIGHT -> cubie.logicalPos.x == offset
        Face.FRONT -> cubie.logicalPos.z == offset
        Face.BACK -> cubie.logicalPos.z == -offset
        null -> false
    }
}

private fun addSticker(
    list: MutableList<StickerFace>,
    face: Face,
    logicalPos: Vector3Int,
    s: Float,
    color: CubeColor,
    mvp: Matrix4
) {
    val h = s / 2f
    val margin = 0.05f 
    val sh = h - margin
    
    val localVertices = when (face) {
        Face.UP -> listOf(Vector3(-sh, h, -sh), Vector3(sh, h, -sh), Vector3(sh, h, sh), Vector3(-sh, h, sh))
        Face.DOWN -> listOf(Vector3(-sh, -h, sh), Vector3(sh, -h, sh), Vector3(sh, -h, -sh), Vector3(-sh, -h, -sh))
        Face.FRONT -> listOf(Vector3(-sh, sh, h), Vector3(sh, sh, h), Vector3(sh, -sh, h), Vector3(-sh, -sh, h))
        Face.BACK -> listOf(Vector3(sh, sh, -h), Vector3(-sh, sh, -h), Vector3(-sh, -h, -sh), Vector3(sh, -h, -sh))
        Face.LEFT -> listOf(Vector3(-h, sh, -sh), Vector3(-h, sh, sh), Vector3(-h, -sh, sh), Vector3(-h, -sh, -sh))
        Face.RIGHT -> listOf(Vector3(h, sh, sh), Vector3(h, sh, -sh), Vector3(h, -sh, -sh), Vector3(h, -sh, sh))
    }

    val projected = localVertices.map { mvp.project(it) }
    if (projected.any { it == null }) return
    val vertices = projected.filterNotNull()
    
    val v1 = vertices[1] - vertices[0]
    val v2 = vertices[2] - vertices[0]
    val normal = v1.cross(v2)
    if (normal.z < 0) return 

    val avgZ = vertices.map { it.z }.average().toFloat()
    list.add(StickerFace(vertices, avgZ, color, normal.normalized(), face, logicalPos))
}

data class StickerFace(
    val vertices: List<Vector3>, 
    val averageZ: Float, 
    val color: CubeColor, 
    val normal: Vector3,
    val origFace: Face,
    val logicalPos: Vector3Int
) {
    fun containsPoint(point: Offset, center: Offset, scale: Float): Boolean {
        if (vertices.size < 3) return false
        val pX = (point.x - center.x) / (scale + 1e-6f)
        val pY = -(point.y - center.y) / (scale + 1e-6f)
        
        var isInside = false
        var j = vertices.size - 1
        for (i in vertices.indices) {
            val yi = vertices[i].y
            val yj = vertices[j].y
            val xi = vertices[i].x
            val xj = vertices[j].x
            
            if (((yi > pY) != (yj > pY)) &&
                (pX < (xj - xi) * (pY - yi) / (yj - yi + 1e-9f) + xi)
            ) {
                isInside = !isInside
            }
            j = i
        }
        return isInside
    }
}

private fun DrawScope.drawSticker(sticker: StickerFace, scale: Float) {
    val centerOffset = center
    val path = Path().apply {
        moveTo(centerOffset.x + sticker.vertices[0].x * scale, centerOffset.y - sticker.vertices[0].y * scale)
        for (i in 1 until sticker.vertices.size) {
            lineTo(centerOffset.x + sticker.vertices[i].x * scale, centerOffset.y - sticker.vertices[i].y * scale)
        }
        close()
    }
    
    val baseColor = when (sticker.color) {
        CubeColor.WHITE -> Color.White
        CubeColor.YELLOW -> Color.Yellow
        CubeColor.GREEN -> Color(0xFF4CAF50)
        CubeColor.BLUE -> Color(0xFF2196F3)
        CubeColor.RED -> Color(0xFFF44336)
        CubeColor.ORANGE -> Color(0xFFFF9800)
        CubeColor.NONE -> Color.DarkGray
    }
    
    val lightDir = Vector3(0.5f, 0.5f, -1f).normalized()
    val intensity = max(0.5f, sticker.normal.dot(lightDir) * -1f)
    
    val finalColor = Color(
        red = (baseColor.red * intensity).coerceIn(0f, 1f),
        green = (baseColor.green * intensity).coerceIn(0f, 1f),
        blue = (baseColor.blue * intensity).coerceIn(0f, 1f),
        alpha = 1f
    )

    drawPath(path, finalColor, style = Fill)
    drawPath(path, Color.Black, style = Stroke(width = 2f))
}
