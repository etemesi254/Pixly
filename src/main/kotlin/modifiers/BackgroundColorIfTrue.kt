package modifiers

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


/**
 * A simple modifier that changes background color based on whether
 * a condition is true or not
 *
 * If condition is true, it changes the color, otherwise it doesn't
 * */
fun Modifier.backgroundColorIfCondition(color: Color, condition: () -> Boolean): Modifier {

    return if (condition()) {
        Modifier.background(color);
    } else {
        this
    }
}