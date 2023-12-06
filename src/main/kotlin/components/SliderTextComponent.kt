package components

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * A component that combines a slider with a text-field
 * and an optional label next to it
 *
 *
 *
 * Builds layouts like
 *```
 *     ┌────────────────────────────────────┐
 *     │                                    │
 *     │                     ┌────────────┐ │
 *     │  LABEL              │  TEXFIELD  │ │
 *     │                     └────────────┘ │
 *     │                                    │
 *     │                           ┌┐       │
 *     │  ─────────────────────────┼┼─────  │
 *     │                           └┘       │
 *     │           SLIDER                   │
 *     └────────────────────────────────────┘
 *
 *```
 *
 * @param label: String containing the label to be displayed at <LABEL>
 * @param value: Initial value to give the text field and slider, range is 0..1
 * @param onValueChange: A callback to call when the value has changed, the value returned is `(value-offset)*scale`
 * @param decimalPattern: The decimal pattern to use for the scale and text field
 *
 * */
@Composable
fun SliderTextComponent(
    label: String,
    value: Float,
    valueRange:ClosedFloatingPointRange<Float>,
    decimalPattern: String = "###0.#########",
    onValueChange: (Float) -> Unit,

    ) {

    val df = remember { DecimalFormat(decimalPattern) };
    // contains the parsed and to be displayed current value
    var currentValue by remember { mutableStateOf(value) }
    // contains whatever value the slider is pointing to
    var sliderValue by remember { mutableStateOf(value) }
    // value shown in the text-field
    var shownValue by remember { mutableStateOf(df.format(currentValue)) }


    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label)
            BasicTextField(
                value = shownValue,
                onValueChange = {

                    try {
                        val newValue = df.parse(it).toFloat()
                        sliderValue = newValue ;
                        currentValue = newValue
                        shownValue = it

                        onValueChange(currentValue)
                    } catch (_: Exception) {
                    }
                },
                textStyle = MaterialTheme.typography.caption.copy(MaterialTheme.colors.onBackground),
                singleLine = true,
                modifier = Modifier.border(
                    1.dp,
                    Color.Gray,
                    shape = RoundedCornerShape(20)
                ).padding(5.dp),

                )
        }
        Slider(
            value = sliderValue,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                sliderValue = it;
                currentValue = it
                shownValue = df.format(currentValue)
                onValueChange(currentValue)

            },
            valueRange = valueRange,
            colors = SliderDefaults.colors()
        )

    }
}