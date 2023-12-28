import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import java.awt.Cursor

private fun Modifier.cursorForHorizontalResize(isHorizontal: Boolean): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(if (isHorizontal) Cursor.E_RESIZE_CURSOR else Cursor.S_RESIZE_CURSOR)))

@Composable
fun ImageSpace(context: AppContext) {

    when (context.imageSpaceLayout) {
        ImageSpaceLayout.SingleLayout -> {
            // showing only one image
            Box(modifier = Modifier.fillMaxSize()) {
                context.getImage().image(context)
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

    HorizontalSplitPane(modifier = Modifier.fillMaxSize(), splitPaneState = pane) {
        first {
            BoxWithConstraints(
                modifier = Modifier.backgroundForScalable(context),
                contentAlignment = Alignment.Center
            ) {
                // For some weird reason, prevents clipping
                Surface {
                    context.getImage().image(context)
                }


            }
        }
        second {
            BoxWithConstraints(
                modifier = Modifier.backgroundForScalable(context),
                contentAlignment = Alignment.Center
            ) {


                // For some weird reason, prevents clipping
                Surface {
                    context.getImage().image(context)
                }
                //Image(context.getImage().canvas(), contentDescription = null)
            }

        }

        splitter {

            handle {
                Box(
                    Modifier
                        .markAsHandle()
                        .cursorForHorizontalResize(true)
                        .width(7.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colors.onSurface.copy(alpha = 0.1F))
                )
            }
            visiblePart {
                Box(
                    Modifier
                        .width(5.dp)
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