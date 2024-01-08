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
import androidx.compose.ui.zIndex
import components.TopTabBar
import modifiers.modifyOnChange
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource


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
                        .fillMaxSize()
                        .modifyOnChange(context.changeOnCloseTab)
                        .clickable(enabled = !context.imageIsLoaded() && !context.showStates.showTopLinearIndicator) {
                            context.showStates.showPopups = !context.showStates.showPopups;
                            if (!context.imageIsLoaded()) {
                                context.showStates.showFilePicker = true;
                            }
                        }) {
                    if (context.showStates.showTopLinearIndicator){
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
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
                                Column(modifier = Modifier.fillMaxSize()) {

                                    if (context.imageStates().isNotEmpty()) {
                                        TopTabBar(context)
                                    }

                                    Surface(
                                        modifier = Modifier.zIndex(-1F),
                                        color = if (context.showStates.showLightTheme)
                                            Color(
                                                245,
                                                245,
                                                245
                                            ) else Color(25, 25, 25)
                                    ) {
                                        AndroidScalableImage(context);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}