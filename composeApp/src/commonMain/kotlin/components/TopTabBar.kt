package components

import AppContext
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import modifiers.modifyOnChange
import java.io.File

@Composable
fun TopTabBar(context: AppContext) {
    var toDeleteFile by remember { mutableStateOf(File("/")) }

    if (context.imageStates().isNotEmpty()) {
        ScrollableTabRow(
            selectedTabIndex = context.tabIndex,
            modifier = Modifier.fillMaxWidth().modifyOnChange(context.changeOnCloseTab),
            edgePadding = 0.dp
        ) {
            // convert to a sequence to iterate
            // the benefits is that linkedHashMap preserves insertion order
            context.imageStates().asSequence().forEachIndexed { idx, it ->
                Tab(it.key == context.imFile,
                    modifier = Modifier.padding(0.dp),
                    onClick = {
                        context.tabIndex = idx
                        // now update tab to be this idx
                        context.imFile = it.key
                        context.broadcastImageChange()
                    }, text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(it.key.name)

                            IconButton(onClick = {
                                if (context.imageStates()[it.key]!!.history.getHistory()
                                        .isNotEmpty()
                                ) {
                                    // a change occurred
                                    context.showStates.showWarningOnClose = true;
                                    toDeleteFile = it.key;

                                } else {
                                    context.removeFile(it.key)

                                }

                            }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    })
            }
        }

    }


    if (context.showStates.showWarningOnClose) {
        AlertDialog(
            modifier = Modifier.fillMaxWidth(0.7F),onDismissRequest = {
            context.showStates.showWarningOnClose = false;
        }, buttons = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Button(
                    onClick = {
                        context.showStates.showWarningOnClose = false
                    },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    Text("No")
                }
                Button(
                    onClick = {
                        context.removeFile(toDeleteFile);
                        context.showStates.showWarningOnClose = false

                    },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    Text("Yes")
                }
            }
        }, title = {
            Text("Confirm close of edited file")
        }, text = {
            Text("File ${toDeleteFile.name} has been modified, close it without saving?")

        })

    }
}