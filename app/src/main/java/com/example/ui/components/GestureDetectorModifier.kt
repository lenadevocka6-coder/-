package com.example.ui.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import com.example.model.GestureType
import kotlin.math.abs

@Composable
fun Modifier.detectLauncherGestures(
    onGesture: (GestureType) -> Unit,
    onSingleTap: ((Offset) -> Unit)? = null
): Modifier {
    return this.pointerInput(Unit) {
        var lastTapTime = 0L
        var lastTapOffset = Offset.Zero

        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            val downTime = System.currentTimeMillis()
            val startPos = down.position
            var totalDragY = 0f
            var totalDragX = 0f
            var isPinching = false
            var zoomAccumulator = 1.0f
            var gestureFired = false
            var pointerCount = 1

            // Check for double tap timing
            val isDoubleTapCandidate = (downTime - lastTapTime < 350) &&
                (abs(startPos.x - lastTapOffset.x) < 80f) &&
                (abs(startPos.y - lastTapOffset.y) < 80f)

            while (true) {
                val event: PointerEvent = awaitPointerEvent(PointerEventPass.Main)
                val changes = event.changes
                val activePointers = changes.filter { it.pressed }
                pointerCount = activePointers.size

                if (activePointers.isEmpty()) {
                    // Released
                    val duration = System.currentTimeMillis() - downTime
                    if (!gestureFired) {
                        if (isDoubleTapCandidate && abs(totalDragY) < 30f && abs(totalDragX) < 30f) {
                            onGesture(GestureType.DOUBLE_TAP)
                            lastTapTime = 0L
                        } else if (duration > 550 && abs(totalDragY) < 30f && abs(totalDragX) < 30f) {
                            onGesture(GestureType.LONG_PRESS)
                            lastTapTime = 0L
                        } else if (duration < 300 && abs(totalDragY) < 25f && abs(totalDragX) < 25f) {
                            lastTapTime = downTime
                            lastTapOffset = startPos
                            onSingleTap?.invoke(startPos)
                        }
                    }
                    break
                }

                if (pointerCount >= 2) {
                    // Two fingers - Pinch detection
                    isPinching = true
                    val zoom = event.calculateZoom()
                    zoomAccumulator *= zoom

                    if (!gestureFired) {
                        if (zoomAccumulator < 0.82f) {
                            onGesture(GestureType.PINCH_IN)
                            gestureFired = true
                        } else if (zoomAccumulator > 1.22f) {
                            onGesture(GestureType.PINCH_OUT)
                            gestureFired = true
                        }
                    }
                } else if (!isPinching && !gestureFired) {
                    // 1 finger drag
                    val firstChange = activePointers.first()
                    val change = firstChange.positionChange()
                    totalDragY += change.y
                    totalDragX += change.x

                    // Check for vertical swipes (predominantly vertical)
                    if (abs(totalDragY) > 90f && abs(totalDragY) > abs(totalDragX) * 1.3f) {
                        if (totalDragY < -90f) {
                            onGesture(GestureType.SWIPE_UP)
                            gestureFired = true
                        } else if (totalDragY > 90f) {
                            onGesture(GestureType.SWIPE_DOWN)
                            gestureFired = true
                        }
                    }
                }
            }
        }
    }
}
