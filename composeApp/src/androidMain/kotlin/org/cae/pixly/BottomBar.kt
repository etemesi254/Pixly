package org.cae.pixly

import AppContext
import RightPaneOpened
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import components.HistogramChart
import components.ImageInformationComponent
import modifiers.backgroundColorIfCondition
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import java.text.DecimalFormat

@Composable
fun InformationPanel(appCtx: AppContext) {

    val scrollState = rememberScrollState()

    Surface(modifier = Modifier.fillMaxSize()) {
        // not using lazy column as the scrollbar is finicky with content
        Column(modifier = Modifier.fillMaxSize().padding(start = 3.dp, end = 10.dp).verticalScroll(scrollState)) {
            ImageInformationComponent(appCtx)
            // ExifMetadataPane(appCtx)
        }
    }
}

@OptIn(ExperimentalResourceApi::class, ExperimentalMaterialApi::class)
@Composable
fun BottomBar(appCtx: AppContext) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (appCtx.openedRightPane != RightPaneOpened.None) {
            Box(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                contentAlignment = Alignment.BottomCenter
            ) {
                when (appCtx.openedRightPane) {
                    RightPaneOpened.None -> Box {}
                    RightPaneOpened.InformationPanel -> InformationPanel(appCtx)
                    RightPaneOpened.ImageFilters -> ColorFilterPanel(appCtx)
                    RightPaneOpened.FineTunePanel -> ImageFilters(appCtx)
                    RightPaneOpened.HistogramPanel -> Box(modifier = Modifier.fillMaxWidth().height(250.dp).padding(10.dp)){HistogramChart(appCtx, showIndicators = true)}
                    else -> Box {}
                }
            }
        }
        Divider(modifier = Modifier.fillMaxWidth().height(1.dp))

        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.Center) {

            IconButton(
                {
                    if (appCtx.openedRightPane == RightPaneOpened.HistogramPanel) {
                        appCtx.openedRightPane = RightPaneOpened.None
                    } else {
                        appCtx.openedRightPane = RightPaneOpened.HistogramPanel
                    }

                },
                enabled = appCtx.imageIsLoaded(),
                modifier = Modifier.backgroundColorIfCondition(MaterialTheme.colors.primary) {
                    appCtx.openedRightPane == RightPaneOpened.HistogramPanel
                }) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(R.drawable.histogram_linear_svgrepo_com),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
            }
            IconButton(
                {
                    if (appCtx.openedRightPane == RightPaneOpened.InformationPanel) {
                        appCtx.openedRightPane = RightPaneOpened.None
                    } else {
                        appCtx.openedRightPane = RightPaneOpened.InformationPanel
                    }

                },
                enabled = appCtx.imageIsLoaded(),
                modifier = Modifier.backgroundColorIfCondition(MaterialTheme.colors.primary) {
                    appCtx.openedRightPane == RightPaneOpened.InformationPanel
                }) {
                Icon(
                    painter = painterResource("xml/info_svgrepo.xml"),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
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
                enabled = appCtx.imageIsLoaded(),

                modifier = Modifier.backgroundColorIfCondition(MaterialTheme.colors.primary) {
                    appCtx.openedRightPane == RightPaneOpened.FineTunePanel
                }
            ) {
                Icon(
                    painter = painterResource("xml/filters_svgrepo_com.xml"),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
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
                enabled = appCtx.imageIsLoaded(),
                modifier = Modifier.backgroundColorIfCondition(MaterialTheme.colors.primary) {
                    appCtx.openedRightPane == RightPaneOpened.ImageFilters
                }
            ) {
                Icon(
                    painter = painterResource("xml/colorfilter_svgrepo_com.xml"),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }
}