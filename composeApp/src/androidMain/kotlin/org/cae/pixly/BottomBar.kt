package org.cae.pixly

import AppContext
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import modifiers.backgroundColorIfCondition
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalResourceApi::class)
@Composable
fun BottomBar(appCtx: AppContext) {
    var isEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(appCtx.imageIsLoaded()) {
        isEnabled = appCtx.imageIsLoaded()
    }
    Column(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalAlignment = Alignment.CenterHorizontally) {
        Divider(modifier = Modifier.fillMaxWidth().height(1.dp))

        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.Center) {
            IconButton(
                {
                    if (appCtx.openedRightPane == RightPaneOpened.InformationPanel) {
                        appCtx.openedRightPane = RightPaneOpened.None
                    } else {
                        appCtx.openedRightPane = RightPaneOpened.InformationPanel
                    }

                },
                enabled = isEnabled,
                modifier = Modifier.backgroundColorIfCondition(MaterialTheme.colors.primary) {
                    appCtx.openedRightPane == RightPaneOpened.InformationPanel
                }) {
                Icon(
                    painter = painterResource("xml/info_svgrepo.xml"),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
            IconButton(
                {
                    if (appCtx.openedRightPane == RightPaneOpened.FineTunePanel) {
                        appCtx.openedRightPane = RightPaneOpened.None
                    } else {
                        appCtx.openedRightPane = RightPaneOpened.FineTunePanel
                    }

                },
                enabled = isEnabled ,

                modifier = Modifier.backgroundColorIfCondition(MaterialTheme.colors.primary) {
                    appCtx.openedRightPane == RightPaneOpened.FineTunePanel
                }
            ) {
                Icon(
                    painter = painterResource("xml/filters_svgrepo_com.xml"),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
            IconButton(
                {
                    if (appCtx.openedRightPane == RightPaneOpened.ImageFilters) {
                        appCtx.openedRightPane = RightPaneOpened.None
                    } else {
                        appCtx.openedRightPane = RightPaneOpened.ImageFilters
                    }

                },
                enabled = isEnabled ,
                modifier = Modifier.backgroundColorIfCondition(MaterialTheme.colors.primary) {
                    appCtx.openedRightPane == RightPaneOpened.ImageFilters
                }
            ) {
                Icon(
                    painter = painterResource("xml/colorfilter_svgrepo_com.xml"),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}