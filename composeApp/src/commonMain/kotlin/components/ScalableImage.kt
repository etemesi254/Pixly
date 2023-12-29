// BORROWED FROM: https://github.com/JetBrains/compose-multiplatform/tree/master/examples/imageviewer/shared/src/desktopMain/kotlin/example/imageviewer
package components

import AppContext
import ImageContext
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.withSave
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.pow
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.geometry.isSpecified
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.withLock
import kotlin.math.max
import kotlin.math.min

/**
 * Encapsulate all transformations about showing some target (an image, relative to its center)
 * scaled and shifted in some area (a window, relative to its center)
 */
class ScalableState {
    var defaultClickLimit = 3F

    /// zoom until you can see the devil
    private var zoomLimits = 0.1f..8000f

    private var offset by mutableStateOf(Offset.Zero)

    /**
     * Zoom of the target relative to the area size. 1.0 - the target completely fits the area.
     */
    var zoom by mutableStateOf(1f)
        private set

    private var areaSize: Size by mutableStateOf(Size.Unspecified)
    private var targetSize: Size by mutableStateOf(Size.Zero)

    /**
     * A transformation that should be applied to render the target in the area.
     *   offset - in pixels in the area coordinate system, should be applied before scaling
     *   scale - scale of the target in the area
     */
    val transformation: Transformation by derivedStateOf {
        Transformation(
            offset = offset,
            scale = zoomToScale(zoom)
        )
    }

    /**
     * The calculated base scale for 100% zoom. Calculated so that the target fits the area.
     */
    private val scaleFor100PercentZoom by derivedStateOf {
        if (targetSize.isSpecified && areaSize.isSpecified) {
            max(areaSize.width / targetSize.width, areaSize.height / targetSize.height)
        } else {
            1.0f
        }
    }

    /**
     * The calculated scale for full visibilitappCtx.image.image(appCtx)y of the target.
     */
    private val scaleForFullVisibility by derivedStateOf {
        if (targetSize.isSpecified && areaSize.isSpecified) {
            min(areaSize.width / targetSize.width, areaSize.height / targetSize.height)
        } else {
            1.0f
        }
    }

    private fun zoomToScale(zoom: Float) = zoom * scaleFor100PercentZoom

    /**
     * Limit the target center position, so:
     * - if the size of the target is less than area,
     *   the center of the target is bound to the center of the area
     * - if the size of the target is greater, then limit the center of it,
     *   so the target will be always in the area
     */
    fun limitTargetInsideArea(
        areaSize: Size,
        targetSize: Size,
    ) {
        this.areaSize = areaSize
        this.targetSize = targetSize
        zoomLimits = scaleForFullVisibility..zoomLimits.endInclusive
        applyLimits()
    }

    private fun applyLimits() {
        if (targetSize.isSpecified && areaSize.isSpecified) {
            val offsetXLimits = centerLimits(targetSize.width * transformation.scale, areaSize.width)
            val offsetYLimits = centerLimits(targetSize.height * transformation.scale, areaSize.height)

            zoom = zoom.coerceIn(zoomLimits)
            offset = Offset(
                offset.x.coerceIn(offsetXLimits),
                offset.y.coerceIn(offsetYLimits),
            )
        }
    }

    private fun centerLimits(targetSize: Float, areaSize: Float): ClosedFloatingPointRange<Float> {
        val areaCenter = areaSize / 2
        val targetCenter = targetSize / 2
        val extra = (targetCenter - areaCenter).coerceAtLeast(0f)
        return -extra / 2..extra / 2
    }

    fun addPan(pan: Offset) {
        offset += pan
        applyLimits()
    }

    /**
     * @param focus on which point the camera is focused in the area coordinate system.
     * After we apply the new scale, the camera should be focused on the same point in
     * the target coordinate system.
     */
    fun addZoom(zoomMultiplier: Float, focus: Offset = Offset.Zero) {
        setZoom(zoom * zoomMultiplier, focus)
    }

    /**
     * @param focus on which point the camera is focused in the area coordinate system.
     * After we apply the new scale, the camera should be focused on the same point in
     * the target coordinate system.
     */
    fun setZoom(zoom: Float, focus: Offset = Offset.Zero) {
        val newZoom = zoom.coerceIn(zoomLimits)
        val newOffset = Transformation.offsetOf(
            point = transformation.pointOf(focus),
            transformedPoint = focus,
            scale = zoomToScale(newZoom)
        )
        this.offset = newOffset
        this.zoom = newZoom
        applyLimits()
    }

    data class Transformation(
        val offset: Offset,
        val scale: Float,
    ) {
        fun pointOf(transformedPoint: Offset) = (transformedPoint - offset) / scale

        companion object {
            // is derived from the equation `point = (transformedPoint - offset) / scale`
            fun offsetOf(point: Offset, transformedPoint: Offset, scale: Float) =
                transformedPoint - point * scale
        }
    }
}

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
fun ScalableImage(ctx: ImageContext, modifier: Modifier = Modifier) {

    BoxWithConstraints {
        var areaSize = areaSize


        val interactionSource = remember { MutableInteractionSource() };

        val image = ctx.imageToDisplay().canvas()
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
                                    val im = ctx.imageToDisplay()
                                    im.protectSkiaMutex.withLock {
                                        drawImage(im.canvas())
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

private val ImageBitmap.size get() = Size(width.toFloat(), height.toFloat())

private val BoxWithConstraintsScope.areaSize
    @Composable get() = with(LocalDensity.current) {
        Size(maxWidth.toPx(), maxHeight.toPx())
    }
