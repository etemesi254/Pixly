package components

import AppContext
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlin.math.roundToInt

@Composable
fun TopHoveringIcons(appContext: AppContext){

    val density = LocalDensity.current

    val visible = appContext.showStates.showPopups
    Box() {
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        Box(modifier = Modifier.fillMaxWidth()
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = slideInVertically {
                        // Slide in from 40 dp from the top.
                        with(density) { -20.dp.roundToPx() }
                    } + expandVertically(
                        // Expand from the top.
                        expandFrom = Alignment.Top
                    ) + fadeIn(
                        // Fade in with the initial alpha of 0.3f.
                        initialAlpha = 0.3f
                    ),
                    exit = slideOutVertically() + shrinkVertically() + fadeOut()) {
                    Popup(alignment = Alignment.TopCenter) {
                        // NITPICK: To apply offset, you need a new box,
                        // You can't apply it into the nested box as it will just do a nasty adjustment
                        Box(modifier = Modifier.offset(y = 10.dp)) {
                            Box(
                                modifier = Modifier.background(Color(0x2F_00_00_00))
                                    .padding(horizontal = 10.dp)
                                    .clip(
                                        shape = RoundedCornerShape(80)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(
                                        10.dp,
                                        Alignment.CenterHorizontally
                                    ),
                                ) {
                                    IconButton(onClick = {

                                    }) {
                                        Image(
                                            painter = painterResource("undo-svgrepo.svg"),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    IconButton(onClick = {

                                    }) {
                                        Image(
                                            painter = painterResource("heart-svgrepo.svg"),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    IconButton(onClick = {

                                        appContext.showStates.showSaveDialog = true
                                    }) {
                                        Image(
                                            painter = painterResource("save-svgrepo.svg"),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    IconButton(onClick = {}) {
                                        Image(
                                            painter = painterResource("delete-svgrepo.svg"),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    IconButton(onClick = {
                                        appContext.openedRightPane= RightPaneOpened.InformationPanel
                                    }) {
                                        Image(
                                            painter = painterResource("info-svgrepo.svg"),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}