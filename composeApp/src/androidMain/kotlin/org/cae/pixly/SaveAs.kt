package org.cae.pixly

import AppContext
import ZilImageFormat
import android.graphics.Bitmap.CompressFormat
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import extensions.launchOnIoThread
import hasEncoder
import kotlinx.coroutines.sync.withLock
import java.io.File
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SaveAsDialog(ctx: AppContext) {
    var expanded by remember { mutableStateOf(false) }

    val nonAllowed = setOf(CompressFormat.WEBP_LOSSLESS,CompressFormat.WEBP_LOSSY)

    val items = CompressFormat.entries.filter { it-> !nonAllowed.contains(it) }

    var selectedIndex by remember { mutableStateOf(0) }
    var selectedFormat by remember { mutableStateOf(CompressFormat.JPEG) }
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp //it requires androidx.compose.material:material-icons-extended
    else
        Icons.Filled.ArrowDropDown

    var textFieldSize by remember { mutableStateOf(IntSize.Zero) }
    var filePath by remember { mutableStateOf(ctx.imFile.path) }
    var qualityValue by remember { mutableStateOf(100F) }


    Dialog(onDismissRequest = { ctx.showStates.showSaveDialog = false }) {
        Surface(modifier = Modifier.clip(RectangleShape).padding(30.dp), color = MaterialTheme.colors.background) {
            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.padding(vertical = 10.dp)) {
                    Text("Save Image", style = MaterialTheme.typography.h6)
                }

                Box {

                    OutlinedTextField(
                        value = selectedFormat.toString(),
                        onValueChange = { },
                        enabled = false,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            disabledTextColor = TextFieldDefaults.outlinedTextFieldColors().textColor(true).value
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                //This value is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size
                            }.clickable {
                                expanded = expanded.xor(true)
                            },
                        label = { Text("Image format") },
                        trailingIcon = {
                            Icon(icon, "contentDescription",
                                Modifier.clickable { expanded = !expanded })
                        }
                    )
                    DropdownMenu(
                        expanded = expanded, onDismissRequest = {
                            expanded = expanded.xor(true)
                        }, modifier = Modifier
                            .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                    ) {

                        items.forEachIndexed { index, s ->
                            run {
                                DropdownMenuItem(onClick = {
                                    selectedIndex = index
                                    selectedFormat = s
                                    expanded = false

                                }) {
                                    Text(s.toString())
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(15.dp))

                AndroidSliderTextComponent("Quality", qualityValue, 0F..100F) {
                    qualityValue = it.roundToInt().toFloat()
                }

                Box {
                    OutlinedTextField(
                        value = filePath,
                        textStyle = TextStyle(fontSize = TextUnit(13F, TextUnitType.Sp)),
                        onValueChange = {
                            filePath = it
                        },
                        modifier = Modifier.fillMaxWidth(), label = { Text("File path") },
                    )
                }
                Spacer(modifier = Modifier.padding(15.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    val scope = rememberCoroutineScope();
                    Button(onClick = {

                        ctx.showStates.showSaveDialog = false;
                        val c = ctx.currentImageContext()?.canvasBitmaps?.get(ImageContextBitmaps.CurrentCanvasImage)!!;
                        scope.launchOnIoThread {
                            ctx.initializeImageChange()
                            // acquire mutex to make a copy
                            // after this, we can manipulate with no fear
                            val img = c.mutex().withLock {
                                c.asImageBitmap().asAndroidBitmap()
                            }

                            val fd = File(filePath);
                            val outputStream = fd.outputStream();

                            val encodedBitmap = img.compress(selectedFormat, qualityValue.toInt(), outputStream);

                            ctx.bottomStatus = "Saved file ${fd.name} to ${fd.parent}"

                            ctx.broadcastImageChange()

                        }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Save")
                    }
                }
            }
        }
    }
}