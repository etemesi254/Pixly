package components

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

@OptIn(ExperimentalUnsignedTypes::class)
fun calculateHistogram(input: ByteArray): UIntArray {
    var output = UIntArray(256);
    input.forEach {
        output[it.toInt()] += 1u

    }
    return output;
}

@Composable
fun HistogramChart(inputs: ByteArray, color: Color, alpha: Float = 1.0F) {
    var array =  calculateHistogram(inputs);
    var len = array.size ;

    var maxRecip =  1.0F / array.max().toFloat()


    Box(
        Modifier.fillMaxSize().drawWithCache {

            var width = this.size.width
            var height = this.size.height
            val singleRange = width / len.toFloat()

            onDrawWithContent {
                for (i in 0 until len) {
                    val heightOffset = (array[i].toFloat() * maxRecip * height);
                    drawRect(
                        color,
                        topLeft = Offset(singleRange * i, height - heightOffset),
                        size = Size(singleRange, heightOffset),
                        alpha = alpha
                    )


                }
            }
        }

    ) {
    }

    //canvas.drawRect(Rect(10F,10F,10F,10F),Paint.)

}

