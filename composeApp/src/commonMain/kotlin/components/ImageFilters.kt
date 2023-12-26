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
                    appContext.imageFilterValues()?.brightness?.let {
                        SliderTextComponent(
                            "Brighten",
                            it,
                            valueRange = -100F..100F,
                            decimalPattern = "##0",
                            enabled = !appContext.isImageOperationRunning()
                        ) {

                            image.brighten(appContext, scope, it)
                        }
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    appContext.imageFilterValues()?.let {
                        SliderTextComponent(
                            "Contrast",
                            it.contrast,
                            valueRange = -100F..100F,
                            decimalPattern = "#0",
                            enabled = !appContext.isImageOperationRunning()

                        ) {

                            image.contrast(appContext, scope, it)
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    appContext.imageFilterValues()?.let {
                        SliderTextComponent(
                            "Exposure", it.exposure,
                            valueRange = -1F..1F,
                            decimalPattern = "0.00",
                            scrollValueChangeBy = 0.01F,
                            enabled = !appContext.isImageOperationRunning()

                        ) {

                            image.exposure(appContext, scope, it)
                        }
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

                        IconButton(
                            onClick = {

                                appContext.getImage().verticalFlip(appContext, scope)
                            }, enabled = !appContext.isImageOperationRunning()
                        ) {
                            Icon(
                                painter = painterResource("flip-vertical-svgrepo-com.svg"),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)

                            )
                        }

                        IconButton(
                            onClick = {

                                if (appContext.imageIsLoaded()) {
                                    appContext.getImage().flop(appContext, scope)
                                }
                            }, enabled = !appContext.isImageOperationRunning()
                        ) {
                            Icon(
                                painter = painterResource("flip-horizontal-svgrepo-com.svg"),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)

                            )
                        }
                        IconButton(
                            onClick = {
                                // add to history
                                appContext.getImage().transpose(appContext, scope)
                            }, enabled = !appContext.isImageOperationRunning()
                        ) {
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
                appContext.imageFilterValues()?.stretchContrastRange?.value?.let {
                    RangeSliderTextComponent(
                        value = it,
                        valueRange = 0F..256F,
                        decimalPattern = "##0",
                        enabled = !appContext.isImageOperationRunning()

                    ) { value ->

                        appContext.getImage().stretchContrast(appContext, scope, value)
                    }
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
                        decimalPattern = "##0",
                        enabled = !appContext.isImageOperationRunning()

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
                        decimalPattern = "#0",
                        enabled = !appContext.isImageOperationRunning()

                    ) {
                        appContext.getImage().gaussianBlur(appContext, scope, it.toLong())
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    SliderTextComponent(
                        "Median Blur",
                        appContext.currentImageState().filterValues.medianBlur.toFloat(),
                        valueRange = 0F..255F,
                        decimalPattern = "#0",
                        enabled = !appContext.isImageOperationRunning()

                    ) {
                        appContext.getImage().medianBlur(appContext, scope, it.toLong())
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    SliderTextComponent(
                        "Bilateral Blur",
                        appContext.currentImageState().filterValues.bilateralBlur.toFloat(),
                        valueRange = 0F..255F,
                        decimalPattern = "#0",
                        enabled = !appContext.isImageOperationRunning()

                    ) {
                        appContext.getImage().bilateralBlur(appContext, scope, it.toLong())
                    }
                }
            }
        }
    }
}


@Composable
fun HslFiltersComponent(appContext: AppContext) {
    Box(modifier = Modifier.padding(vertical = 10.dp)) {
        CollapsibleBox("Hue, Saturation, Lightness", appContext.showStates.showHslFilters, {
            appContext.showStates.showHslFilters = !appContext.showStates.showHslFilters
        }) {
            val scope = rememberCoroutineScope()
            Column {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    SliderTextComponent(
                        "Hue",
                        appContext.currentImageState().filterValues.hue,
                        valueRange = -360F..360F,
                        decimalPattern = "##0",
                        enabled = !appContext.isImageOperationRunning()

                    ) {
                        appContext.getImage().hslAdjust(
                            appContext,
                            scope,
                            it,
                            appContext.currentImageState().filterValues.saturation,
                            appContext.currentImageState().filterValues.lightness
                        )
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    SliderTextComponent(
                        "Saturation",
                        appContext.currentImageState().filterValues.saturation,
                        valueRange = -2f..4f,
                        scrollValueChangeBy = 0.5f,
                        decimalPattern = "#0.##",
                        enabled = !appContext.isImageOperationRunning()

                    ) {
                        appContext.getImage().hslAdjust(
                            appContext,
                            scope,
                            appContext.currentImageState().filterValues.hue,
                            it,
                            appContext.currentImageState().filterValues.lightness
                        )
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    SliderTextComponent(
                        "Lightness",
                        appContext.currentImageState().filterValues.lightness,
                        valueRange = 0f..2f,
                        scrollValueChangeBy = 0.5f,
                        decimalPattern = "#0.##",
                        enabled = !appContext.isImageOperationRunning()

                    ) {

                        appContext.getImage().hslAdjust(
                            appContext,
                            scope,
                            appContext.currentImageState().filterValues.hue,
                            appContext.currentImageState().filterValues.saturation,
                            it
                        )
                    }
                }
            }
        }
    }
}