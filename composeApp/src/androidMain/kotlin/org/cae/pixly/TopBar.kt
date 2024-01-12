package org.cae.pixly

import AppContext
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import extensions.launchOnIoThread
import history.undoSingleHistory
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun TopBar(context: AppContext) {
    var showLinearIndicator by remember { mutableStateOf(false) }
    rememberCoroutineScope().launch {
        context.showStates.showTopLinearIndicator.collect {
            showLinearIndicator = it
        }
    }
    Column(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            IconButton(onClick = {
                context.showStates.showFilePicker = true;
            }) {
                Icon(
                    painter = painterResource("xml/open_file_svgrepo_com.xml"),
                    contentDescription = "Open a file from gallery",
                    modifier = Modifier.size(22.dp)
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Box(modifier = Modifier.padding(horizontal = 5.dp)) {
                    val scope = rememberCoroutineScope()
                    IconButton(
                        onClick = {
                            scope.launchOnIoThread {
                                context.undoSingleHistory()
                            }
                        }, enabled = if (context.getHistory() == null) {
                            false
                        } else {
                            context.getHistory()!!.getHistory().isNotEmpty()
                        }
                    ) {
                        Icon(
                            painter = painterResource("xml/undo_svgrepo.xml"),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
                Box(modifier = Modifier.padding(horizontal = 5.dp)) {

                    IconButton(onClick = {
                        if (context.imageIsLoaded()) {
                            context.showStates.showSaveDialog = true
                        }
                    }, enabled = context.imageIsLoaded()) {
                        Icon(
                            painter = painterResource("xml/save_svgrepo.xml"),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                        )
                        if (context.showStates.showSaveDialog) {
                            SaveAsDialog(context)
                        }
                    }
                }
                IconButton(onClick = {
                    context.showStates.showLightTheme = !context.showStates.showLightTheme
                }) {
                    Icon(
                        painter = if (context.showStates.showLightTheme) {
                            painterResource("xml/moon_svgrepo_com.xml")
                        } else {
                            painterResource("xml/sun_svgrepo_com.xml")
                        },
                        contentDescription = "Change the app theme",
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }

        Box(modifier = Modifier.height(2.dp)) {
            if (showLinearIndicator) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.Transparent
                )
            }
        }
        Box(modifier = Modifier.requiredHeight(1.dp)) {
            Divider(modifier = Modifier.fillMaxWidth().height(1.dp))
        }


    }
}