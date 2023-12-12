import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import components.*
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

enum class FiltersPaneOrdering {
    ImageInfo,
    LightFilters,
    OrientationFilters,
    HistogramFilters,
    Levels,
}

@Composable
fun histogramPane(appCtx: AppContext) {
    CollapsibleBox("Histogram", appCtx.showStates.showHistogram, onTopRowClicked = {
        appCtx.showStates.showHistogram = appCtx.showStates.showHistogram.not()
    }) {
        Box(modifier = Modifier.heightIn(0.dp, 250.dp)) {
            HistogramChart(appCtx)
        }
    }
}


@Composable
fun FiltersPane(appCtx: AppContext) {
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

    AnimatedVisibility(
        visible = appCtx.imageIsLoaded && appCtx.showStates.showImageEditors,
        enter = slideInHorizontally { with(density) { +40.dp.roundToPx() } },
        exit = slideOutHorizontally { with(density) { +400.dp.roundToPx() } },
        modifier = Modifier.fillMaxHeight()
    ) {
        Box(modifier = Modifier.modifyOnChange(orderChanged).fillMaxHeight().padding(horizontal = 5.dp)) {
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
                        FiltersPaneOrdering.ImageInfo -> {
                            ReorderableItem(state, key = item, index = it) {
                                ImageInformationComponent(appCtx)

                            }
                        }

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

                        FiltersPaneOrdering.HistogramFilters -> {
                            ReorderableItem(
                                state,
                                key = item,
                                index = it
                            ) {
                                histogramPane(appCtx)
                            }
                        }

                        FiltersPaneOrdering.Levels -> {
                            ReorderableItem(state, key = item, index = it) {
                                LevelsFiltersComponent(appCtx)
                            }
                        }
                    }
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