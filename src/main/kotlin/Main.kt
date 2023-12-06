import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import java.io.File
import java.text.DecimalFormat
import kotlin.system.measureTimeMillis


@OptIn(ExperimentalStdlibApi::class, ExperimentalSplitPaneApi::class, ExperimentalUnsignedTypes::class)
@Composable
@Preview
fun App() {

    var showModifiers by remember { mutableStateOf(ShowModifiers()) }
    var appStates by remember { mutableStateOf(AppStates()) }

    var statusMessages by remember { mutableStateOf("Hello, World!") }

    var contrastValue by remember { mutableStateOf(100F) }
    var hideableClicked by remember { mutableStateOf(true) }


    var imageIsLoaded by remember { mutableStateOf(false) }

    var imFile by remember { mutableStateOf(File("")) }
    var imBackgroundColor = if (imageIsLoaded) Color.Transparent else Color(0x0F_00_00_00)
    var rootDirectory by remember { mutableStateOf("/") }

    val topHorizontalSplitterState = rememberSplitPaneState()
    val nestedHorizontalSplitterState = rememberSplitPaneState()

    var zilImage = remember { ZilImageAndBitmapInterop() };
    var isFirstDraw by remember { mutableStateOf(true) }

    if (isFirstDraw) {
        appStates.showLightTheme = !isSystemInDarkTheme();
        isFirstDraw = false;
    }

    MaterialTheme(
        typography = poppinsTypography, colors = if (appStates.showLightTheme)
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
                                Button(onClick = {
                                    showModifiers.showFilePicker = true;
                                }) {
                                    Text("Open File")
                                }

                                FilePicker(
                                    show = showModifiers.showFilePicker,
                                    fileExtensions = SUPPORTED_EXTENSIONS
                                ) { file ->
                                    showModifiers.showFilePicker = false

                                    if (file != null) {
                                        imFile = File(file.path)
                                        showModifiers.showTopLinearIndicator = true
                                        rootDirectory = imFile.parent;

                                        GlobalScope.launch {
                                            var time = measureTimeMillis { zilImage.loadFile(file.path) }
                                            statusMessages = "Loaded ${imFile.name} in ${time} ms"

                                            showModifiers.showTopLinearIndicator = false;
                                            imageIsLoaded = true;
                                        }
                                        // do something with the file
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                            // open directory
                            Box() {
                                Button(onClick = {
                                    showModifiers.showDirectoryPicker = true;
                                }) {
                                    Text("Open Directory")
                                }

                                DirectoryPicker(show = showModifiers.showDirectoryPicker) { dir ->
                                    showModifiers.showDirectoryPicker = false
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
                                    appStates.showLightTheme = appStates.showLightTheme.xor(true)
                                }, modifier = Modifier.onFocusEvent { state ->
                                    {
                                    }
                                }) {
                                    Icon(
                                        painter = if (!appStates.showLightTheme) painterResource("sun-svgrepo-com.svg") else painterResource(
                                            "moon-svgrepo-com.svg"
                                        ), contentDescription = null, modifier = Modifier.size(40.dp)
                                    )
                                }

                            }
                        }
                        if (showModifiers.showTopLinearIndicator) {
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
                            DirectoryViewer(rootDirectory) {
                                // on file clicked
                                if (it.extension == "jpeg" || it.extension == "jpg" || it.extension == "png") {
                                    showModifiers.showTopLinearIndicator = true
                                    imFile = it;

                                    GlobalScope.launch {
                                        val time = measureTimeMillis { zilImage.loadFile(it.absolutePath) }
                                        statusMessages = "Loaded ${imFile.name} in $time ms"

                                        showModifiers.showTopLinearIndicator = false;
                                        imageIsLoaded = true;
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
                                            showModifiers.showPopups = showModifiers.showPopups.xor(true);
                                            if (!imageIsLoaded) {
                                                showModifiers.showFilePicker = true;
                                            }
                                        }) {
                                    // We depend on boxes having kind of a stacked layout
                                    // so we can have multiple things that take max size and the layout still works
                                    // we exploit that here by having a column + row which both request .fillMaxSize
                                    // depending on order, the row is overlayed on top of the column,
                                    // but the column only contains text, so we don't need anything from it
                                    // which kinda works out
                                    if (!imageIsLoaded) {

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
                                            zilImage.image()
                                        }

                                        //Image(image, contentDescription = null, modifier = Modifier.fillMaxSize())
                                    }

                                    TopHoveringIcons(showModifiers.showPopups)

                                }
                            }
                            second(if (imageIsLoaded) 300.dp else 0.dp) {
                                if (imageIsLoaded) {
                                    Box() {
                                        Column(
                                            modifier = Modifier.padding(10.dp)
                                        ) {

                                            if (imageIsLoaded) {
                                                val imCopy = zilImage.inner;
                                                Box(modifier = Modifier.padding(10.dp)) {
                                                    CollapsibleBox("Information", showModifiers.showInformation, {
                                                        showModifiers.showInformation =
                                                            showModifiers.showInformation.xor(true);
                                                    }) {
                                                        Column(modifier = Modifier.padding(10.dp)) {
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                                                horizontalArrangement = Arrangement.SpaceBetween
                                                            ) {
                                                                Text("Width")
                                                                Text(imCopy.width().toString() + " px");
                                                            }
                                                            Divider()
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                                                horizontalArrangement = Arrangement.SpaceBetween
                                                            ) {
                                                                Text("Height")
                                                                Text(imCopy.height().toString() + " px");
                                                            }

                                                            Divider()
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                                                horizontalArrangement = Arrangement.SpaceBetween
                                                            ) {
                                                                Text("Colorspace")
                                                                Text(imCopy.colorspace().toString());
                                                            }
                                                            Divider()
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                                                horizontalArrangement = Arrangement.SpaceBetween
                                                            ) {
                                                                Text("Size")
                                                                Text(formatSize(imFile.length()));
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            Box(modifier = Modifier.padding(10.dp)) {
                                                CollapsibleBox(title = "Light", hideableClicked, {
                                                    hideableClicked = hideableClicked.xor(true)
                                                }) {
                                                    Column() {
                                                        Box(
                                                            modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                                                        ) {
                                                            SliderTextComponent(
                                                                "Contrast",
                                                                contrastValue / 100F,
                                                                { value ->
                                                                    if (imageIsLoaded) {
                                                                        zilImage.contrast(value)
                                                                    }
                                                                    contrastValue = value
                                                                },
                                                                offset = 0.0F,
                                                                scale = 100F,
                                                                decimalPattern = "#00"
                                                            )
                                                        }


                                                        Box(
                                                            modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)
                                                        ) {
                                                            SliderTextComponent(
                                                                "Gamma", 0.5F,
                                                                { value ->

                                                                    if (imageIsLoaded) {
                                                                        zilImage.gamma(value)
                                                                    }
                                                                }, offset = 0.5F
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            Box(modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)) {
                                                SliderTextComponent("Exposure", 0.5F, { value ->
                                                    //contrastValue = value
                                                }, offset = 0.5F, scale = 10F, decimalPattern = "#0.00")
                                            }
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

                    Text(statusMessages, style = TextStyle(fontSize = TextUnit(14F, TextUnitType.Sp)))
                }

            }
        }
    }
}


fun formatSize(bytes: Long): String {
    var bytes = bytes.toDouble();
    val formatter = DecimalFormat("####.##")
    if (bytes < 1024.0) {
        return formatter.format(bytes) + " B"
    }
    bytes /= 1024.0;
    if (bytes < 1024.0) {
        return formatter.format(bytes) + " KB"
    }
    bytes /= 1024.0;
    if (bytes < 1024.0) {
        return formatter.format(bytes) + " MB"
    }
    bytes /= 1024.0;
    return formatter.format(bytes) + " GB"
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = APP_TITLE, undecorated = false) {
        App()
    }
}
