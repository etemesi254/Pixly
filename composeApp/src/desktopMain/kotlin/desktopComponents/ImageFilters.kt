package desktopComponents

import AppContext
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import components.CollapsibleBox
import components.HistogramChart
import extensions.launchOnIoThread
import imageops.*


@Composable
fun LightFiltersComponent(appContext: AppContext) {

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
                            //enabled = !appContext.isImageOperationRunning()
                        ) {
                            scope.launchOnIoThread {
                                appContext.imageBrighten(it)
                            }

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
                            //  enabled = !appContext.isImageOperationRunning()

                        ) {

                            scope.launchOnIoThread {
                                appContext.imageContrast(it)
                            }
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
//                            enabled = !appContext.isImageOperationRunning()

                        ) {

                            scope.launchOnIoThread {
                                appContext.imageExposure(it)
                            }
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

                                scope.launchOnIoThread {
                                    appContext.imageVerticalFlip()
                                }
                            },
                            //enabled = !appContext.isImageOperationRunning()
                        ) {
                            Icon(
                                painter = painterResource("flip-vertical-svgrepo-com.svg"),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)

                            )
                        }

                        IconButton(
                            onClick = {

                                scope.launchOnIoThread {
                                    appContext.imageHorizontalFlip()
                                }

                            },
                            //enabled = !appContext.isImageOperationRunning()
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
                                scope.launchOnIoThread {
                                    appContext.imageTranspose()
                                }
                            },
                            //enabled = !appContext.isImageOperationRunning()
                        ) {
                            Icon(
                                painter = painterResource("transpose-svgrepo-com.png"),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)

                            )
                        }
                    }
                }
                Divider()
                Box(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        IconButton(
                            onClick = {

                                scope.launchOnIoThread {
                                    appContext.imageRotate(90.0F)
                                }
                            },
                            //enabled = !appContext.isImageOperationRunning()
                        ) {
                            Icon(
                                painter = painterResource("rotate-90.svg"),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp).rotate(90.0F)
                            )
                        }

                        IconButton(
                            onClick = {

                                scope.launchOnIoThread {
                                    appContext.imageRotate(180.0F)
                                }

                            },
                            //enabled = !appContext.isImageOperationRunning()
                        ) {
                            Icon(
                                painter = painterResource("rotate-180.svg"),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp).rotate(180.0F)

                            )
                        }
                        IconButton(
                            onClick = {
                                // add to history
                                scope.launchOnIoThread {
                                    appContext.imageRotate(270.0F);
                                }
                            },
                            //enabled = !appContext.isImageOperationRunning()
                        ) {
                            Icon(
                                painter = painterResource("rotate-270.svg"),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp).rotate(270.0F)

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
                Box(modifier = Modifier.height(150.dp))
                {
                    HistogramChart(
                        ctx = appContext,
                        showIndicators = false,
                        min = appContext.imageFilterValues()!!.stretchContrastRange.value.start.toInt(),
                        maxV = appContext.imageFilterValues()!!.stretchContrastRange.value.endInclusive.toInt()

                    )
                }
                appContext.imageFilterValues()?.stretchContrastRange?.value?.let {
                    RangeSliderTextComponent(
                        value = it,
                        valueRange = 0F..256F,
                        decimalPattern = "##0",
                        //enabled = !appContext.isImageOperationRunning()

                    ) { value ->
                        scope.launchOnIoThread {
                            appContext.imageStretchContrast(value)
                        }
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
                    appContext.currentImageContext()?.filterValues?.boxBlur?.toFloat()?.let {
                        SliderTextComponent(
                            "Box Blur",
                            it,
                            valueRange = 0F..255F,
                            decimalPattern = "##0",
                            //enabled = !appContext.isImageOperationRunning()

                        ) {
                            scope.launchOnIoThread {
                                appContext.imageBoxBlur(it.toLong())
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    appContext.currentImageContext()?.filterValues?.gaussianBlur?.toFloat()?.let {
                        SliderTextComponent(
                            "Gaussian Blur",
                            it,
                            valueRange = 0F..255F,
                            decimalPattern = "#0",
                            //enabled = !appContext.isImageOperationRunning()

                        ) {
                            scope.launchOnIoThread {
                                appContext.imageGaussianBlur(it.toLong())
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    appContext.currentImageContext()?.filterValues?.medianBlur?.toFloat()?.let {
                        SliderTextComponent(
                            "Median Blur",
                            it,
                            valueRange = 0F..255F,
                            decimalPattern = "#0",
                            //enabled = !appContext.isImageOperationRunning()

                        ) {
                            scope.launchOnIoThread {
                                appContext.imageMedianBlur(it.toLong())
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    appContext.currentImageContext()?.filterValues?.bilateralBlur?.toFloat()?.let {
                        SliderTextComponent(
                            "Bilateral Blur",
                            it,
                            valueRange = 0F..255F,
                            decimalPattern = "#0",
                            //enabled = !appContext.isImageOperationRunning()

                        ) {
                            scope.launchOnIoThread {
                                appContext.imageBilateralBlur(it.toLong())
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun HslFiltersComponent(appContext: AppContext) {
    Box(modifier = Modifier.padding(vertical = 10.dp)) {
        CollapsibleBox("HSL Filters", appContext.showStates.showHslFilters, {
            appContext.showStates.showHslFilters = !appContext.showStates.showHslFilters
        }) {
            val scope = rememberCoroutineScope()
            Column {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    appContext.currentImageContext()?.filterValues?.hue?.let {
                        SliderTextComponent(
                            "Hue",
                            it,
                            valueRange = -360F..360F,
                            decimalPattern = "##0",
                            //enabled = !appContext.isImageOperationRunning()

                        ) {
                            scope.launchOnIoThread {
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
                        SliderTextComponent(
                            "Saturation",
                            it,
                            valueRange = -2f..4f,
                            scrollValueChangeBy = 0.5f,
                            decimalPattern = "#0.##",
                            // enabled = !appContext.isImageOperationRunning()

                        ) {
                            scope.launchOnIoThread {
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
                        SliderTextComponent(
                            "Lightness",
                            it,
                            valueRange = 0f..2f,
                            scrollValueChangeBy = 0.5f,
                            decimalPattern = "#0.##",
                            //  enabled = !appContext.isImageOperationRunning()

                        ) {
                            scope.launchOnIoThread {
                                appContext.imageHslAdjust(
                                    lightness = it
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}