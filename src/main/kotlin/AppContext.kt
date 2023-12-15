import androidx.compose.runtime.*
import components.ScalableState
import events.ExternalNavigationEventBus
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import java.io.File

class FilterValues {
    val contrast by mutableStateOf(0F);
    val gamma by mutableStateOf(0F)
    val exposure by mutableStateOf(0F)

}

@OptIn(ExperimentalSplitPaneApi::class)
class AppContext {

    var paths: MutableList<File> = mutableListOf()
    var position = 0;

    var showStates by mutableStateOf(ShowModifiers())

    var bottomStatus by mutableStateOf("")

    var isFirstDraw by mutableStateOf(false)

    var imageIsLoaded by mutableStateOf(false)
    var externalNavigationEventBus by mutableStateOf(ExternalNavigationEventBus())

    var image by mutableStateOf(ZilImageAndBitmapInterop());
    var imFile by mutableStateOf(File(""))
    var rootDirectory by mutableStateOf("/")

    var recomposeWidgets by mutableStateOf(RecomposeWidgets())

    var filters: MutableMap<File, FilterValues> by mutableStateMapOf()

    var zoomState by mutableStateOf(ScalableState())
    var openPane by mutableStateOf(RightPaneOpened.None);


    fun createFilterMap() {
        filters[imFile] = FilterValues()
    }

    fun returnFilterValues(): FilterValues {
        return filters[imFile]!!
    }

    fun initializeImageChange() {
        showStates.showTopLinearIndicator = true;

    }

    fun broadcastImageChange() {
        // tell whoever is listening to this to rebuild
        recomposeWidgets.rerunHistogram = recomposeWidgets.rerunHistogram.xor(true)
        showStates.showTopLinearIndicator = false;

    }
}

class RecomposeWidgets {
    var rerunHistogram by mutableStateOf(false)

}