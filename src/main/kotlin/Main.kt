import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import components.*
import events.ExternalImageViewerEvent
import events.ExternalNavigationEventBus
import events.handleKeyEvents
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import org.jetbrains.skiko.currentNanoTime
import java.io.File
import kotlin.system.measureTimeMillis


suspend fun loadImage(appCtx: AppContext) {


    appCtx.showStates.showTopLinearIndicator = true
    val time = measureTimeMillis { appCtx.image.loadFile(appCtx.imFile.path) }
    appCtx.bottomStatus = "Loaded ${appCtx.imFile.name} in ${time} ms"
    appCtx.showStates.showTopLinearIndicator = false;
    appCtx.imageIsLoaded = true;
}

@OptIn(ExperimentalStdlibApi::class, ExperimentalSplitPaneApi::class, ExperimentalUnsignedTypes::class)
@Composable
@Preview
fun App(appCtx: AppContext) {


    var imBackgroundColor = if (appCtx.imageIsLoaded) Color.Transparent else Color(0x0F_00_00_00)

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
        handleKeyEvents(appCtx)
    }

    MaterialTheme(
        typography = poppinsTypography, colors = if (appCtx.showStates.showLightTheme)
            lightColors() else darkColors()
    ) {

        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        }) { it ->
            Column(Modifier.padding(it).fillMaxSize()) {

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp, horizontal = 0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // top area with buttons
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                            IconButton(onClick = {

                                appCtx.showStates.showDirectoryViewer = appCtx.showStates.showDirectoryViewer.xor(true);
                            }) {
                                Icon(
                                    painter = painterResource("open-panel-filled-left-svgrepo-com.png"),
                                    contentDescription = null,
                                    modifier = Modifier.size(50.dp).padding(end =10.dp),
                                )
                            }
                            // open file
                            Box() {
                                val coroutineScope = rememberCoroutineScope()

                                Button(onClick = {
                                    appCtx.showStates.showFilePicker = true;
                                }) {
                                    Text("Open File")
                                }

                                FilePicker(
                                    show = appCtx.showStates.showFilePicker,
                                    fileExtensions = SUPPORTED_EXTENSIONS
                                ) { file ->
                                    appCtx.showStates.showFilePicker = false

                                    if (file != null) {
                                        appCtx.imFile = File(file.path)
                                        appCtx.rootDirectory = appCtx.imFile.parent;

                                        appCtx.showStates.showTopLinearIndicator = true;

                                        coroutineScope.launch {
                                            loadImage(appCtx)
                                        }

                                        // do something with the file
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                            // open directory
                            Box() {
                                Button(onClick = {
                                    appCtx.showStates.showDirectoryPicker = true;

                                }) {
                                    Text("Open Directory")
                                }

                                DirectoryPicker(show = appCtx.showStates.showDirectoryPicker) { dir ->
                                    appCtx.showStates.showDirectoryPicker = false
                                    // do something with the directory
                                    if (dir != null) {
                                        appCtx.rootDirectory = dir
                                    }
                                }
                            }
                            //Spacer(modifier = Modifier.fillMaxWidth(0.9F))
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(
                                    horizontal = 10.dp
                                ), horizontalArrangement = Arrangement.End
                            ) {


                                IconButton({
                                    if (appCtx.imageIsLoaded) {
                                        appCtx.showStates.showImageEditors =
                                            appCtx.showStates.showImageEditors.xor(true)
                                    }
                                }) {
                                    Icon(
                                        painter = painterResource("image-edit-svgrepo-com.svg"),
                                        contentDescription = null,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }

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
                        if (appCtx.showStates.showTopLinearIndicator) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }

                Divider()

                // Movable panes
                HorizontalSplitPane(
                    modifier = Modifier.fillMaxWidth(1F).fillMaxHeight(0.96F),
                    splitPaneState = topHorizontalSplitterState
                ) {

                    first(
                        if (appCtx.showStates.showDirectoryViewer) {
                            250.dp
                        } else {
                            0.dp
                        }
                    ) {

                        val density = LocalDensity.current;

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
                                        /*
                                        * For some weird reason, using a local scope doesn't cause LinearProgressIndicator
                                        * to render, it appears on screen, the animation just doesn't run, which is weird
                                        * So for now we use GlobalScope until we figure out
                                        *
                                        */
                                        scope.launch {
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

                    second(0.dp) {
                        HorizontalSplitPane(
                            modifier = Modifier.fillMaxSize(),
                            splitPaneState = nestedHorizontalSplitterState
                        ) {
                            first(0.dp) {
                                Box(
                                    Modifier.background(imBackgroundColor).fillMaxSize().padding(horizontal = 10.dp)
                                        .clickable {
                                            appCtx.showStates.showPopups = appCtx.showStates.showPopups.xor(true);
                                            if (!appCtx.imageIsLoaded) {
                                                appCtx.showStates.showFilePicker = true;
                                            }
                                        }) {
                                    // We depend on boxes having kind of a stacked layout
                                    // so we can have multiple things that take max size and the layout still works
                                    // we exploit that here by having a column + row which both request .fillMaxSize
                                    // depending on order, the row is overlayed on top of the column,
                                    // but the column only contains text, so we don't need anything from it
                                    // which kinda works out
                                    if (!appCtx.imageIsLoaded) {

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

                                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            appCtx.image.image()
                                        }

                                        //Image(image, contentDescription = null, modifier = Modifier.fillMaxSize())
                                    }

                                    TopHoveringIcons(appCtx)

                                }
                            }
                            second(if (appCtx.imageIsLoaded && appCtx.showStates.showImageEditors) 300.dp else 0.dp) {
                                val density = LocalDensity.current;

                                AnimatedVisibility(visible = appCtx.imageIsLoaded && appCtx.showStates.showImageEditors,
                                    enter = slideInHorizontally { with(density) { +40.dp.roundToPx() } },
                                    exit = slideOutHorizontally { with(density) { +400.dp.roundToPx() } }) {
                                    Box() {
                                        Column(
                                            modifier = Modifier.padding(10.dp)
                                        ) {

                                            ImageInformationComponent(appCtx)
                                            LightFiltersComponent(appCtx)
                                            OrientationFiltersComponent(appCtx)


                                            //HistogramChart(buffer, Color(0x1F_88_88_88_88))

                                        }
                                    }
                                }
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
//                        handle {
//                            Box(
//                                Modifier
//                                   .markAsHandle()
////                                    .pointerHoverIcon(PointerIcon.Default)
////                                    .background(SolidColor(Color.Gray), alpha = 0.50f)
//                                    .width(9.dp)
//                                    .fillMaxHeight()
//                            )
//                        }
                    }
                }


                // Bottom row with statuses
                Divider()
                Row(modifier = Modifier.fillMaxWidth().height(40.dp), verticalAlignment = Alignment.CenterVertically) {

                    Text(appCtx.bottomStatus, style = TextStyle(fontSize = TextUnit(14F, TextUnitType.Sp)))
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
