package components

import AppContext
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import java.text.DecimalFormat

@Composable
fun ImageInformationComponent(appCtx: AppContext) {
    val file = appCtx.imFile
    val innerImage = appCtx.currentImageContext()?.imageToDisplay()?.innerInterface();

    if (innerImage != null) {
        Column(modifier = Modifier.padding(vertical = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Width", style = TextStyle(fontSize = TextUnit(14F, TextUnitType.Sp)))
                Text(innerImage.width().toString() + " px", style = TextStyle(fontSize = TextUnit(14F, TextUnitType.Sp)));
            }
            Divider()
            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Height", style = TextStyle(fontSize = TextUnit(14F, TextUnitType.Sp))
                )
                Text(
                    innerImage.height().toString() + " px", style = TextStyle(fontSize = TextUnit(14F, TextUnitType.Sp))
                );
            }

            Divider()
            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Size in Disk",
                    style = TextStyle(fontSize = TextUnit(14F, TextUnitType.Sp))
                )
                Text(
                    formatSize(file.length()),
                    style = TextStyle(fontSize = TextUnit(14F, TextUnitType.Sp))
                );
            }
            Divider()

            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Size in Memory",
                    style = TextStyle(fontSize = TextUnit(14F, TextUnitType.Sp))
                )
                Text(
                    formatSize(
                        (innerImage.height()
                                * innerImage.width()
                                * innerImage.colorspace().components().toUInt()
                                * innerImage.depth().sizeOf().toUInt()
                                ).toLong()
                    ),
                    style = TextStyle(fontSize = TextUnit(14F, TextUnitType.Sp))
                );
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