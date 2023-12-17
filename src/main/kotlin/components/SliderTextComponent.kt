package components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.unit.dp
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
 * @param scrollValueChangeBy: The delta amount a scroll event should change the filter by
 *
 * */
@Composable
fun SliderTextComponent(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    decimalPattern: String = "###0.#########",
    scrollValueChangeBy: Float = 1.0F,
    onValueChange: (Float) -> Unit,
) {

    val df = remember { DecimalFormat(decimalPattern) };
    // contains whatever value the slider is pointing to
    var sliderValue by mutableStateOf(value)
    // value shown in the text-field
    var shownValue by mutableStateOf(df.format(sliderValue))


    Column(modifier = modifier) {
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
                        // we depend on this crashing
                        // to catch invalid inputs in the slider
                        // so DON'T remove
                        it.toFloat()
                        val newValue = df.parse(it).toFloat()
                        sliderValue = newValue;
                        shownValue = it

                        onValueChange(sliderValue)
                    } catch (_: Exception) {
                    }
                },
                textStyle = MaterialTheme.typography.caption.copy(MaterialTheme.colors.onBackground),
                singleLine = true,
                modifier = Modifier.border(
                    1.dp, Color.Gray, shape = RoundedCornerShape(20)
                )
                    .padding(5.dp)
                    .onKeyEvent {
                        if (it.key == Key.DirectionUp || it.key == Key.DirectionDown) {
                            if (it.key == Key.DirectionUp) {

                                sliderValue += scrollValueChangeBy
                            } else {
                                sliderValue -= scrollValueChangeBy
                            }
                            // clamp to range allowed
                            sliderValue = sliderValue.coerceIn(valueRange)

                            shownValue = df.format(sliderValue)
                            onValueChange(sliderValue)
                        }
                        false
                    }
                    .onPointerEvent(PointerEventType.Scroll, pass = PointerEventPass.Main) {
                        val delta = it.changes[0].scrollDelta
                        if (delta.y < 0.0) {

                            sliderValue += scrollValueChangeBy
                        } else {
                            sliderValue -= scrollValueChangeBy
                        }
                        // clamp to range allowed
                        sliderValue = sliderValue.coerceIn(valueRange)

                        shownValue = df.format(sliderValue)
                        onValueChange(sliderValue)
                    })
        }
        Slider(
            value = sliderValue,
            modifier = Modifier.fillMaxWidth()
                .onPointerEvent(PointerEventType.Scroll, pass = PointerEventPass.Main) {
                    val delta = it.changes[0].scrollDelta
                    if (delta.y < 0.0) {

                        sliderValue += scrollValueChangeBy
                    } else {
                        sliderValue -= scrollValueChangeBy
                    }
                    // clamp to range allowed
                    sliderValue = sliderValue.coerceIn(valueRange)

                    shownValue = df.format(sliderValue)
                    onValueChange(sliderValue)
                }, onValueChange = {
                sliderValue = it;
                shownValue = df.format(sliderValue)
                onValueChange(sliderValue)

            }, valueRange = valueRange, colors = SliderDefaults.colors()
        )

    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RangeSliderTextComponent(
    value: ClosedFloatingPointRange<Float>,
    valueRange: ClosedFloatingPointRange<Float>,
    decimalPattern: String = "###0.#########",
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,

    ) {

    val df = remember { DecimalFormat(decimalPattern) };
    // contains the parsed and to be displayed current value
    var currentValue by remember { mutableStateOf(value.start) }
    var endValue by remember { mutableStateOf(value.endInclusive) }
    // contains whatever value the slider is pointing to
    var sliderValue by remember { mutableStateOf(value) }
    // value shown in the text-field
    var shownStartValue by remember { mutableStateOf(df.format(currentValue)) }
    var shownEndValue by remember { mutableStateOf(df.format(endValue)) }



    Column {

        RangeSlider(
            value = sliderValue, modifier = Modifier.fillMaxWidth(), onValueChange = {
                sliderValue = it;
                // currentValue = itonValueChange
                // shownValue = df.format(currentValue)
                shownStartValue = df.format(it.start)
                shownEndValue = df.format(it.endInclusive)
                onValueChange(it)

            }, valueRange = valueRange, colors = SliderDefaults.colors()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            BasicTextField(
                value = shownStartValue,
                onValueChange = {

                    try {
                        shownEndValue = it
                    } catch (_: Exception) {
                    }
                },
                textStyle = MaterialTheme.typography.caption.copy(MaterialTheme.colors.onBackground),
                singleLine = true,
                modifier = Modifier.border(
                    1.dp, Color.Gray, shape = RoundedCornerShape(20)
                ).padding(5.dp).width(35.dp)

            )
            BasicTextField(
                value = shownEndValue,
                onValueChange = {

                    try {
                        shownEndValue = it
                    } catch (_: Exception) {
                    }
                },
                textStyle = MaterialTheme.typography.caption.copy(MaterialTheme.colors.onBackground),
                singleLine = true,
                modifier = Modifier.border(
                    1.dp, Color.Gray, shape = RoundedCornerShape(20)
                ).padding(5.dp).width(35.dp),
            )
        }
    }
}