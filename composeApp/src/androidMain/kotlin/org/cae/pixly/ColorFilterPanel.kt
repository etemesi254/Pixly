package org.cae.pixly

import AppContext
import FilterMatrixComponent
import ZilBitmapInterface
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import calcResize
import colorMatricesPane
import extensions.launchOnIoThread


@Composable
fun SingleFilterPanel(image: ZilBitmapInterface, component: FilterMatrixComponent) {

    val bitmap = remember { AndroidProtectedBitmap() }
    var isDone by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {

        this.launchOnIoThread {
            if (!isDone) {
                val image = image.clone()
                image.colorMatrix(component.colorMatrix, bitmap)
                isDone = true
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth().height(200.dp).padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (isDone) {
            Image(bitmap.asImageBitmap(), null, modifier = Modifier.fillMaxWidth().height(150.dp))
        } else {
            CircularProgressIndicator()
        }

        Text(component.name)
    }
}


@Composable
fun ColorFilterPanel(appCtx: AppContext) {
    key(appCtx.imFile) { // to recompose it on file change
        val scrollState = rememberScrollState()

        val filters = colorMatricesPane()

        val ctx = appCtx.currentImageContext();
        if (ctx != null) {
            val image = remember(ctx.images.size) {
                val c = ctx.imageToDisplay().clone()
                val ratios = calcResize(c, 200, 200)
                c.innerInterface().resize(ratios[0], ratios[1])
                c
            };

            Box(modifier = Modifier.fillMaxHeight().padding(end = 10.dp)) {
                Divider(modifier = Modifier.fillMaxHeight().width(1.dp))

                // Lazy columns would be better but, they have some annoying rebuilding
                // when scrolling up
                Row(modifier = Modifier.fillMaxHeight().horizontalScroll(scrollState).padding(horizontal = 15.dp)) {
                    filters.forEach {
                        SingleFilterPanel(image, it)
                    }
                }
            }
        }
    }
}