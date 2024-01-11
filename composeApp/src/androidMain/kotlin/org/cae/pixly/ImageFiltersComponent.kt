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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
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
    Blur,
    Orientation,
}

@Composable
fun LightFilters(appContext: AppContext) {
    val scope = rememberCoroutineScope();
    Box(
        modifier = Modifier.height(200.dp).fillMaxWidth()
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
            ImageFiltersComponentClicked.Orientation -> AndroidOrientationFilters(appContext)
        }
        Divider(modifier = Modifier.fillMaxWidth())
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {
                imageFiltersComponentClicked = ImageFiltersComponentClicked.Light
            },
                modifier = Modifier.backgroundColorIfCondition(MaterialTheme.colors.primary) {
                    imageFiltersComponentClicked == ImageFiltersComponentClicked.Light
                }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painterResource(R.drawable.light_bulb_svgrepo_com),
                        contentDescription = "Light Icon",
                        modifier = Modifier.size(25.dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text("Light", textAlign = TextAlign.Center, fontSize = TextUnit(12F, TextUnitType.Sp))
                }
            }
            IconButton(onClick = {
                imageFiltersComponentClicked = ImageFiltersComponentClicked.Orientation
            }, modifier = Modifier.backgroundColorIfCondition(MaterialTheme.colors.primary) {
                imageFiltersComponentClicked == ImageFiltersComponentClicked.Orientation
            }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painterResource(R.drawable.orientation_svgrepo_com),
                        contentDescription = "Orientation Icon",
                        modifier = Modifier.size(25.dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        "Orientation",
                        textAlign = TextAlign.Center,
                        fontSize = TextUnit(12F, TextUnitType.Sp)
                    )
                }

            }

            IconButton(onClick = {
                imageFiltersComponentClicked = ImageFiltersComponentClicked.Blur
            }, modifier = Modifier.backgroundColorIfCondition(MaterialTheme.colors.primary) {
                imageFiltersComponentClicked == ImageFiltersComponentClicked.Blur
            }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painterResource(R.drawable.blur_svgrepo_com),
                        contentDescription = "Blur Icon",
                        modifier = Modifier.size(25.dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    Text("Blur", fontSize = TextUnit(12F, TextUnitType.Sp))
                }
            }
        }
    }
}