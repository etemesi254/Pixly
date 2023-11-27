package components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import java.io.File
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp


@Composable
fun SingleDirectoryView(path: File, onDirectoryClicked: (File) -> Unit) {

    val folderBitmap = painterResource("folder-svgrepo.svg")
    val fileBitmap = painterResource("file-svgrepo.svg")


    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth().clickable {
            onDirectoryClicked(path)
        }
    ) {
        Image(
            painter = if (path.isDirectory) folderBitmap else fileBitmap,
            contentDescription = "Info",
            modifier = Modifier.size(45.dp, 45.dp)
        )
        Text(
            path.name,
            modifier = Modifier.padding(horizontal = 10.dp),
            fontSize = TextUnit(14F, TextUnitType.Sp)
        )

    }

}

@Composable
fun DirectoryViewer(root: String) {

    var rootFile by remember { mutableStateOf(root) }

    var showHidden by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf("") }
    val searchBitmap = painterResource("search-svgrepo.svg")

    val file = File(rootFile);

    // function to filter files
    var filterFiles = { file: File ->
        var result = file.exists()
        if (!showHidden && file.isHidden) {
            result = false
        }
        if (textFieldValue.isNotEmpty()) {
            result = file.path.contains(textFieldValue)
        }
        result
    }

    if (file.exists() && file.isDirectory()) {
        var files = file.walk().maxDepth(1).filter(filterFiles).toList();

        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {


                Text("Show hidden files")

                Checkbox(showHidden, onCheckedChange = {
                    showHidden = showHidden.xor(true)
                })

            }
            Divider()
            Row(
                modifier = Modifier.fillMaxWidth().height(70.dp)
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    textFieldValue,
                    onValueChange = {
                        textFieldValue = it;
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().border(
                        1.dp,
                        Color.Gray,
                        shape = RoundedCornerShape(20)
                    ).padding(5.dp),

                    )
            }

            Divider()
            LazyColumn(userScrollEnabled = true) {
                items(files.count()) {
                    SingleDirectoryView(files[it]) {
                        rootFile = it.absolutePath
                    }
                }
            }
        }

    } else {
        Text("File");
    }


}

