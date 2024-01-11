package org.cae.pixly

import AppContext
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import components.CollapsibleBox
import extensions.launchOnIoThread
import imageops.imageBilateralBlur
import imageops.imageBoxBlur
import imageops.imageGaussianBlur
import imageops.imageMedianBlur

@Composable
fun BlurFiltersComponent(appContext: AppContext) {

    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxWidth().height(200.dp).verticalScroll(rememberScrollState())) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
        ) {
            appContext.currentImageContext()?.filterValues?.boxBlur?.toFloat()?.let {
                AndroidSliderTextComponent(
                    "Mean Blur",
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
                AndroidSliderTextComponent(
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
                AndroidSliderTextComponent(
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
                AndroidSliderTextComponent(
                    "Bilateral Blur",
                    it,
                    valueRange = 0F..255F,
                    decimalPattern = "#0",
                    //enabled = !appContext.isImageOperationRunning()

                ) {
                    //appContext.showStates.showTopLinearIndicator=true
                    scope.launchOnIoThread {
                        appContext.imageBilateralBlur(it.toLong())
                    }
                }
            }
        }
    }
}