import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import java.io.File
import kotlin.system.measureTimeMillis


suspend fun loadImage(appCtx: AppContext, imFile: File) {
    appCtx.showStates.showTopLinearIndicator = true

    val time = measureTimeMillis { appCtx.image.loadFile(imFile.path) }
    appCtx.bottomStatus = "Loaded ${imFile.name} in ${time} ms"

    appCtx.showStates.showTopLinearIndicator = false;
    appCtx.imageIsLoaded = true;
}

@OptIn(ExperimentalStdlibApi::class, ExperimentalSplitPaneApi::class, ExperimentalUnsignedTypes::class)
@Composable
@Preview
fun App(appCtx: AppContext) {


    var imFile by remember { mutableStateOf(File("")) }
    var imBackgroundColor = if (appCtx.imageIsLoaded) Color.Transparent else Color(0x0F_00_00_00)
    var rootDirectory by remember { mutableStateOf("/") }

    val topHorizontalSplitterState = rememberSplitPaneState()
    val nestedHorizontalSplitterState = rememberSplitPaneState()


    if (appCtx.isFirstDraw) {
        appCtx.showStates.showLightTheme = !isSystemInDarkTheme();
        appCtx.isFirstDraw = false;
    }

    LaunchedEffect(Unit) {

        appCtx.externalNavigationEventBus.events.collect() {
            if (it == ExternalImageViewerEvent.ReloadImage && appCtx.imageIsLoaded && imFile.exists() && imFile.isFile) {
                appCtx.showStates.showTopLinearIndicator = true

                this.launch {
                    loadImage(appCtx, imFile)
                }
            }
        }
    }
    MaterialTheme(
        typography = poppinsTypography, colors = if (appCtx.showStates.showLightTheme)
            lightColors() else darkColors()
    ) {

        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        }) { it ->
            Column(Modifier.padding(it).fillMaxSize()) {

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp, horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // top area with buttons
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                                        imFile = File(file.path)
                                        appCtx.showStates.showTopLinearIndicator = true
                                        rootDirectory = imFile.parent;

                                        coroutineScope.launch {
                                            loadImage(appCtx, imFile)
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
                                        rootDirectory = dir
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
                                    appCtx.showStates.showLightTheme = appCtx.showStates.showLightTheme.xor(true)
                                }, modifier = Modifier.onFocusEvent { state ->
                                    {
                                    }
                                }) {
                                    Icon(
                                        painter = if (!appCtx.showStates.showLightTheme) painterResource("sun-svgrepo-com.svg") else painterResource(
                                            "moon-svgrepo-com.svg"
                                        ), contentDescription = null, modifier = Modifier.size(40.dp)
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


                    first(250.dp) {
                        Row {
                            val scope = rememberCoroutineScope();

                            DirectoryViewer(rootDirectory) {
                                // on file clicked
                                val ext = it.extension.lowercase()
                                if (ext == "jpeg" || ext == "jpg" || ext == "png") {
                                    appCtx.showStates.showTopLinearIndicator = true
                                    imFile = it;

                                    scope.launch {
                                        loadImage(appCtx, imFile)
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

                    second(700.dp) {
                        HorizontalSplitPane(
                            modifier = Modifier.fillMaxSize(),
                            splitPaneState = nestedHorizontalSplitterState
                        ) {
                            first(500.dp) {
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

                                    TopHoveringIcons(appCtx.showStates.showPopups)

                                }
                            }
                            second(if (appCtx.imageIsLoaded) 300.dp else 0.dp) {
                                if (appCtx.imageIsLoaded) {
                                    Box() {
                                        Column(
                                            modifier = Modifier.padding(10.dp)
                                        ) {

                                            ImageInformationComponent(appCtx, imFile)
                                            LightFiltersComponent(appCtx)


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
                                    .background(MaterialTheme.colors.background)
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

    Window(onCloseRequest = ::exitApplication, title = APP_TITLE, undecorated = false, onKeyEvent = {
        when (it.key) {
            Key.DirectionLeft -> appContext.externalNavigationEventBus.produceEvent(
                ExternalImageViewerEvent.Previous
            )

            Key.DirectionRight -> appContext.externalNavigationEventBus.produceEvent(
                ExternalImageViewerEvent.Next
            )

            Key.R -> appContext.externalNavigationEventBus.produceEvent(ExternalImageViewerEvent.ReloadImage)

        }
        false
    }) {
        App(appContext)
    }
}
