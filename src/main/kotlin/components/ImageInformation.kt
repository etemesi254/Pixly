package components

import AppContext
import ZilImageAndBitmapInterop
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File
import java.text.DecimalFormat

@Composable
fun ImageInformationComponent(appCtx: AppContext){
    val file = appCtx.imFile
    val innerImage = appCtx.image.inner;

    Box(modifier = Modifier.padding(vertical = 10.dp)) {
        CollapsibleBox("Information", appCtx.showStates.showInformation, {
            appCtx.showStates.showInformation =
                appCtx.showStates.showInformation.xor(true);
        }) {
            Column(modifier = Modifier.padding(vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Width")
                    Text(innerImage.width().toString() + " px");
                }
                Divider()
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Height")
                    Text(innerImage.height().toString() + " px");
                }

                Divider()
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Colorspace")
                    Text(innerImage.colorspace().toString());
                }
                Divider()
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Size")
                    Text(formatSize(file.length()));
                }
            }
        }
    }


}


fun formatSize(bytes: Long): String {
    var bytes = bytes.toDouble();
    val formatter = DecimalFormat("####.##")
    if (bytes < 1024.0) {
        return formatter.format(bytes) + " B"
    }
    bytes /= 1024.0;
    if (bytes < 1024.0) {
        return formatter.format(bytes) + " KB"
    }
    bytes /= 1024.0;
    if (bytes < 1024.0) {
        return formatter.format(bytes) + " MB"
    }
    bytes /= 1024.0;
    return formatter.format(bytes) + " GB"
}