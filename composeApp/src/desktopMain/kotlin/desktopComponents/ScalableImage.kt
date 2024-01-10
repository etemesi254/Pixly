// BORROWED FROM: https://github.com/JetBrains/compose-multiplatform/tree/master/examples/imageviewer/shared/src/desktopMain/kotlin/example/imageviewer
package desktopComponents

import ImageContext
import ImageContextBitmaps
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenuState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.contextMenuOpenDetector
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.pow
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.withLock
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skiko.toImage


fun Modifier.onPointerEvent(
    eventType: PointerEventType,
    pass: PointerEventPass = PointerEventPass.Main,
    onEvent: AwaitPointerEventScope.(event: PointerEvent) -> Unit
): Modifier = composed {
    val currentEventType by rememberUpdatedState(eventType)
    val currentOnEvent by rememberUpdatedState(onEvent)
    pointerInput(pass) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(pass)
                if (event.type == currentEventType) {
                    currentOnEvent(event)
                }
            }
        }
    }
}

/**
 * Initial zoom of the image. 1.0f means the image fully fits the window.
 */
private const val INITIAL_ZOOM = 1.0f

/**
 * This zoom means that the image isn't significantly zoomed for the user yet.
 */
private const val SLIGHTLY_INCREASED_ZOOM = 1.5f

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScalableImage(
    ctx: ImageContext,
    modifier: Modifier = Modifier,
    imageContextBitmaps: ImageContextBitmaps = ImageContextBitmaps.CurrentCanvasImage
) {


    BoxWithConstraints(modifier) {
        val areaSize = areaSize

        val interactionSource = remember { MutableInteractionSource() };

        val canvasBitmap = ctx.canvasBitmaps[imageContextBitmaps];
        if (canvasBitmap != null) {
            val image = canvasBitmap.asImageBitmap()
            val imageSize = image.size
            val menu = remember { DropdownMenuState(initialStatus = DropdownMenuState.Status.Closed) }

            val imageCenter = Offset(image.width / 2f, image.height / 2f)
            val areaCenter = Offset(areaSize.width / 2f, areaSize.height / 2f)

            Box(
                modifier = Modifier.contextMenuOpenDetector(menu).fillMaxSize(),

                ) {
                Box(
                    modifier
                        .fillMaxSize()
                        .clickable(interactionSource = interactionSource, indication = null) {
                            // https://stackoverflow.com/questions/66703448/how-to-disable-ripple-effect-when-clicking-in-jetpack-compose
                            //appContext.showStates.showPopups = !appContext.showStates.showPopups
                        }
                        .drawWithContent {
                            drawIntoCanvas {
                                it.withSave {
                                    it.translate(areaCenter.x, areaCenter.y)
                                    it.translate(
                                        ctx.zoomState.transformation.offset.x,
                                        ctx.zoomState.transformation.offset.y
                                    )
                                    it.scale(
                                        ctx.zoomState.transformation.scale,
                                        ctx.zoomState.transformation.scale
                                    )
                                    it.translate(-imageCenter.x, -imageCenter.y)
                                    runBlocking {
                                        canvasBitmap.mutex().withLock {
                                            drawImage(canvasBitmap.asImageBitmap())
                                        }

                                    }
                                }
                            }
                        }
                        .clipToBounds()
                        //.transformable(state)
                        .pointerInput(Unit) {

                            detectTransformGestures { centroid, pan, zoom, _ ->
                                ctx.zoomState.addPan(pan)
                                ctx.zoomState.addZoom(zoom, centroid - areaCenter)
                            }
                        }
                        .onPointerEvent(PointerEventType.Scroll) {
                            val centroid = it.changes[0].position
                            val delta = it.changes[0].scrollDelta
                            val zoom = 1.2f.pow(-delta.y)
                            ctx.zoomState.addZoom(zoom, centroid - areaCenter)
                        }

                        .pointerInput(Unit) {
                            detectTapGestures(onDoubleTap = { position ->
                                // If a user zoomed significantly, the zoom should be the restored on double tap,
                                // otherwise the zoom should be increased
                                ctx.zoomState.setZoom(
                                    if (ctx.zoomState.zoom > SLIGHTLY_INCREASED_ZOOM) {
                                        INITIAL_ZOOM
                                    } else {
                                        ctx.zoomState.defaultClickLimit
                                    },
                                    position - areaCenter
                                )
                            })
                        },
                )

                SideEffect {
                    ctx.zoomState.limitTargetInsideArea(areaSize, imageSize)
                }
            }
        }
    }
}

private val ImageBitmap.size get() = Size(width.toFloat(), height.toFloat())

private val BoxWithConstraintsScope.areaSize
    @Composable get() = with(LocalDensity.current) {
        Size(maxWidth.toPx(), maxHeight.toPx())
    }
