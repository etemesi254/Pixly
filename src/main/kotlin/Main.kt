import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.RetryStrategy
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import components.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import java.io.File
import kotlin.random.Random
import kotlin.random.nextUInt


class OpenAiLayout {


    var promptString = "";
    var promptResponse = "";

    @Composable
    fun build() {
        val openAiCtx =
            remember { OpenAI("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", retry = RetryStrategy(1)) }


        var mutablePromptString by remember { mutableStateOf("") }


        Column(modifier = Modifier.fillMaxSize(0.95F).padding(20.dp)) {

            Row {
                BasicTextField(
                    value = mutablePromptString,
                    onValueChange = {
                        mutablePromptString = it;
                        promptString = it;

                    },
                    singleLine = true,
                    modifier = Modifier.border(
                        1.dp,
                        Color.Gray,
                        shape = RoundedCornerShape(20)
                    ).padding(5.dp),

                    )
                OutlinedButton(
                    onClick = {
                        val chatCompletionRequest = ChatCompletionRequest(
                            model = ModelId("gpt-3.5-turbo-16k"),
                            messages = listOf(

                                ChatMessage(
                                    role = ChatRole.User,
                                    content = mutablePromptString
                                )
                            )
                        )
                        runBlocking {
                            println("Hello");
                            println(openAiCtx.chatCompletion(chatCompletionRequest).choices[0].message.content)
                        }
                    }
                ) {
                    Text("Go")
                }

            }
        }
    }
}

@OptIn(ExperimentalStdlibApi::class, ExperimentalSplitPaneApi::class, ExperimentalUnsignedTypes::class)
@Composable
@Preview
fun App() {

    var showModifiers by remember { mutableStateOf(ShowModifiers()) }
    var appStates by remember { mutableStateOf(AppStates()) }

    var statusMessages by remember { mutableStateOf("Hello, World!") }

    var contrastValue by remember { mutableStateOf(100F) }
    var hideableClicked by remember { mutableStateOf(true) }
    var ran = Random.Default

    val amplititudes = (1..256).map { ran.nextUInt() }


    var imageIsLoaded by remember { mutableStateOf(false) }

    var imFile by remember { mutableStateOf(File("")) }
    var image by remember { mutableStateOf(ImageBitmap(10, 10)) }
    var imBackgroundColor = if (imageIsLoaded) Color.Transparent else Color(0x0F_00_00_00)

    val topHorizontalSplitterState = rememberSplitPaneState()
    val nestedHorizontalSplitterState = rememberSplitPaneState()


    MaterialTheme(typography = poppinsTypography) {

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
                                    imFile = file?.path?.let { it1 -> File(it1) }!!
                                    showModifiers.showTopLinearIndicator = true

                                    GlobalScope.launch {

                                        image = loadImageBitmap(imFile.inputStream())
                                        showModifiers.showTopLinearIndicator = false;
                                        imageIsLoaded = true;
                                    }
                                    // do something with the file
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
                            DirectoryViewer("C:\\")
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


                                            Image(
                                                painter = painterResource("add-circle.svg"),
                                                contentDescription = null,
                                                modifier = Modifier.size(100.dp)
                                            )
                                            Spacer(modifier = Modifier.height(30.dp))
                                            Text("Drag an image here\nUse the directory picker to start \nOr click me to open an image")
                                        }
                                    } else {
                                        Image(image, contentDescription = null, modifier = Modifier.fillMaxSize())
                                    }

                                    TopHoveringIcons(showModifiers.showPopups)

                                }
                            }
                            second(300.dp) {
                                Box() {
                                    Column(
                                        modifier = Modifier.padding(10.dp)
                                    ) {

                                        CollapsibleBox(title = "Light", hideableClicked, {
                                            hideableClicked = hideableClicked.xor(true)
                                        }) {
                                            Column() {
                                                Box(modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)) {
                                                    SliderTextComponent("Contrast", contrastValue / 100F, { value ->
                                                        contrastValue = value
                                                    }, offset = 0.0F, scale = 100F, decimalPattern = "#00")
                                                }


                                                Box(modifier = Modifier.fillMaxWidth().padding(10.dp).scale(1F)) {
                                                    SliderTextComponent(
                                                        "Brightness", 0.5F,
                                                        { value ->
                                                            // contrastValue = value
                                                        }, offset = 0.5F
                                                    )
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
                Row(modifier = Modifier.fillMaxWidth().height(40.dp)) {

                    Text(statusMessages)
                }

            }
        }
    }
}


@OptIn(ExperimentalStdlibApi::class)
@Composable
fun AppChatOpenAi() {
    val oai = OpenAiLayout();

    MaterialTheme {


        Scaffold(topBar = {

        }) {
            Column(modifier = Modifier.padding(it)) {
                oai.build()
            }
        }

    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = APP_TITLE, undecorated = false) {
        App()
    }
}
