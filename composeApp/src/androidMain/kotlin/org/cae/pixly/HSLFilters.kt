package org.cae.pixly

import AppContext
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.godaddy.android.colorpicker.HsvColor
import com.godaddy.android.colorpicker.harmony.ColorHarmonyMode
import com.godaddy.android.colorpicker.harmony.HarmonyColorPicker
import components.CollapsibleBox
import extensions.launchOnIoThread
import imageops.imageHslAdjust


@Composable
fun HslFiltersComponent(appContext: AppContext) {

    val scope = rememberCoroutineScope()
    Column( modifier = Modifier.height(200.dp).fillMaxWidth().verticalScroll(rememberScrollState())) {

        var updatedHsvColor by remember() {
            mutableStateOf(HsvColor.from(Color.Red))
        }
//        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
//
//            HarmonyColorPicker(
//                modifier = Modifier,
//                harmonyMode = ColorHarmonyMode.NONE,
//                color = updatedHsvColor,
//                onColorChanged = { hsvColor: HsvColor ->
//                    updatedHsvColor = hsvColor
//                    if (appContext.currentImageContext() != null) {
//                        val ctx = appContext.currentImageContext()!!;
//                        ctx.filterValues.hue = hsvColor.hue;
//                        ctx.filterValues.saturation = hsvColor.saturation;
//                        ctx.filterValues.lightness = hsvColor.value;
//                    }
//                    // hsvColor.hue;
//                    //onColorChanged(hsvColor)
//                },
//            )
//        }

        Box(
            modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
        ) {
            appContext.currentImageContext()?.filterValues?.hue?.let {
                AndroidSliderTextComponent(
                    "Hue",
                    it,
                    valueRange = -360F..360F,
                    decimalPattern = "##0",
                    //enabled = !appContext.isImageOperationRunning()

                ) {
                    scope.launchOnIoThread {
                        updatedHsvColor = updatedHsvColor.copy(hue = it);
                        appContext.imageHslAdjust(
                            hue = it
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
        ) {
            appContext.currentImageContext()?.filterValues?.saturation?.let {
                AndroidSliderTextComponent(
                    "Saturation",
                    it,
                    valueRange = -2f..4f,
                    scrollValueChangeBy = 0.5f,
                    decimalPattern = "#0.##",
                    // enabled = !appContext.isImageOperationRunning()

                ) {
                    scope.launchOnIoThread {
                        updatedHsvColor = updatedHsvColor.copy(saturation = it.coerceIn(0F..1F));

                        appContext.imageHslAdjust(
                            saturation = it
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
        ) {
            appContext.currentImageContext()?.filterValues?.lightness?.let {
                AndroidSliderTextComponent(
                    "Lightness",
                    it,
                    valueRange = 0f..2f,
                    scrollValueChangeBy = 0.5f,
                    decimalPattern = "#0.##",
                    //  enabled = !appContext.isImageOperationRunning()

                ) {
                    scope.launchOnIoThread {
                        updatedHsvColor = updatedHsvColor.copy(value = it.coerceIn(0F..1F));

                        appContext.imageHslAdjust(
                            lightness = it
                        )
                    }
                }
            }
        }
    }
}