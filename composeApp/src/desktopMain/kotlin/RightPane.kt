import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import components.*
import desktopComponents.*
import extensions.launchOnIoThread
import history.HistoryWidget
import modifiers.backgroundColorIfCondition
import modifiers.modifyOnChange
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable


@Composable
fun histogramPane(appCtx: AppContext) {
    CollapsibleBox("Histogram", appCtx.showStates.showHistogram, onTopRowClicked = {
        appCtx.showStates.showHistogram = appCtx.showStates.showHistogram.not()
    }) {
        Box() {
            HistogramChart(appCtx)
        }
    }
}


@Composable
fun RightPanel(appCtx: AppContext) {

    var isEnabled by remember {  mutableStateOf(false)}

    LaunchedEffect(appCtx.imageIsLoaded()) {
        isEnabled = appCtx.imageIsLoaded()
    }
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier.fillMaxSize().defaultMinSize(80.dp)
    ) {
        if (appCtx.openedRightPane != RightPaneOpened.None && appCtx.imageIsLoaded()) {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxWidth().padding(end = 40.dp)
            ) {
                when (appCtx.openedRightPane) {
                    RightPaneOpened.None -> Box {}
                    RightPaneOpened.InformationPanel -> InformationPanel(appCtx)
                    RightPaneOpened.FineTunePanel -> ImageFineTunePane(appCtx)
                    RightPaneOpened.HistoryPanel -> HistoryWidget(appCtx)
                    RightPaneOpened.ImageFilters -> FiltersPanel(appCtx)
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxHeight().requiredWidth(50.dp)
            //.border(2.dp, MaterialTheme.colors.onBackground, shape = RectangleShape)
        ) {

            Divider(modifier = Modifier.fillMaxHeight().width(1.dp))

            Column(modifier = Modifier.fillMaxHeight()) {
                PixlyToolTip(
                    title = "Show Image Information",
                    helpfulMessage = "This will show both general image information, (width, height) and Exif information if present "
                ) {
                    IconButton(
                        {
                            if (appCtx.openedRightPane == RightPaneOpened.InformationPanel) {
                                appCtx.openedRightPane = RightPaneOpened.None
                            } else {
                                appCtx.openedRightPane = RightPaneOpened.InformationPanel
                            }

                        },
                       enabled = isEnabled ,
                        modifier = Modifier.backgroundColorIfCondition(MaterialTheme.colors.primary) {
                            appCtx.openedRightPane == RightPaneOpened.InformationPanel
                        }) {
                        Icon(
                            painter = painterResource("info-svgrepo.svg"),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                PixlyToolTip(title = "Show the edits panel") {
                    IconButton(
                        {
                            if (appCtx.openedRightPane == RightPaneOpened.FineTunePanel) {
                                appCtx.openedRightPane = RightPaneOpened.None
                            } else {
                                appCtx.openedRightPane = RightPaneOpened.FineTunePanel
                            }

                        },
//                        enabled = appCtx.imageIsLoaded(),
                        enabled = isEnabled ,

                        modifier = Modifier.backgroundColorIfCondition(MaterialTheme.colors.primary) {
                            appCtx.openedRightPane == RightPaneOpened.FineTunePanel
                        }
                    ) {
                        Icon(
                            painter = painterResource("filters-2-svgrepo-com.svg"),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                PixlyToolTip(title = "Show filters panel") {
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
                            painter = painterResource("colorfilter-svgrepo-com.svg"),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Bottom) {

                    IconButton(
                        {
                            if (appCtx.openedRightPane == RightPaneOpened.HistoryPanel) {
                                appCtx.openedRightPane = RightPaneOpened.None
                            } else {
                                appCtx.openedRightPane = RightPaneOpened.HistoryPanel
                            }
                        },
                        enabled = isEnabled ,

                        modifier = Modifier.backgroundColorIfCondition(MaterialTheme.colors.primary) {
                            appCtx.openedRightPane == RightPaneOpened.HistoryPanel
                        }
                    ) {
                        Icon(
                            painter = painterResource("history-svgrepo-com.svg"),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ExifMetadataPane(appCtx: AppContext) {

    val exif: Map<String, String>? = appCtx.currentImageContext()?.imageToDisplay()?.innerInterface()?.exifMetadata()

    if (exif != null) {

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

            if (exif.isNotEmpty()) {
                Divider()
                Text(
                    "Exif Metadata",
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                    style = MaterialTheme.typography.h5
                )
                //Divider()
            }
            for ((k, v) in exif) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(all = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        k,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(end = 10.dp),
                        style = TextStyle(fontSize = TextUnit(14F, TextUnitType.Sp))
                    )
                    Text(
                        v,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = TextStyle(fontSize = TextUnit(14F, TextUnitType.Sp))
                    )
                }
                Divider()
            }
        }
    }
}

@Composable
fun InformationPanel(appCtx: AppContext) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxHeight().padding(end = 15.dp)) {
        Divider(modifier = Modifier.fillMaxHeight().width(1.dp))
        // not using lazy column as the scrollbar is finicky with content
        Column(modifier = Modifier.verticalScroll(scrollState).padding(start = 3.dp, end = 10.dp)) {
            histogramPane(appCtx)
            ImageInformationComponent(appCtx)
            ExifMetadataPane(appCtx)
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = scrollState
            )
        )
    }
}

@Composable
fun ImageFineTunePane(appCtx: AppContext) {
    val density = LocalDensity.current;

    // We want to have items to reorder in the panels/pane. but then we can't have compose return
    // a list of widgets. So we use this
    //
    // The trick is that we store a separate ordering list and then on drawing, loop on that list
    var enumOrdering = remember { FiltersPaneOrdering.values().toMutableList() };

    // it may happen that the enumOrdering changed but compose doesn't rerender
    // the components, this is because it seems to compose that
    // the box didn't change
    //
    // So we use this to force compose to recompose via our cool
    // modifyOnChange extension
    var orderChanged by remember { mutableStateOf(false) };

    val scrollState = rememberLazyListState()

    val state = rememberReorderableLazyListState(onMove = { from, to ->
        enumOrdering = enumOrdering.apply {

            this.add(to.index, removeAt(from.index))
            // tell box to recompose
            orderChanged = orderChanged.not()

        }
    })

    Box() {
        Divider(modifier = Modifier.fillMaxHeight().width(1.dp))

        AnimatedVisibility(
            visible = appCtx.openedRightPane == RightPaneOpened.FineTunePanel,
            enter = slideInHorizontally { with(density) { +40.dp.roundToPx() } },
            exit = slideOutHorizontally { with(density) { +400.dp.roundToPx() } },
            modifier = Modifier.fillMaxHeight().padding(start = 3.dp)
        ) {

            Surface(
                modifier = Modifier.modifyOnChange(orderChanged)
                    .fillMaxHeight().padding(horizontal = 5.dp)
            ) {
                // provides a scrollbar

                LazyColumn(
                    modifier = Modifier.padding(10.dp)
                        .reorderable(state)
                        .detectReorderAfterLongPress(state)
                        .fillMaxHeight(),
                    state = state.listState,

                    ) {

                    items(enumOrdering.size) {
                        when (val item = enumOrdering[it]) {

                            FiltersPaneOrdering.LightFilters -> {
                                ReorderableItem(state, key = item, index = it) {
                                    LightFiltersComponent(appCtx)
                                }
                            }

                            FiltersPaneOrdering.OrientationFilters -> {
                                ReorderableItem(
                                    state,
                                    key = item,
                                    index = it
                                ) { OrientationFiltersComponent(appCtx) }
                            }


                            FiltersPaneOrdering.HSLFilters -> {
                                ReorderableItem(
                                    state,
                                    key = item,
                                    index = it
                                ) {
                                    HslFiltersComponent(appCtx)
                                }

                            }

                            FiltersPaneOrdering.Levels -> {
                                ReorderableItem(state, key = item, index = it) {
                                    LevelsFiltersComponent(appCtx)
                                }
                            }

                            FiltersPaneOrdering.BlurFilters -> {
                                ReorderableItem(state, key = item, index = it) {
                                    BlurFiltersComponent(appCtx)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


data class FilterMatrixComponent(val name: String, val colorMatrix: FloatArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FilterMatrixComponent

        if (name != other.name) return false
        if (!colorMatrix.contentEquals(other.colorMatrix)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + colorMatrix.contentHashCode()
        return result
    }
};


fun colorMatricesPane(): List<FilterMatrixComponent> {
    // convert to grayscale
    return listOf(
        FilterMatrixComponent(
            "Grayscale", listOf(
                0.2F, 0.3F, 0.5F, 0.0F, 0.0F,
                0.2F, 0.3F, 0.5F, 0.0F, 0.0F,
                0.2F, 0.3F, 0.5F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F, 1.0F, 0.0F
            ).toFloatArray()
        ),
        //https://learn.microsoft.com/en-us/archive/msdn-magazine/2005/january/net-matters-sepia-tone-stringlogicalcomparer-and-more
        FilterMatrixComponent(
            "Sepia", listOf(
                0.393F, 0.769F, 0.189F, 0.0F, 0.0F,
                0.349F, 0.686F, 0.686F, 0.0F, 0.0F,
                0.272F, 0.534F, 0.534F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F, 1.0F, 0.0F
            ).toFloatArray()
        ),

        FilterMatrixComponent(
            "Vivid", listOf(
                +1.2F, -0.1F, -0.1F, 0.0F, 0.0F,
                -0.1F, +1.2F, -0.1F, 0.0F, 0.0F,
                -0.1F, -0.1F, +1.2F, 0.0F, 0.0F,
                +0.0F, +0.0F, +0.0F, 1.0F, 0.0F
            ).toFloatArray()
        ),
        FilterMatrixComponent(
            "Polaroid", listOf(
                +1.438F, -0.122F, -0.016F, 0.0F, -0.03F,
                -0.062F, +1.378F, -0.016F, 0.0F, +0.00F,
                -0.062F, -0.122F, +1.483F, 0.0F, -0.02F,
                +0.0F, +0.0F, +0.0F, 1.0F, 0.0F
            ).toFloatArray()
        ),
        FilterMatrixComponent(
            "Lilac", listOf(
                1.2F, 0.00F, 0.00F, 0.0F, 0.00F,
                0.0F, 0.8F, 0.00F, 0.0F, 0.00F,
                0.0F, 0.00F, 1.2F, 0.0F, 0.00F,
                0.0F, +0.0F, +0.0F, 1.0F, 0.0F
            ).toFloatArray()
        ),

        )
}

@Composable
fun SingleFilterPanel(image: ZilBitmapInterface, component: FilterMatrixComponent) {

    val bitmap = remember { ProtectedBitmap() }
    var isDone by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {

        this.launchOnIoThread {
            if (!isDone) {
                val image = image.clone()
                image.colorMatrix(component.colorMatrix, bitmap)
                isDone = true
            }
        }
    }
    Column(modifier = Modifier.fillMaxWidth().height(300.dp), horizontalAlignment = Alignment.CenterHorizontally) {

        if (isDone) {
            Image(bitmap.asImageBitmap(), null, modifier = Modifier.fillMaxWidth().height(250.dp))
        } else {
            CircularProgressIndicator()
        }

        Text(component.name)
    }
}


@Composable
fun FiltersPanel(appCtx: AppContext) {

    val scrollState = rememberScrollState()

    val filters = colorMatricesPane()

    val ctx = appCtx.currentImageContext();
    if (ctx != null) {
        val image = remember(ctx.images.size) {
            val c = ctx.imageToDisplay().clone()
            val ratios = calcResize(c, 400, 300)
            c.innerInterface().resize(ratios[0], ratios[1])
            c
        };

        Box(modifier = Modifier.fillMaxHeight().padding(end = 10.dp)) {
            Divider(modifier = Modifier.fillMaxHeight().width(1.dp))

            // Lazy columns would be better but, they have some annoying rebuilding
            // when scrolling up
            Column(modifier = Modifier.fillMaxHeight().verticalScroll(scrollState).padding(horizontal = 15.dp)) {
                filters.forEach {
                    SingleFilterPanel(image, it)
                }

            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = scrollState
                )
            )
        }
    }
}