import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import components.*
import events.ExternalImageViewerEvent
import events.handleKeyEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import java.io.File
import java.text.DecimalFormat
import javax.swing.Scrollable
import kotlin.system.measureTimeMillis


/**
 * Load the image specified by appCtx.imFile
 *
 * NB: DO NOT RUN THIS ON THE MAIN THREAD AS IT WILL BLOCK
 * OTHER I/0, RUN IT ON IO THREAD
 * */
fun loadImage(appCtx: AppContext) {

    // check if we loaded this image, and move to that instead
    // of reloading
    if (appCtx.imageStates().containsKey(appCtx.imFile)) {
        appCtx.imageStates().asSequence().forEachIndexed { idx, it ->
            if (it.key == appCtx.imFile) {
                appCtx.tabIndex = idx
            }
        }

    } else {
        val time = measureTimeMillis {
            val image = ZilBitmap(appCtx.imFile.path)
            appCtx.initializeImageSpecificStates(image)
        }
        appCtx.bottomStatus = "Loaded ${appCtx.imFile.name} in $time ms"

        appCtx.setImageIsLoaded(true)
    }
    appCtx.broadcastImageChange()

}

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
@Preview
fun App(appCtx: AppContext) {


    val imBackgroundColor = if (appCtx.imageIsLoaded()) Color.Transparent else Color(0x0F_00_00_00)

    val topHorizontalSplitterState = rememberSplitPaneState(0F)
    // give the image maximum split plane support
    val nestedHorizontalSplitterState = rememberSplitPaneState(1F)


    if (appCtx.isFirstDraw) {
        // take system theme based
        appCtx.showStates.showLightTheme = !isSystemInDarkTheme();
        appCtx.isFirstDraw = false;
    }


    /*
     *  BUG, BUG, BUG
     *
     * For some reason, using local coroutine scope/LaunchedEffect causes the linear indicator
     * to not render, so it shows, but it doesn't show any pro
     * For now we are forced to use the global scope which miraculously works
     *
     * when that is fixed, revert this to local
     *
     */
    LaunchedEffect(Unit) {
        this.launch(Dispatchers.IO) {
            handleKeyEvents(appCtx)
        }

    }
    // TODO: See if we can get smoother for themes transitions
    //  https://stackoverflow.com/questions/70942573/android-jetpack-composecomposable-change-theme-color-smoothly
    MaterialTheme(
        typography = poppinsTypography, colors = if (appCtx.showStates.showLightTheme)
            lightColors() else darkColors()
    ) {


        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        }) { it ->
            Column(Modifier.padding(it).fillMaxSize()) {

                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
                        .padding(vertical = 5.dp, horizontal = 0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // top area with buttons

                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // open file
                        Box(modifier = Modifier.padding(horizontal = 5.dp)) {
                            val coroutineScope = rememberCoroutineScope()

                            PixlyToolTip(
                                "Open a file",
                                helpfulMessage = "This opens a file using the native file picker of your platform"
                            ) {
                                IconButton(onClick = {
                                    appCtx.showStates.showFilePicker = true;
                                }) {
                                    Icon(
                                        painter = painterResource("open-file-svgrepo-com (1).svg"),
                                        contentDescription = null,
                                        modifier = Modifier.size(35.dp)
                                    )
                                }
                            }

                            FilePicker(
                                show = appCtx.showStates.showFilePicker,
                                fileExtensions = SUPPORTED_EXTENSIONS
                            ) { file ->
                                appCtx.showStates.showFilePicker = false

                                if (file != null) {
                                    appCtx.imFile = File(file.path)
                                    appCtx.rootDirectory = appCtx.imFile.parent;

                                    appCtx.initializeImageChange()

                                    coroutineScope.launch(Dispatchers.IO) {
                                        loadImage(appCtx)
                                    }

                                    // do something with the file
                                }
                            }
                        }
                        PixlyToolTip(
                            title = "Open a directory",
                            helpfulMessage = "This opens a directory in your native directory picker\nOn choosing a directory, the files can be navigated via the in-app directory viewer"
                        ) {
                            // open directory
                            Box(modifier = Modifier.padding(horizontal = 5.dp)) {
                                IconButton(onClick = {
                                    appCtx.showStates.showDirectoryPicker = true;

                                }) {
                                    Icon(
                                        painter = painterResource("open-folder-svgrepo-com.svg"),
                                        contentDescription = null,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }


                                DirectoryPicker(show = appCtx.showStates.showDirectoryPicker) { dir ->
                                    appCtx.showStates.showDirectoryPicker = false
                                    appCtx.openedLeftPane = LeftPaneOpened.DirectoryViewer
                                    // do something with the directory
                                    if (dir != null) {
                                        appCtx.rootDirectory = dir
                                    }
                                }
                            }
                        }
                        //Spacer(modifier = Modifier.fillMaxWidth(0.9F))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(
                                horizontal = 0.dp
                            ), horizontalArrangement = Arrangement.End
                        ) {


                            // File picker doesn't support saving file dialogs
                            //
                            // See:  https://github.com/Wavesonics/compose-multiplatform-file-picker/issues/8


                            Box(modifier = Modifier.padding(horizontal = 5.dp)) {

                                IconButton(onClick = {
                                    if (appCtx.imageIsLoaded()) {
                                        appCtx.showStates.showSaveDialog = true
                                    }
                                }, enabled = appCtx.imageIsLoaded()) {
                                    Icon(
                                        painter = painterResource("save-svgrepo.svg"),
                                        contentDescription = null,
                                        modifier = Modifier.size(26.dp),
                                    )
                                }

                            }

                            if (appCtx.showStates.showSaveDialog && appCtx.imageIsLoaded()) {
                                SaveAsDialog(appCtx)
                            }
                            Divider(
                                //color = Color.Red,
                                modifier = Modifier
                                    .fillMaxHeight()  //fill the max height
                                    .width(1.dp)
                            )

                            PixlyToolTip(
                                title = "Toggle the App Theme",
                                helpfulMessage = "This changes the app theme from dark to light and vice versa"
                            ) {

                                IconButton({
                                    appCtx.showStates.showLightTheme = appCtx.showStates.showLightTheme.xor(true)
                                }) {
                                    Icon(
                                        painter = if (!appCtx.showStates.showLightTheme) painterResource("sun-svgrepo-com.svg") else painterResource(
                                            "moon-svgrepo-com.svg"
                                        ), contentDescription = null, modifier = Modifier.size(35.dp)
                                    )
                                }
                            }

                        }
                    }

                }

                Divider()

                // Movable panes
                HorizontalSplitPane(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.96F),
                    splitPaneState = topHorizontalSplitterState
                ) {

                    first(if (appCtx.openedLeftPane != LeftPaneOpened.None) 250.dp else 54.dp) {
                        LeftPane(appCtx)
                    }

                    second(0.dp) {
                        HorizontalSplitPane(
                            modifier = Modifier.fillMaxSize(),
                            splitPaneState = nestedHorizontalSplitterState
                        ) {
                            first(0.dp) {
                                var changeOnDelete by remember { mutableStateOf(false) }
                                Column(modifier = Modifier.fillMaxSize()) {

                                    if (appCtx.imageStates().isNotEmpty()) {
                                        ScrollableTabRow(
                                            selectedTabIndex = appCtx.tabIndex,
                                            modifier = Modifier.fillMaxWidth().modifyOnChange(changeOnDelete),
                                            edgePadding = 0.dp
                                        ) {
                                            // convert to a sequence to iterate
                                            // the benefits is that linkedHashMap preserves insertion order
                                            appCtx.imageStates().asSequence().forEachIndexed { idx, it ->
                                                Tab(it.key == appCtx.imFile,
                                                    onClick = {
                                                        appCtx.tabIndex = idx
                                                        // now update tab to be this idx
                                                        appCtx.imFile = it.key
                                                        appCtx.broadcastImageChange()
                                                    }, text = {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Text(it.key.name)

                                                            IconButton(onClick = {
                                                                appCtx.imageStates().remove(appCtx.imFile)

                                                                val value = appCtx.tabIndex - 1;
                                                                appCtx.tabIndex = value.coerceIn(
                                                                    minimumValue = 0,
                                                                    maximumValue = null
                                                                )
                                                                // set the new imFile
                                                                appCtx.imageStates().asSequence()
                                                                    .forEachIndexed { idx, it ->

                                                                        if (idx == appCtx.tabIndex) {
                                                                            appCtx.imFile = it.key
                                                                        }
                                                                    }

                                                                changeOnDelete = !changeOnDelete
                                                                appCtx.broadcastImageChange()

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

                                    Box(

                                        Modifier.background(imBackgroundColor).fillMaxSize()
                                            .padding(horizontal = 0.dp)
                                            .clickable(enabled = !appCtx.imageIsLoaded()) {
                                                appCtx.showStates.showPopups = !appCtx.showStates.showPopups;
                                                if (!appCtx.imageIsLoaded()) {
                                                    appCtx.showStates.showFilePicker = true;
                                                }
                                            }) {
                                        // We depend on boxes having kind of a stacked layout
                                        // so we can have multiple things that take max size and the layout still works
                                        // we exploit that here by having a column + row which both request .fillMaxSize
                                        // depending on order, the row is overlayed on top of the column,
                                        // but the column only contains text, so we don't need anything from it
                                        // which kinda works out
                                        if (!appCtx.imageIsLoaded()) {

                                            Column(
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                Icon(
                                                    painter = painterResource("add-circle.svg"),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(100.dp),

                                                    )
                                                Spacer(modifier = Modifier.height(30.dp))
                                                Text("Drag an image here\nUse the directory picker to start \nOr click me to open an image")
                                            }
                                        } else {

                                            Surface(
                                                color = if (appCtx.showStates.showLightTheme)
                                                    Color(
                                                        245,
                                                        245,
                                                        245
                                                    ) else Color(25, 25, 25)
                                            ) {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    // this language is weird
                                                    appCtx.getImage().image(appCtx)
                                                }
                                            }

                                            //Image(image, contentDescription = null, modifier = Modifier.fillMaxSize())
                                        }

                                        TopHoveringIcons(appCtx)
                                    }

                                }

                            }
                            second(if (appCtx.imageIsLoaded() && appCtx.openedRightPane != RightPaneOpened.None) 400.dp else 54.dp) {
                                RightPanel(appCtx)
                            }
                        }
                    }
                    splitter {

                        visiblePart {
                            Box(
                                Modifier
                                    .width(10.dp)
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colors.onPrimary)
                            )
                        }
                    }
                }


                // Bottom row with statuses
                Divider()
                Row(modifier = Modifier.fillMaxWidth().height(40.dp), verticalAlignment = Alignment.CenterVertically) {
                    val formatter = remember { DecimalFormat("0") };

                    Box(
                        contentAlignment = Alignment.CenterEnd,
                        modifier = Modifier.width(70.dp).padding(horizontal = 5.dp),
                    ) {
                        if (appCtx.imageIsLoaded()) {
                            Text(
                                "${formatter.format(appCtx.currentImageState().zoomState.zoom * 100F)} %",
                                style = TextStyle(fontSize = TextUnit(14F, TextUnitType.Sp)),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    }

                    Divider(
                        //color = Color.Red,
                        modifier = Modifier
                            .fillMaxHeight()  //fill the max height
                            .width(1.dp)
                    )

                    Text(
                        appCtx.bottomStatus,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        style = TextStyle(fontSize = TextUnit(14F, TextUnitType.Sp))
                    )

                    Box(
                        contentAlignment = Alignment.CenterEnd,
                        modifier = Modifier.fillMaxWidth().padding(end = 50.dp)
                    ) {
                        if (appCtx.showStates.showTopLinearIndicator) {
                            LinearProgressIndicator()
                        }
                    }
                }

            }
        }
    }
}


fun main() = application {
    val appContext by remember { mutableStateOf(AppContext()) }
    var time by mutableStateOf(System.currentTimeMillis())

    Window(onCloseRequest = ::exitApplication, title = APP_TITLE, undecorated = false, onKeyEvent = {
        val currTime = System.currentTimeMillis();
        val diff = currTime - time;
        time = currTime;
        /* BUG
        * It so happens that on keyEvent emits two keys when one is pressed
        *
        * which causes things to jump around, and breaks things like arrow key movement
        * IDK why, I think it's the propagation things, but we have this workaround
        * detect if two consecutive key presses have a short delay between them,
        * if they do, don't produce an event, otherwise produce one
        *
        * */
        if (diff > 250) {
            if (it.isCtrlPressed && it.key == Key.O) {
                // open
                appContext.externalNavigationEventBus.produceEvent(ExternalImageViewerEvent.OpenImage)
            }
            when (it.key) {
                Key.DirectionLeft -> appContext.externalNavigationEventBus.produceEvent(
                    ExternalImageViewerEvent.Previous
                )

                Key.DirectionRight -> appContext.externalNavigationEventBus.produceEvent(
                    ExternalImageViewerEvent.Next
                )

                Key.R -> appContext.externalNavigationEventBus.produceEvent(ExternalImageViewerEvent.ReloadImage)


            }
        }
        false
    }) {
        App(appContext)
    }
}
