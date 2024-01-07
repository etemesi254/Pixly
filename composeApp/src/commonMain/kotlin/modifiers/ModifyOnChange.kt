package modifiers

import androidx.compose.ui.Modifier

/** A stub modifier that can be used to tell compose to
 * rebuild a widget
 * */
fun Modifier.modifyOnChange(modified: Boolean): Modifier {
    return this
}

fun Modifier.modifyIf(modifier: Modifier, yes: () -> Boolean): Modifier {
    return if (yes()) {
        this.then(modifier)
    } else {
        this
    }
}