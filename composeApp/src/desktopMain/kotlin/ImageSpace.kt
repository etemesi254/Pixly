@file:Suppress("FunctionName")

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import desktopComponents.ScalableImage
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import java.awt.Cursor

private fun Modifier.cursorForHorizontalResize(isHorizontal: Boolean): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(if (isHorizontal) Cursor.E_RESIZE_CURSOR else Cursor.S_RESIZE_CURSOR)))

@Composable
fun ImageSpace(context: AppContext) {

    var toModify by remember { mutableStateOf(false) }
    rememberCoroutineScope().launch {
        if (context.currentImageContext() != null) {
            context.currentImageContext()!!.imageModified.collect {
                toModify = it
            }
        }
    }

    when (context.imageSpaceLayout) {
        ImageSpaceLayout.SingleLayout -> {

            // showing only one image
            Box(modifier = Modifier.fillMaxSize()) {
                val imageContext = context.currentImageContext();

                key(toModify) {
                    ScalableImage(
                        imageContext!!,
                    )
                }
            }
        }

        ImageSpaceLayout.PanedLayout -> {
            // showing two images separated by a pane
            TwoPanedImageSpace(context)
        }
    }
}

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun TwoPanedImageSpace(context: AppContext) {
    val pane = rememberSplitPaneState(0.5f)

    var toModify by remember { mutableStateOf(false) }
    rememberCoroutineScope().launch {
        if (context.currentImageContext() != null) {
            context.currentImageContext()!!.imageModified.collect {
                toModify = it
            }
        }
    }

    // ensure we have a bitmap for first canvas
    context.currentImageContext()?.canvasBitmaps?.putIfAbsent(
        ImageContextBitmaps.FirstCanvasImage,
        DesktopProtectedBitmap()
    )

    HorizontalSplitPane(modifier = Modifier.fillMaxSize(), splitPaneState = pane) {
        first {
            BoxWithConstraints(
                modifier = Modifier.backgroundForScalable(context),
                contentAlignment = Alignment.Center
            ) {
                // For some weird reason, prevents clipping
                Surface(modifier = Modifier.backgroundForScalable(context)) {
                    val imageContext = context.currentImageContext();

                    remember {

                        imageContext?.canvasBitmaps?.get(ImageContextBitmaps.FirstCanvasImage)?.let {
                            imageContext.firstImage()?.writeToCanvas(
                                it
                            )
                        };
                    }
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            "Original",
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                            style = MaterialTheme.typography.h6,
                            textAlign = TextAlign.End
                        )

                        ScalableImage(imageContext!!, imageContextBitmaps = ImageContextBitmaps.FirstCanvasImage)

                    }
                }
            }
        }
        second {
            BoxWithConstraints(
                modifier = Modifier.backgroundForScalable(context),
                contentAlignment = Alignment.Center
            ) {


                // For some weird reason, prevents clipping
                Surface(modifier = Modifier.backgroundForScalable(context)) {
                    val imageContext = context.currentImageContext();

                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            "Edited",
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                            style = MaterialTheme.typography.h6,
                            textAlign = TextAlign.Start
                        )

                        key(toModify) {
                            ScalableImage(imageContext!!)
                        }

                    }
                }
            }

        }

        splitter {

            handle {
                Box(
                    Modifier
                        .markAsHandle()
                        .cursorForHorizontalResize(true)
                        .width(3.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colors.onSurface.copy(alpha = 0.1F))
                )
            }
            visiblePart {
                Box(
                    Modifier
                        .width(3.dp)
                        .fillMaxHeight()
                        .background(Color.Transparent)
                )
            }
        }
    }

}

fun Modifier.backgroundForScalable(appContext: AppContext): Modifier {
    val color = if (appContext.showStates.showLightTheme)
        Color(
            245,
            245,
            245
        ) else Color(25, 25, 25);

    return Modifier.background(color)
}