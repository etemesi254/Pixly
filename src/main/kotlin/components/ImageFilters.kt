package components

import AppContext
import ZilImageAndBitmapInterop
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp


@Composable
fun LightFiltersComponent(appContext: AppContext) {
    val image = appContext.image

    Box(modifier = Modifier.padding(10.dp)) {
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
                        decimalPattern = "#00"
                    ){
                        image.contrast(it)
                    }
                }


                Box(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                ) {
                    SliderTextComponent(
                        "Gamma", 2.3F,
                        valueRange = 0F ..5F,
                        decimalPattern = "0.00"
                    ){
                        image.gamma(it)
                    }
                }
            }
        }
    }

}

@Composable
fun imageFiltersComponent() {

}