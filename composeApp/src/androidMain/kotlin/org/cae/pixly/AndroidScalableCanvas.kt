package org.cae.pixly

import AppContext
import ImageContext
import ImageContextBitmaps
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.withLock
import modifiers.modifyIf
import modifiers.modifyOnChange


/**
 * Initial zoom of the image. 1.0f means the image fully fits the window.
 */
private const val INITIAL_ZOOM = 1.0f

/**
 * This zoom means that the image isn't significantly zoomed for the user yet.
 */
private const val SLIGHTLY_INCREASED_ZOOM = 1.5f

@Composable
fun AndroidScalableImage(
    appCtx: AppContext,
    modifier: Modifier = Modifier,
    imageContextBitmaps: ImageContextBitmaps = ImageContextBitmaps.CurrentCanvasImage
) {

    BoxWithConstraints(modifier) {
        val areaSize = areaSize

        val ctx by remember(appCtx.imFile) {
            mutableStateOf(appCtx.imageSpecificStates[appCtx.imFile]!!)
        };

        val canvasBitmap = ctx.canvasBitmaps[imageContextBitmaps];
        if (canvasBitmap != null) {
            val image = canvasBitmap.asImageBitmap()
            val imageSize = image.size

            val imageCenter = Offset(image.width / 2f, image.height / 2f)
            val areaCenter = Offset(areaSize.width / 2f, areaSize.height / 2f)

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Box(
                    modifier
                        .fillMaxSize()
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
                        .pointerInput(Unit) {

                            detectTransformGestures { centroid, pan, zoom, _ ->
                                // BUG: Do not use ctx here, it is captured in context
                                // and never updates when you change the image.
                                //
                                // Mahn that took me some hours
                                if (appCtx.currentImageContext() != null) {
                                    appCtx.currentImageContext()!!.zoomState.addPan(pan)
                                    appCtx.currentImageContext()!!.zoomState.addZoom(zoom, centroid - areaCenter)
                                }
                            }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(onDoubleTap = { position ->
                                // If a user zoomed significantly, the zoom should be the restored on double tap,
                                // otherwise the zoom should be increased
                                if (appCtx.currentImageContext() !=null) {
                                    val ctx = appCtx.currentImageContext()!!;
                                    ctx.zoomState.setZoom(
                                        if (ctx.zoomState.zoom > SLIGHTLY_INCREASED_ZOOM) {
                                            ctx.zoomState.scaleFor100PercentZoom
                                        } else {
                                            ctx.zoomState.defaultClickLimit
                                        },
                                        position - areaCenter
                                    )
                                }
                            }) { }
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
