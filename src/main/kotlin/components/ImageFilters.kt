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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LightFiltersComponent(appContext: AppContext) {
    val image = appContext.image

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
                        0F,
                        valueRange = 0F..255F,
                        decimalPattern = "##0"
                    ) {

                        image.brighten(appContext, scope, it)
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

                        image.contrast(appContext, scope, it)
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
                        "Exposure", 0F,
                        valueRange = -1F..1F,
                        decimalPattern = "0.00",
                        scrollValueChangeBy = 0.01F
                    ) {
                        appContext.appendToHistory(HistoryOperationsEnum.Exposure, it);

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

                            appContext.image.verticalFlip(appContext, scope)
                        }) {
                            Icon(
                                painter = painterResource("flip-vertical-svgrepo-com.svg"),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)

                            )
                        }

                        IconButton(onClick = {

                            appContext.image.flop(appContext, scope)
                        }) {
                            Icon(
                                painter = painterResource("flip-horizontal-svgrepo-com.svg"),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)

                            )
                        }
                        IconButton(onClick = {
                            // add to history
                            appContext.image.transpose(appContext, scope)
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

                    appContext.image.stretchContrast(appContext, scope, it)
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
                        0F,
                        valueRange = 0F..255F,
                        decimalPattern = "##0"
                    ) {
                        appContext.image.boxBlur(appContext, scope, it.toLong())
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
                        appContext.image.gaussianBlur(appContext, scope, it.toLong())
                    }
                }
//                ColorPicker {
//                    println(it)
//                }
            }
        }
    }
}