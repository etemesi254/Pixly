package components

import androidx.compose.animation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


/**
 * Create a collapsible widget that can show the child contents based
 * on a boolean true or false
 *
 * Builds layouts like
 *
 *
 * When visibility is false
 * ```text
 *    ┌──────────────────────────────────────────────────┐
 *    │  TITLE                                       ►   │
 *    └──────────────────────────────────────────────────┘
 * ```
 * When visibility is true
 * ```text
 *     ┌──────────────────────────────────────────────────┐
 *     │                                                  │
 *     │   TITLE                                     ▼    │
 *     │  ┌────────────────────────────────────────────┐  │
 *     │  │                                            │  │
 *     │  └────────────────────────────────────────────┘  │
 *     └──────────────────────────────────────────────────┘
 *```
 *
 *  @param title: Title to be shown in the layout
 *  @param visible: Whether to show contents of collapsed box or not
 *  @param onTopRowClicked: A callback called when the top row is clicked
 *  @param content: Child content for [AnimatedVisibility]
 *
 *
 */
@Composable
fun CollapsibleBox(
    title: String,
    visible: Boolean,
    onTopRowClicked: () -> Unit,
    content: @Composable() (AnimatedVisibilityScope.() -> Unit)
) {

    val density = LocalDensity.current
    Box(
        modifier = Modifier.fillMaxWidth()
            //.background(Color.Gray)
            .border(
                Dp.Hairline, Color(0x3F888888), shape = RoundedCornerShape(2)
            )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable {
                    onTopRowClicked()
                }, horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, modifier = Modifier.padding(5.dp))

                Icon(
                    if (visible) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                    contentDescription = null
                )

            }
            AnimatedVisibility(visible = visible,
                enter = slideInVertically {
                // Slide in from 40 dp from the top.
                with(density) { -40.dp.roundToPx() }
            } + expandVertically(
                // Expand from the top.
                expandFrom = Alignment.Top
            ) + fadeIn(
                // Fade in with the initial alpha of 0.3f.
                initialAlpha = 0.3f
            ), exit = slideOutVertically() + shrinkVertically() + fadeOut(), content = content)
        }

    }
}