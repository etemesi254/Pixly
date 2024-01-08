package org.cae.pixly

import AppContext
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import extensions.launchOnIoThread
import imageops.imageBrighten
import imageops.imageContrast
import imageops.imageExposure
import modifiers.backgroundColorIfCondition

enum class ImageFiltersComponentClicked {
    None,
    Light,
    HSL,
    Blur
}

@Composable
fun LightFilters(appContext: AppContext) {
    val scope = rememberCoroutineScope();
    Box(modifier = Modifier.height(200.dp).fillMaxWidth()
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(vertical = 10.dp)) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp, horizontal = 5.dp)
            ) {
                appContext.imageFilterValues()?.brightness?.let {
                    AndroidSliderTextComponent(
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
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp, horizontal = 5.dp)
            ) {
                appContext.imageFilterValues()?.let {
                    AndroidSliderTextComponent(
                        "Contrast",
                        it.contrast,
                        valueRange = -100F..100F,
                        decimalPattern = "#0",
                    ) {

                        scope.launchOnIoThread {
                            appContext.imageContrast(it)
                        }
                    }
                }
            }
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp, horizontal = 5.dp)
            ) {
                appContext.imageFilterValues()?.let {
                    AndroidSliderTextComponent(
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

@Composable
fun ImageFilters(appContext: AppContext) {
    var imageFiltersComponentClicked by remember { mutableStateOf(ImageFiltersComponentClicked.None) };

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (imageFiltersComponentClicked) {
            ImageFiltersComponentClicked.None -> Box {}
            ImageFiltersComponentClicked.Light -> LightFilters(appContext)
            ImageFiltersComponentClicked.HSL -> LightFilters(appContext)
            ImageFiltersComponentClicked.Blur -> LightFilters(appContext)
        }
        Divider(modifier = Modifier.fillMaxWidth())
        Row() {
            IconButton(onClick = {
                imageFiltersComponentClicked = ImageFiltersComponentClicked.Light
            },
                modifier = Modifier.backgroundColorIfCondition(MaterialTheme.colors.primary) {
                    imageFiltersComponentClicked == ImageFiltersComponentClicked.Light
                }) {
                Text("Light")
            }
            IconButton(onClick = {
                imageFiltersComponentClicked = ImageFiltersComponentClicked.HSL
            }, modifier = Modifier.backgroundColorIfCondition(MaterialTheme.colors.primary) {
                imageFiltersComponentClicked == ImageFiltersComponentClicked.HSL
            }) {
                Text("HSL")
            }
            IconButton(onClick = {
                imageFiltersComponentClicked = ImageFiltersComponentClicked.Blur
            }, modifier = Modifier.backgroundColorIfCondition(MaterialTheme.colors.primary) {
                imageFiltersComponentClicked == ImageFiltersComponentClicked.Blur
            }) {
                Text("Blur")
            }
        }
    }
}