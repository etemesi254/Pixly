import androidx.compose.runtime.*
import components.ScalableState
import events.ExternalNavigationEventBus
import history.HistoryOperations
import history.HistoryOperationsEnum
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import java.io.File

class FilterValues {
    var contrast by mutableStateOf(0F)
    var brightness by mutableStateOf(0F)
    var gamma by mutableStateOf(0F)
    var exposure by mutableStateOf(0F)
    var boxBlur by mutableStateOf(0u)
    var gaussianBlur by mutableStateOf(0u)
    var stretchContrastRange: MutableState<ClosedFloatingPointRange<Float>> = mutableStateOf(0F..256.0F)


}


class ImageSpecificStates {
    val filterValues by mutableStateOf(FilterValues())
    var history by mutableStateOf(HistoryOperations())
    var image by mutableStateOf(ZilBitmap())
    var imageIsLoaded by mutableStateOf(false)
    var zoomState by mutableStateOf(ScalableState())



    constructor()
    constructor(image: ZilBitmap) {
        println("${image.inner.width()} ${image.inner.height()}")

        this.image = image;
    }
}

@OptIn(ExperimentalSplitPaneApi::class)
class AppContext {

    var paths: MutableList<File> = mutableListOf()
    var position = 0;

    var showStates by mutableStateOf(ShowModifiers())

    var bottomStatus by mutableStateOf("")

    var isFirstDraw by mutableStateOf(false)

    var externalNavigationEventBus by mutableStateOf(ExternalNavigationEventBus())

    var imFile by mutableStateOf(File(""))
    var rootDirectory by mutableStateOf("/")

    var recomposeWidgets by mutableStateOf(RecomposeWidgets())

    private var imageSpecificStates: LinkedHashMap<File, ImageSpecificStates> = LinkedHashMap()

    var tabIndex by mutableStateOf(0)

    var openedRightPane by mutableStateOf(RightPaneOpened.None)

    var openedLeftPane by mutableStateOf(LeftPaneOpened.None)

    /**
     * Contains history of currently executed image operaions
     * */
    fun initializeImageSpecificStates(image: ZilBitmap) {
        imageSpecificStates[imFile] = ImageSpecificStates(image)

        // move the tab index to the newly loaded tab
        imageSpecificStates.asSequence().forEachIndexed { idx, it ->
            if (it.key == imFile) {
                tabIndex = idx
            }
        }
        recomposeWidgets.rerunImageSpecificStates = recomposeWidgets.rerunImageSpecificStates
    }

    fun returnFilterValues(): FilterValues {
        return imageSpecificStates[imFile]!!.filterValues
    }

    fun initializeImageChange() {
        showStates.showTopLinearIndicator = true;

    }

    fun broadcastImageChange() {
        // tell whoever is listening to this to rebuild
        recomposeWidgets.rerunHistogram = !recomposeWidgets.rerunHistogram
        recomposeWidgets.rerunImageSpecificStates = !recomposeWidgets.rerunImageSpecificStates
        showStates.showTopLinearIndicator = false;

    }

    fun getHistory(): HistoryOperations {
        imageSpecificStates.putIfAbsent(imFile, ImageSpecificStates())

        return imageSpecificStates[imFile]!!.history

    }

    fun imageIsLoaded(): Boolean {
        if (imFile == File("")) {
            return false
        }
        if (imageSpecificStates[imFile] == null) {
            return false
        }
        return imageSpecificStates[imFile]!!.imageIsLoaded
    }

    fun setImageIsLoaded(yes: Boolean) {
        imageSpecificStates[imFile]!!.imageIsLoaded = yes
    }

    fun setHistory(history: HistoryOperations) {
        imageSpecificStates.putIfAbsent(imFile, ImageSpecificStates())
        imageSpecificStates[imFile]!!.history = history;
    }

    fun appendToHistory(newValue: HistoryOperationsEnum, value: Any? = null) {
        imageSpecificStates.putIfAbsent(imFile, ImageSpecificStates())

        imageSpecificStates[imFile]!!.history.addHistory(newValue, value);
    }

    fun resetHistory() {
        imageSpecificStates.putIfAbsent(imFile, ImageSpecificStates())
        imageSpecificStates[imFile]!!.history.reset()
    }

    fun imageFilterValues(): FilterValues {
        imageSpecificStates.putIfAbsent(imFile, ImageSpecificStates())
        return imageSpecificStates[imFile]!!.filterValues
    }

    fun imageStates(): LinkedHashMap<File, ImageSpecificStates> {
        return imageSpecificStates
    }

    fun currentImageState(): ImageSpecificStates {
        return imageSpecificStates[imFile]!!
    }

    fun getImage(): ZilBitmap {

        // if this is null it means the initializer didn't initialize the image
        return imageSpecificStates[imFile]!!.image
    }
}


class RecomposeWidgets {
    var rerunHistogram by mutableStateOf(false)
    var rerunImageSpecificStates by mutableStateOf(false)

}