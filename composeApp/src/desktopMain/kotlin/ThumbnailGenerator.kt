import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import extensions.launchOnIoThread
import java.io.File



@Composable
fun SingleThumbnail(appContext: AppContext, file: File) {

    val bitmap = remember { DesktopProtectedBitmap() }

    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {

        this.launchOnIoThread {
            if (!isLoaded) {
                // load image
                val sharedBuffer = SharedBuffer();
                val im = ZilBitmap(file.path, sharedBuffer, ZilJvmImage(file = file.path))
                val sizes = calcResize(im, 200, 200)
                im.inner.resize(sizes[0], sizes[1])
                im.writeToCanvas(bitmap)
                isLoaded = true

            }
        }
    }
    val scope = rememberCoroutineScope();
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp, horizontal = 5.dp).clickable {
        scope.launchOnIoThread {
            appContext.imFile = file;
            appContext.initializeImageChange()
            loadImage(appContext, false)
        }
    }, horizontalAlignment = Alignment.CenterHorizontally) {

        if (isLoaded) {
            Image(
                bitmap.asImageBitmap(),
                null,
                modifier = Modifier.fillMaxWidth().height(200.dp).padding(10.dp),
                contentScale = ContentScale.FillHeight
            )
        } else {
            Box(modifier = Modifier.align(alignment = Alignment.CenterHorizontally).size(200.dp)){
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

            }
        }

        Text(file.name, fontSize = TextUnit(12.0F, TextUnitType.Sp))
    }

}

@Composable
fun ThumbnailGenerator(appContext: AppContext) {

    key(appContext.paths.size) {

        if (appContext.showStates.showThumbnail) {
            val scrollState = rememberScrollState();
            val paths = remember { appContext.paths.toList() }

            Box(modifier = Modifier.fillMaxSize()) {
                Divider(modifier = Modifier.fillMaxWidth().height(1.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 10.dp)
                        .horizontalScroll(scrollState)
                ) {

                    paths.forEach {
                        SingleThumbnail(appContext, file = it)
                    }
                }

                HorizontalScrollbar(
                    rememberScrollbarAdapter(scrollState),
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart)
                )

            }
        }
    }
}