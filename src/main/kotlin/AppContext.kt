import androidx.compose.runtime.*
import components.ScalableState
import events.ExternalNavigationEventBus
import history.HistoryOperations
import history.HistoryOperationsEnum
import kotlinx.coroutines.sync.Mutex
import java.io.File
import java.nio.ByteBuffer

class SharedBuffer {
    val mutex: Mutex = Mutex()
    var sharedBuffer: ByteArray = ByteArray(0)
    var nativeBuffer: ByteBuffer = ByteBuffer.allocate(0)

}


class FilterValues {
    var contrast by mutableStateOf(0F)
    var brightness by mutableStateOf(0F)
    var gamma by mutableStateOf(0F)
    var exposure by mutableStateOf(0F)
    var boxBlur by mutableStateOf(0u)
    var gaussianBlur by mutableStateOf(0u)
    var stretchContrastRange: MutableState<ClosedFloatingPointRange<Float>> = mutableStateOf(0F..256.0F)
}


class ImageSpecificStates(tempSharedBuffer: SharedBuffer) {
    val filterValues by mutableStateOf(FilterValues())
    var history by mutableStateOf(HistoryOperations())
    var image by mutableStateOf(ZilBitmap(tempSharedBuffer))
    var imageIsLoaded by mutableStateOf(false)
    var zoomState by mutableStateOf(ScalableState())


    constructor(image: ZilBitmap, tempSharedBuffer: SharedBuffer) : this(tempSharedBuffer) {
        this.image = image;
    }
}

class AppContext {

    /**
     * Contains possible paths that may be images, useful for
     * left and right movement
     * */
    var paths: MutableList<File> = mutableListOf()

    /**
     * Contains current position for keeping track
     * of what is next and previous in left,right navigation
     * */
    var pathPosition = 0;

    /**
     * Contains information about what to show when the decision is
     * a true or false decision, e.g. whether to show a dialog box or not
     * */
    var showStates by mutableStateOf(ShowModifiers())

    /**
     * Contains text for the bottom status
     * */
    var bottomStatus by mutableStateOf("")

    /**
     * External navigations, for passing keyboard events to compose
     * */
    var externalNavigationEventBus by mutableStateOf(ExternalNavigationEventBus())

    /**
     * Current image file we are displaying
     * */
    var imFile by mutableStateOf(File(""))

    /**
     * Current directory we are showing on the directory picker
     * */
    var rootDirectory by mutableStateOf("/")


    var recomposeWidgets by mutableStateOf(RecomposeWidgets())

    /**
     * Contains each image specific states, each image can be seen as a file+ info about it
     * and the information includes the image filter states, image history, image details etc
     * */
    private var imageSpecificStates: LinkedHashMap<File, ImageSpecificStates> = LinkedHashMap()

    /**
     * Contains information on which tab the user is currently on
     * Each tab can be a different image
     * */
    var tabIndex by mutableStateOf(0)

    /**
     * Contains information on what right pane was opened
     * when None, indicates no pane is currently opened
     * */
    var openedRightPane by mutableStateOf(RightPaneOpened.None)

    /**
     * Contains information on what left pane was opened,
     * when None, indicates no pane is opened
     * */
    var openedLeftPane by mutableStateOf(LeftPaneOpened.None)


    /**
     *  Contains a shared buffer used by images when we want to write
     *
     *  The image layout is usually ZilBitmap->Skia, but since I can't hand
     *  skia a native memory pointer, we need an intermediate buffer
     *  ZilBitmap->ByteBuffer->Skia, skia will set of to write its own native
     *  memory which it will then use, ignoring our buffer,
     *  this means the buffer can be reused multiple times by separate image threads
     *
     *  So it's here to facilitate that.
     *
     *  When an image wants to display it's output it's gonna lock this shared buffer, write to skia and finally
     *  unlock, skia will read its pointer, allocate what it needs and do it's internal shenanigans
     *
     * */
    var sharedBuffer: SharedBuffer = SharedBuffer()

    /**
     * Contains history of currently executed image operaions
     * */
    fun initializeImageSpecificStates(image: ZilBitmap) {
        imageSpecificStates[imFile] = ImageSpecificStates(image, sharedBuffer)

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
        showStates.showTopLinearIndicator = true

    }

    fun broadcastImageChange() {
        // tell whoever is listening to this to rebuild
        recomposeWidgets.rerunHistogram = !recomposeWidgets.rerunHistogram
        recomposeWidgets.rerunImageSpecificStates = !recomposeWidgets.rerunImageSpecificStates
        showStates.showTopLinearIndicator = false

    }

    fun getHistory(): HistoryOperations? {

        return imageSpecificStates[imFile]?.history

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
        imageSpecificStates[imFile]?.history = history;
    }

    fun appendToHistory(newValue: HistoryOperationsEnum, value: Any? = null) {

        imageSpecificStates[imFile]?.history?.addHistory(newValue, value);
    }

    fun resetHistory() {
        imageSpecificStates[imFile]?.history?.reset()
    }

    fun imageFilterValues(): FilterValues? {
        return imageSpecificStates[imFile]?.filterValues
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