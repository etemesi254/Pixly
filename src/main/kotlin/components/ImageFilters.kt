package components

import AppContext
import ZilImageAndBitmapInterop
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp


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
                        "Contrast",
                        0F,
                        valueRange =0F..255F ,
                        decimalPattern = "#0"
                    ){
                        image.contrast(it)
                    }
                }


                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    SliderTextComponent(
                        "Gamma", 0F,
                        valueRange = -5F ..5F,
                        decimalPattern = "0.00"
                    ){
                        image.gamma(it+2.3F)
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    SliderTextComponent(
                        "Exposure", 0F,
                        valueRange = -5F ..5F,
                        decimalPattern = "0.00"
                    ){
                        image.exposure(it+1F)
                    }
                }
            }
        }
    }

}

@Composable
fun OrientationFiltersComponent(appContext: AppContext){

}