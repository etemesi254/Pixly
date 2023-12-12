package components

import AppContext
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

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
            Pair("0", Color.Red.copy(0.5F)),
            Pair("1", Color.Green.copy(alpha = 0.5F)),
            Pair("2", Color.Blue.copy(alpha = 0.5F))
        )
    }


    Box(
        Modifier.fillMaxSize().drawWithCache {

            var width = this.size.width
            var height = this.size.height.toFloat()
            val singleRange = width / 256.toFloat()

            onDrawWithContent {
                for ((k, v) in array) {
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
                                alpha = 1.0F
                            )

                        }
                    }
                }
            }
        }

    ) {
    }
}

//canvas.drawRect(Rect(10F,10F,10F,10F),Paint.)


