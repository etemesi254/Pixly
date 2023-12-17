package components

import AppContext
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import history.HistoryOperationsEnum


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LightFiltersComponent(appContext: AppContext) {
    val image = appContext.image

    Box(modifier = Modifier.padding(vertical = 10.dp)) {
        CollapsibleBox(title = "Light", appContext.showStates.showLightFilters, {
            appContext.showStates.showLightFilters = appContext.showStates.showLightFilters.xor(true)
        }) {
            Column() {

                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    SliderTextComponent(
                        "Brighten",
                        0F,
                        valueRange = 0F..255F,
                        decimalPattern = "##0"
                    ) {
                        appContext.appendToHistory(HistoryOperationsEnum.Brighten,it)

                        image.brighten(it)
                        appContext.broadcastImageChange()
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    SliderTextComponent(
                        "Contrast",
                        0F,
                        valueRange = 0F..255F,
                        decimalPattern = "#0"
                    ) {

                        appContext.appendToHistory(HistoryOperationsEnum.Contrast,it)
                        image.contrast(it)
                        appContext.broadcastImageChange()
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    SliderTextComponent(
                        "Gamma", 0F,
                        valueRange = -5F..5F,
                        decimalPattern = "0.00",
                        scrollValueChangeBy = 0.2F
                    ) {

                        appContext.appendToHistory(HistoryOperationsEnum.Gamma,it)
                        // gamma works in a weird way, higher gamma
                        // is a darker image, which beats the logic of the
                        // slider since we expect higher gamma to be a brighter
                        // image, so just invert that here
                        // this makes higher gamma -> brighter images
                        // smaller gamma -> darker images
                        image.gamma((-1*it) + 2.3F)
                        appContext.broadcastImageChange()
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    SliderTextComponent(
                        "Exposure", 0F,
                        valueRange = -1F..1F,
                        decimalPattern = "0.00",
                        scrollValueChangeBy = 0.01F
                    ) {
                        appContext.appendToHistory(HistoryOperationsEnum.Exposure,it);

                        image.exposure(it + 1F)
                        appContext.broadcastImageChange()
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
            Column {
                Box(modifier = Modifier.fillMaxWidth().padding(10.dp)) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        IconButton(onClick = {
                            appContext.appendToHistory(HistoryOperationsEnum.VerticalFlip);

                            appContext.image.verticalFlip()
                        }) {
                            Icon(
                                painter = painterResource("flip-vertical-svgrepo-com.svg"),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)

                            )
                        }

                        IconButton(onClick = {
                            appContext.appendToHistory(HistoryOperationsEnum.HorizontalFlip);

                            appContext.image.flop()
                        }) {
                            Icon(
                                painter = painterResource("flip-horizontal-svgrepo-com.svg"),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)

                            )
                        }
                        IconButton(onClick = {
                            // add to history
                            appContext.appendToHistory(HistoryOperationsEnum.Transposition);
                            appContext.image.transpose()
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

                    appContext.appendToHistory(HistoryOperationsEnum.Levels,it)
                    appContext.image.stretchContrast(it.start, it.endInclusive)
                    appContext.broadcastImageChange()
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
            Column {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    SliderTextComponent(
                        "Box Blur",
                        0F,
                        valueRange = 0F..255F,
                        decimalPattern = "##0"
                    ) {
                        appContext.initializeImageChange()
                        appContext.image.boxBlur(it.toLong())
                        appContext.broadcastImageChange()
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    SliderTextComponent(
                        "Gaussian Blur",
                        0F,
                        valueRange = 0F..255F,
                        decimalPattern = "#0"
                    ) {
                        appContext.initializeImageChange()
                        appContext.image.gaussianBlur(it.toLong())
                        appContext.broadcastImageChange()
                    }
                }
//                ColorPicker {
//                    println(it)
//                }
            }
        }
    }
}