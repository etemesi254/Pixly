package org.cae.pixly

import AppContext
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import components.ImageInformationComponent
import modifiers.modifyIf
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import themes.AppTheme

val BOTTOM_SIZE = 200.dp;

@Composable
fun InformationPanel(appCtx: AppContext) {

    Box(modifier = Modifier.fillMaxHeight().padding(end = 15.dp)) {
        Divider(modifier = Modifier.fillMaxHeight().width(1.dp))
        // not using lazy column as the scrollbar is finicky with content
        Column(modifier = Modifier.padding(start = 3.dp, end = 10.dp)) {
            ImageInformationComponent(appCtx)
           // ExifMetadataPane(appCtx)
        }

    }
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Preview
@Composable
fun App() {
    val context by remember { mutableStateOf(AppContext()) }
    var isFirstDraw by remember { mutableStateOf(false) }
    val imBackgroundColor = if (context.imageIsLoaded()) Color.Transparent else Color(0x0F_00_00_00)


    if (isFirstDraw) {
        // take system theme based
        context.showStates.showLightTheme = !isSystemInDarkTheme();
        isFirstDraw = false;
    }

    // needed to launch file reader
    // doesn't compose anything to the screen, setting showFilePicker status modifier will launch a file picker
    showFilePicker(appContext = context)

    MaterialTheme(
        colors = if (context.showStates.showLightTheme) lightColors() else darkColors()
    ) {
        Scaffold(modifier = Modifier.fillMaxSize(), topBar = { TopBar(context) }, bottomBar = {
            BottomBar(context)
        }) {
            Column(modifier = Modifier.fillMaxSize().padding(it), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier.background(imBackgroundColor)
                    //    .fillMaxSize()
                        .modifyIf(Modifier.fillMaxWidth().fillMaxHeight(0.8F)) { context.imageIsLoaded() && context.openedRightPane != RightPaneOpened.None }
                        .clickable(enabled = !context.imageIsLoaded() && !context.showStates.showTopLinearIndicator) {
                            context.showStates.showPopups = !context.showStates.showPopups;
                            if (!context.imageIsLoaded()) {
                                context.showStates.showFilePicker = true;
                            }
                        }) {
                    if (!context.imageIsLoaded()) {

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {

                            if (context.showStates.showTopLinearIndicator) {

                                // An image is loading or something
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(30.dp))

                                Text("Loading")
                            } else {
                                Icon(
                                    painter = painterResource("xml/add_circle.xml"),
                                    contentDescription = null,
                                    modifier = Modifier.size(100.dp),

                                    )
                                Spacer(modifier = Modifier.height(30.dp))
                                Text("Click me to open an image")
                            }
                        }
                    } else {

                        Surface(
                            color = if (context.showStates.showLightTheme)
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
                                val ctx = context.currentImageContext()
                                if (ctx != null) {
                                    AndroidScalableImage(ctx)
                                }
                            }
                        }
                    }
                }
                if (context.imageIsLoaded() && context.openedRightPane != RightPaneOpened.None) {
                    Box(modifier = Modifier.fillMaxWidth().height(BOTTOM_SIZE)) {
                        when (context.openedRightPane) {
                            RightPaneOpened.None -> Box {}
                            RightPaneOpened.InformationPanel -> InformationPanel(context)
                            else -> Box {}
                        }
                    }
                }
            }
        }
    }
}