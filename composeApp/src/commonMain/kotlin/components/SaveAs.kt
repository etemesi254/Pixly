package components

import AppContext
import ZilImageFormat
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SaveAsDialog(ctx: AppContext) {
    var expanded by remember { mutableStateOf(false) }

    // drop ImageFormat.UnknownFormat
    val items = ZilImageFormat.values().drop(1)
    var selectedIndex by remember { mutableStateOf(0) }
    var selectedText by remember { mutableStateOf(ZilImageFormat.JPEG) }
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp //it requires androidx.compose.material:material-icons-extended
    else
        Icons.Filled.ArrowDropDown

    var textFieldSize by remember { mutableStateOf(IntSize.Zero) }


    Dialog(onDismissRequest = { ctx.showStates.showSaveDialog = false }) {
        Surface(modifier = Modifier.clip(RectangleShape).padding(30.dp), color = MaterialTheme.colors.background) {
            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.padding(vertical = 10.dp)) {
                    Text("Save Image", style = MaterialTheme.typography.h6)
                }

                Box {

                    OutlinedTextField(
                        value = selectedText.toString(),
                        onValueChange = {  },
                        enabled = false,
                        colors = TextFieldDefaults.outlinedTextFieldColors(disabledTextColor = TextFieldDefaults.outlinedTextFieldColors().textColor(true).value),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                //This value is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size
                            }.onClick {
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
                                    selectedText = s
                                    expanded =false

                                }) {
                                    Text(s.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}