package components

import AppContext
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import modifyOnChange

@OptIn(ExperimentalUnsignedTypes::class)
fun _calculateHistogram(input: UByteArray): MutableMap<String, FloatArray> {
    val rgb = mutableMapOf<String, FloatArray>();
    val r = FloatArray(256)
    val g = FloatArray(256)
    val b = FloatArray(256)

//    val r = rgb["r"]!!;
//    val g = rgb["g"]!!;
//    val b = rgb["b"]!!;
    input.asSequence().chunked(4).forEach {
        // FORMAT is BGRA, so ignore A and write RGB
        b[it[0].toInt()] += 1F
        g[it[1].toInt()] += 1F
        r[it[2].toInt()] += 1F
    }

    rgb["r"] = r
    rgb["g"] = g
    rgb["b"] = b


    return rgb;
}

@OptIn(ExperimentalUnsignedTypes::class)
@Composable
fun HistogramChart(ctx: AppContext) {
    var array: Map<String, LongArray> =
        remember(ctx.recomposeWidgets.rerunHistogram) { ctx.image.inner.histogram() }

    // array type is BGRA, but histogram returns 1,2,3,4
    // 0->B,1->R, 2->G
    val colors: Map<String, Color> = remember {
        mapOf(
            Pair("0", Color.Red.copy(0.7F)),
            Pair("1", Color.Green.copy(alpha = 0.7F)),
            Pair("2", Color.Blue.copy(alpha = 0.7F))
        )
    }
    // checkboxes booleans for whetheer they are matched
    val checkedIndicator: MutableMap<String, Boolean> = remember {
        mutableMapOf(
            Pair("0", true),
            Pair("1", true),
            Pair("2", true)
        )
    }

    val recip = remember { 1F / 256F };
    // force a redraw when we carried out an action
    var forceRedraw by remember { mutableStateOf(false) };

    Column(modifier = Modifier.modifyOnChange(forceRedraw)) {
        Box(
            Modifier.fillMaxWidth().height(200.dp).drawWithCache {

                var width = this.size.width
                var height = this.size.height
                val singleRange = width * recip

                onDrawWithContent {
                    for ((k, v) in array) {
                        if (checkedIndicator[k] != true) {
                            continue
                        }
                        val color2 = colors[k] ?: Color.Gray.copy(0.4F);
                        val max = v.max().toInt()
                        if (max == (ctx.image.inner.width() * ctx.image.inner.height()).toInt()) {
                            // just one point, do not redraw, just store ignore it
                            continue
                        }
                        if (max != 0) {
                            val maxRecip = 1.0F / max
                            for (i in 0 until 256) {
                                val heightOffset: Float = (v[i] * maxRecip * height);
                                drawRect(
                                    color2,
                                    topLeft = Offset(singleRange * i, height - heightOffset),
                                    size = Size(singleRange, heightOffset),
                                    alpha = 1.0F,
                                    blendMode = BlendMode.Plus
                                )

                            }
                        }
                    }
                }
            }

        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checkedIndicator["0"] ?: false,
                    onCheckedChange = {
                        checkedIndicator["0"] = it
                        forceRedraw = !forceRedraw
                    },
                    colors = CheckboxDefaults.colors(checkedColor = Color.Red)
                )
                Text("Red", style = TextStyle(fontSize = TextUnit(14F, TextUnitType.Sp)))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checkedIndicator["1"] ?: false,
                    onCheckedChange = {
                        checkedIndicator["1"] = it
                        forceRedraw = !forceRedraw


                    },
                    colors = CheckboxDefaults.colors(checkedColor = Color.Green)
                )
                Text("Green", style = TextStyle(fontSize = TextUnit(14F, TextUnitType.Sp)))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checkedIndicator["2"] ?: false, onCheckedChange = {
                    checkedIndicator["2"] = it
                    forceRedraw = !forceRedraw


                }, colors = CheckboxDefaults.colors(checkedColor = Color.Blue))
                Text("Blue ", style = TextStyle(fontSize = TextUnit(14F, TextUnitType.Sp)))
            }

        }
    }

}

//canvas.drawRect(Rect(10F,10F,10F,10F),Paint.)


