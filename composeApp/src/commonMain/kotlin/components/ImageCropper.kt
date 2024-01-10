package components

import AppContext
import ImageContext
import ImageContextBitmaps
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenuState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


private val BoxWithConstraintsScope.areaSize
    @Composable get() = with(LocalDensity.current) {
        Size(maxWidth.toPx(), maxHeight.toPx())
    }

private val ImageBitmap.size get() = Size(width.toFloat(), height.toFloat())


@Composable
fun ImageCroper(
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



            Box(modifier = Modifier.fillMaxSize().drawWithContent {
                drawIntoCanvas {
                    val edgesWhitePaint = Paint()
                    edgesWhitePaint.color = androidx.compose.ui.graphics.Color.White;
                    edgesWhitePaint.strokeWidth = 30F
                    edgesWhitePaint.strokeCap = StrokeCap.Square
                    edgesWhitePaint.strokeJoin = StrokeJoin.Round
                    edgesWhitePaint.style = PaintingStyle.Stroke

                    val innerWhitePaint = Paint()
                    innerWhitePaint.color = androidx.compose.ui.graphics.Color.White;
                    innerWhitePaint.strokeWidth = 10F


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

                    it.drawRect(0F, 0F, image.width.toFloat(), image.height.toFloat(), edgesWhitePaint)
                    // first draw horizontal
                    it.drawLine(
                        Offset(0F, imageSize.height.toFloat() / 3F),
                        Offset(image.width.toFloat(), imageSize.height.toFloat() / 3F),
                        innerWhitePaint
                    )
                    it.drawLine(
                        Offset(0F, imageSize.height.toFloat() * 2F / 3F),
                        Offset(image.width.toFloat(), imageSize.height.toFloat() * 2F / 3F),
                        innerWhitePaint
                    )
                    // then vertical
                    it.drawLine(
                        Offset(imageSize.width.toFloat() / 3F, 0F),
                        Offset(imageSize.width.toFloat() / 3F, image.height.toFloat()),
                        innerWhitePaint
                    )
                    it.drawLine(
                        Offset(imageSize.width.toFloat() * 2F / 3F, 0F),
                        Offset(imageSize.width.toFloat() * 2F / 3F, image.height.toFloat()),
                        innerWhitePaint
                    )
                }
            }
            ) {}
        }
    }
}
