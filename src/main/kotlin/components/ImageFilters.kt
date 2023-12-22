package components

import AppContext
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import history.HistoryOperationsEnum
import kotlinx.coroutines.CoroutineScope
import modifyOnChange


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LightFiltersComponent(appContext: AppContext) {
    val image = appContext.getImage()

    Box(modifier = Modifier.padding(vertical = 10.dp)) {
        val scope = rememberCoroutineScope();

        CollapsibleBox(title = "Light", appContext.showStates.showLightFilters, {
            appContext.showStates.showLightFilters = appContext.showStates.showLightFilters.xor(true)
        }) {
            Column() {

                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    SliderTextComponent(
                        "Brighten",
                        appContext.imageFilterValues().brightness,
                        modifier = Modifier.modifyOnChange(appContext.recomposeWidgets.rerunImageSpecificStates),
                        valueRange = 0F..255F,
                        decimalPattern = "##0"
                    ) {

                        image.brighten(appContext, scope, it)
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    SliderTextComponent(
                        "Contrast",
                        appContext.imageFilterValues().contrast,
                        valueRange = 0F..255F,
                        decimalPattern = "#0"
                    ) {

                        image.contrast(appContext, scope, it)
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    SliderTextComponent(
                        "Gamma", appContext.imageFilterValues().gamma,
                        valueRange = -5F..5F,
                        decimalPattern = "0.00",
                        scrollValueChangeBy = 0.02F
                    ) {

                        appContext.appendToHistory(HistoryOperationsEnum.Gamma, it)
                        // gamma works in a weird way, higher gamma
                        // is a darker image, which beats the logic of the
                        // slider since we expect higher gamma to be a brighter
                        // image, so just invert that here
                        // this makes higher gamma -> brighter images
                        // smaller gamma -> darker images
                        image.gamma(appContext, scope, (-1 * it) + 2.3F)
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    SliderTextComponent(
                        "Exposure", appContext.imageFilterValues().exposure,
                        valueRange = -1F..1F,
                        decimalPattern = "0.00",
                        scrollValueChangeBy = 0.01F
                    ) {

                        image.exposure(appContext, scope, it + 1F)
                    }
                }
            }
        }
    }

}

@Composable
fun OrientationFiltersComponent(appContext: AppContext) {

    Box(modifier = Modifier.padding(vertical = 10.dp)) {
        CollapsibleBox("Orientation", appContext.showStates.showOrientationFilters, {
            appContext.showStates.showOrientationFilters = appContext.showStates.showOrientationFilters.xor(true)
        }) {
            val scope = rememberCoroutineScope()
            Column {
                Box(modifier = Modifier.fillMaxWidth().padding(10.dp)) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        IconButton(onClick = {

                            appContext.getImage().verticalFlip(appContext, scope)
                        }) {
                            Icon(
                                painter = painterResource("flip-vertical-svgrepo-com.svg"),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)

                            )
                        }

                        IconButton(onClick = {

                            appContext.getImage().flop(appContext, scope)
                        }) {
                            Icon(
                                painter = painterResource("flip-horizontal-svgrepo-com.svg"),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)

                            )
                        }
                        IconButton(onClick = {
                            // add to history
                            appContext.getImage().transpose(appContext, scope)
                        }) {
                            Icon(
                                painter = painterResource("transpose-svgrepo-com.png"),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)

                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun LevelsFiltersComponent(appContext: AppContext) {
    Box(modifier = Modifier.padding(vertical = 10.dp)) {
        CollapsibleBox("Levels", appContext.showStates.showLevels, {
            appContext.showStates.showLevels = appContext.showStates.showLevels.not()
        }) {
            val scope = rememberCoroutineScope()
            Column {
                Box(modifier = Modifier.height(100.dp))
                {
                    //HistogramChart(ctx = appContext)
                }
                RangeSliderTextComponent(
                    value = 0F..256F,
                    valueRange = 0F..256F,
                    decimalPattern = "##0"
                ) {

                    appContext.getImage().stretchContrast(appContext, scope, it)
                }
            }
        }
    }
}

@Composable
fun BlurFiltersComponent(appContext: AppContext) {
    Box(modifier = Modifier.padding(vertical = 10.dp)) {
        CollapsibleBox("Blur", appContext.showStates.showBlurFilters, {
            appContext.showStates.showBlurFilters = !appContext.showStates.showBlurFilters
        }) {
            val scope = rememberCoroutineScope()
            Column {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    SliderTextComponent(
                        "Box Blur",
                        appContext.currentImageState().filterValues.boxBlur.toFloat(),
                        valueRange = 0F..255F,
                        decimalPattern = "##0"
                    ) {
                        appContext.getImage().boxBlur(appContext, scope, it.toLong())
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    SliderTextComponent(
                        "Gaussian Blur",
                        appContext.currentImageState().filterValues.gaussianBlur.toFloat(),
                        valueRange = 0F..255F,
                        decimalPattern = "#0"
                    ) {
                        appContext.getImage().gaussianBlur(appContext, scope, it.toLong())
                    }
                }
//                ColorPicker {
//                    println(it)
//                }
            }
        }
    }
}