package org.cae.pixly

import AppContext
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import components.CollapsibleBox
import extensions.launchOnIoThread
import imageops.imageHorizontalFlip
import imageops.imageRotate
import imageops.imageTranspose
import imageops.imageVerticalFlip

@Composable
fun AndroidOrientationFilters(appContext: AppContext) {
    val scope = rememberCoroutineScope()
    Column {
        Box(modifier = Modifier.fillMaxWidth().padding(10.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                IconButton(
                    onClick = {

                        scope.launchOnIoThread {
                            appContext.imageVerticalFlip()
                        }
                    },
                    //enabled = !appContext.isImageOperationRunning()
                ) {

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(R.drawable.flip_vertical_svgrepo_com),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text("Flip Vertically", textAlign = TextAlign.Center, fontSize = TextUnit(12F, TextUnitType.Sp))
                    }
                }

                IconButton(
                    onClick = {

                        scope.launchOnIoThread {
                            appContext.imageHorizontalFlip()
                        }

                    },
                    //enabled = !appContext.isImageOperationRunning()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(R.drawable.flip_horizontal_svgrepo_com),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )

                        Spacer(modifier = Modifier.height(5.dp))
                        Text("Flip Horizontally", textAlign = TextAlign.Center, fontSize = TextUnit(12F, TextUnitType.Sp))
                    }
                }
                IconButton(
                    onClick = {
                        // add to history
                        scope.launchOnIoThread {
                            appContext.imageTranspose()
                        }
                    },
                    //enabled = !appContext.isImageOperationRunning()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(R.drawable.transpose_svgrepo_com),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)

                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text("Transpose", textAlign = TextAlign.Center, fontSize = TextUnit(12F, TextUnitType.Sp))
                    }
                }
            }
        }
        Divider()
        Box(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                IconButton(
                    onClick = {

                        scope.launchOnIoThread {
                            appContext.imageRotate(90.0F)
                        }
                    },
                    //enabled = !appContext.isImageOperationRunning()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(R.drawable.rotate_90),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp).rotate(90.0F)
                        )
                        Text("Rotate 90°", textAlign = TextAlign.Center, fontSize = TextUnit(12F, TextUnitType.Sp))
                    }
                }
                IconButton(
                    onClick = {
                        scope.launchOnIoThread {
                            appContext.imageRotate(180.0F)
                        }
                    },
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(R.drawable.rotate_90),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp).rotate(180.0F)
                        )

                        Text("Rotate 180°", textAlign = TextAlign.Center, fontSize = TextUnit(12F, TextUnitType.Sp))

                    }
                }
                IconButton(
                    onClick = {
                        // add to history
                        scope.launchOnIoThread {
                            appContext.imageRotate(270.0F);
                        }
                    },
                    //enabled = !appContext.isImageOperationRunning()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(R.drawable.rotate_90),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp).rotate(270.0F)

                        )
                        Text("Rotate 270°", textAlign = TextAlign.Center, fontSize = TextUnit(12F, TextUnitType.Sp))

                    }
                }
            }
        }

    }
}