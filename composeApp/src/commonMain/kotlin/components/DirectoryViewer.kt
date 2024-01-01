package components

import AppContext
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import java.io.File
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import extensions.launchOnIoThread
import fillPaths
import isImage
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.nio.file.Paths


@Composable
fun SingleDirectoryView(path: File, ctx: AppContext, onDirectoryClicked: (File) -> Unit) {

    val folderBitmap = painterResource("folder-svgrepo.svg")
    val fileBitmap = painterResource("file-svgrepo.svg")

    if (ctx.imFile == path) {
        // show which image is in focus
        Row(
            modifier = Modifier.fillMaxWidth().border(
                BorderStroke(
                    1.dp,
                    Color.Gray
                ), shape = RoundedCornerShape(5)

            ).padding(vertical = 5.dp, horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
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
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 5.dp, horizontal = 5.dp).fillMaxWidth().clickable {
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
}

@Composable
fun DirectoryViewer(appCtx: AppContext, onFileClicked: (file: File) -> Unit) {

    var showHidden by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf("") }

    val toggleHiddenFilesPainter =
        if (showHidden) painterResource("eye-off-svgrepo-com.svg") else painterResource("eye-show-svgrepo-com.svg")

    val redoPainter = painterResource("reload-svgrepo-com.svg")

    var file by mutableStateOf(File(appCtx.rootDirectory));

    // function to filter files
    val filterFiles = { cFile: File ->

        var result = cFile.exists()
        if (!showHidden && cFile.isHidden) {
            result = false
        } else if (cFile == file) {
            result = false
        } else if (textFieldValue.isNotEmpty()) {
            result = cFile.path.lowercase().contains(textFieldValue.lowercase())
        }
        result
    }

    if (file.exists() && file.isDirectory) {
        val files = file.walk().maxDepth(1).filter(filterFiles).toList();

        LaunchedEffect(appCtx.rootDirectory) {
            this.launchOnIoThread {

                fillPaths(appCtx, files)
            }
        }

        Column(modifier = Modifier.padding(horizontal = 2.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = {
                    // force a redraw, rootFile is viewable by compose
                    // so just set that up to be the changed so that it seems
                    // we did something
                    file = file
                    appCtx.rootDirectory = file.toString()
                }, modifier = Modifier.size(40.dp).padding(horizontal = 10.dp)) {
                    Icon(painter = redoPainter, contentDescription = null)
                }
                IconButton(onClick = {
                    showHidden = showHidden.xor(true)
                }, modifier = Modifier.size(45.dp).padding(horizontal = 10.dp)) {
                    Icon(painter = toggleHiddenFilesPainter, contentDescription = null)
                }

                IconButton(onClick = {
                    if (file.parentFile != null) {
                        file = file.parentFile
                        appCtx.rootDirectory = file.toString();
                    }
                }) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, modifier = Modifier.size(25.dp))
                }

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
                    textStyle = MaterialTheme.typography.caption.copy(MaterialTheme.colors.onBackground),

                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().border(
                        1.dp,
                        Color.Gray,
                        shape = RoundedCornerShape(20)
                    ).padding(5.dp),

                    )
            }


            Divider()
            Box() {
                val scrollState = rememberLazyListState()
                val adapter = rememberScrollbarAdapter(scrollState)

                LazyColumn(userScrollEnabled = true) {
                    items(files.count()) { items ->

                        SingleDirectoryView(files[items], appCtx) {
                            if (it.isDirectory) {
                                appCtx.rootDirectory = it.absolutePath
                                file = it.absoluteFile
                            }
                            if (it.isFile) {
                                onFileClicked(it)
                            }
                        }

                    }
                }
                Box(
                    modifier = Modifier.matchParentSize()
                ) {
                    VerticalScrollbar(
                        adapter = adapter,
                        modifier = Modifier.align(Alignment.CenterEnd),
                    )
                }
            }
        }

    } else {

        onFileClicked(file)
        // don't hide that view
        //rootFile = file.parentFile.toString();

    }


}

