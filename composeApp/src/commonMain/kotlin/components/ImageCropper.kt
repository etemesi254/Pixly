package components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun ImageCroper(modifier: Modifier = Modifier, content: @Composable () -> Unit) {


    BoxWithConstraints(modifier) {


        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        val minSize = 300f
        var width by remember { mutableStateOf(minSize) }
        var height by remember { mutableStateOf(minSize) }

        val widthInDp: Dp
        val heightInDp: Dp

        with(LocalDensity.current) {
            widthInDp = width.toDp()
            heightInDp = height.toDp()
        }


        val imageWidth = constraints.maxWidth
        val imageHeight = constraints.maxHeight

        content()

        BoxWithConstraints(
            modifier = Modifier
                .offset {
                    IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
                }
                .size(widthInDp, heightInDp)
        ) {
            //      move the overlay
            Box(
                Modifier
                    .background(Color.Black.copy(0.6f))
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            offsetX = (offsetX + dragAmount.x).coerceIn(
                                0f,
                                (imageWidth - width).coerceAtLeast(0f)
                            )
                            offsetY = (offsetY + dragAmount.y).coerceIn(
                                0f,
                                (imageHeight - height).coerceAtLeast(0f)
                            )
                        }
                    }
            )
            //      resize the overlay

            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .background(Color.White)
                    .height(20.dp)
                    .width(20.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            width = (width + dragAmount.x).coerceIn(
                                minSize,
                                (imageWidth - offsetX).coerceAtLeast(minSize)
                            )
                            height = (height + dragAmount.y).coerceIn(
                                minSize,
                                (imageHeight - offsetY).coerceAtLeast(minSize)
                            )

                        }
                    }
            )
        }
    }
}
