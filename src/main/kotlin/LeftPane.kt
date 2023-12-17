import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import components.DirectoryViewer
import components.PixlyToolTip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modifiers.backgroundColorIfCondition

@Composable
fun DirectoryViewerEntry(appCtx: AppContext) {

    val density = LocalDensity.current

    AnimatedVisibility(
        visible = appCtx.showStates.showDirectoryViewer,
        enter = slideInHorizontally { with(density) { -40.dp.roundToPx() } },
        exit = slideOutHorizontally { with(density) { -400.dp.roundToPx() } }) {
        Row {
            val scope = rememberCoroutineScope();

            DirectoryViewer(appCtx) {
                // on file clicked
                if (isImage(it)) {
                    appCtx.imFile = it;
                    appCtx.initializeImageChange()

                    scope.launch(Dispatchers.IO) {
                        loadImage(appCtx)
                    }


                }
            }
            Divider(

                modifier = Modifier
                    .fillMaxHeight()  //fill the max height
                    .width(1.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LeftPane(appCtx: AppContext) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier.fillMaxSize().defaultMinSize(80.dp)
    ) {
        if (appCtx.openedLeftPane != LeftPaneOpened.None) {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxWidth().padding(start = 54.dp)
            ) {
                when (appCtx.openedLeftPane) {
                    LeftPaneOpened.None -> Box {}
                    LeftPaneOpened.DirectoryViewer -> DirectoryViewerEntry(appCtx)
                }
            }
        }
        Row(modifier = Modifier.fillMaxHeight()) {

            Column(modifier = Modifier.fillMaxHeight()) {
                PixlyToolTip(title = "Show the directory navigator",
                    helpfulMessage = "This is a simple navigator for viewing other files in the current directory containing a loaded image ") {
                    IconButton(
                        {
                            if (appCtx.openedLeftPane == LeftPaneOpened.DirectoryViewer) {
                                appCtx.openedLeftPane = LeftPaneOpened.None
                            } else {
                                appCtx.openedLeftPane = LeftPaneOpened.DirectoryViewer
                            }

                        },
                        // enabled = appCtx.imageIsLoaded,
                        modifier = Modifier.backgroundColorIfCondition(MaterialTheme.colors.primary) {
                            appCtx.openedLeftPane == LeftPaneOpened.DirectoryViewer
                        }) {
                        Icon(
                            painter = painterResource("folder-svgrepo-com.svg"),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
            Divider(modifier = Modifier.fillMaxHeight().width(1.dp))

        }
    }
}