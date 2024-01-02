package desktopComponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

const val TooltipAlpha = 0.12F
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PixlyToolTip(title: String, helpfulMessage: String = "", content: @Composable () -> Unit) {
    TooltipArea(
        tooltip = {
            // composable tooltip content

            Surface(shape = RoundedCornerShape(10.dp),
                modifier = Modifier.widthIn(1.dp, 400.dp),
                border = BorderStroke(1.dp,MaterialTheme.colors.onSurface.copy(alpha = TooltipAlpha))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = title,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = TextUnit(15.0F, TextUnitType.Sp)
                    )

                    if (helpfulMessage !== "") {

                        Divider(modifier = Modifier.padding(vertical = 5.dp))
                        Text(
                            helpfulMessage,
                            fontSize = TextUnit(12.0F, TextUnitType.Sp)
                        )
                    }
                }
            }
        },
        delayMillis = 1300, content = content
    )
}