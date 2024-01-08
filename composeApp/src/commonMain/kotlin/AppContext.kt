import androidx.compose.runtime.*
import events.ExternalNavigationEventBus
import history.HistoryOperations
import history.HistoryOperationsEnum
import history.HistoryResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import java.io.File
import java.nio.ByteBuffer
import java.util.HashMap

/**
 * A buffer used by native methods to write pixels to
 * where java can understand
 *
 * This is used to tie zune-image to java to skia since we can't tie
 * native memory just like that (and compose skia doesn't support native memory locations)
 *
 * We share this amongst multiple images and adjust size to be enough to hold interleaved pixels for the image
 * (means the buffer can be larger than image pixels, but never smaller)
 *
 * Since we share this amongst multiple images which can be called from multiple threads, we protect it with a mutex
 * */
class SharedBuffer {
    /**
     * The sharedBuffer is used from multiple threads, which means it may happen that two threads
     * write to this which is how you loose sleep.
     *
     * So this protects us, before accessing sharedBuffer and nativeBuffer, lock this mutex
     * */
    val mutex: Mutex = Mutex()

    /**
     * A bytearray that can hold image pixels
     *
     * This is used by skia to read image pixels written by the zune-image
     * */
    var sharedBuffer: ByteArray = ByteArray(0)

    /**
     * A bytebuffer used by zune-image to write pixels
     *
     * This is allocated via ByteBuffer.allocateDirect so that it can be used via
     * jni otherwise the native function will raise an exception
     * */
    var nativeBuffer: ByteBuffer = ByteBuffer.allocate(1)

}

/**
 * Image filter values
 *
 * This contains the current image filter values such as brightness, contrast
 * e.t.c  and are modified by image filters
 * */
class FilterValues {
    var contrast by mutableStateOf(0F)
    var brightness by mutableStateOf(0F)
    var gamma by mutableStateOf(0F)
    var exposure by mutableStateOf(0F)
    var boxBlur by mutableStateOf(0L)
    var gaussianBlur by mutableStateOf(0L)
    var stretchContrastRange: MutableState<ClosedFloatingPointRange<Float>> = mutableStateOf(0F..256.0F)
    var hue by mutableStateOf(0f)
    var saturation by mutableStateOf(1f)
    var lightness by mutableStateOf(1f)
    var medianBlur by mutableStateOf(0L)
    var bilateralBlur by mutableStateOf(0L)
}


/**
 * Image group details
 *
 * This contains the image itself ,filter values, history, zoom state etc
 * */
class ImageContext(image: ZilBitmapInterface) {
    var filterValues by mutableStateOf(FilterValues())
    var history by mutableStateOf(HistoryOperations())


    var images = mutableListOf(image)
    var operationsMutex: Mutex = Mutex()
    var imageIsLoaded = MutableStateFlow(false)
    var zoomState by mutableStateOf(ScalableState())
    var imageModified by mutableStateOf(false)
    var file by mutableStateOf(File("/"))

    // Use multiple bitmaps in order to reduce contention, e.g do not fight
    // for the same bitmap in the two two paned stage
    var canvasBitmaps = HashMap<ImageContextBitmaps, ProtectedBitmapInterface>();


    fun initCurrentCanvas(bitmapInterface: ProtectedBitmapInterface) {
        canvasBitmaps[ImageContextBitmaps.CurrentCanvasImage] = bitmapInterface;

        images[0].prepareNewFile(canvasBitmaps[ImageContextBitmaps.CurrentCanvasImage]!!)
    }

    /**
     * Current image
     *
     * @param response: If response is the filter is the same as the previous, we pop the last item
     * NOTE: It is only read and manipulate this only after locking the
     * operations mutex, otherwise bad things will happen (race conditions + native code)
     * */
    fun currentImage(response: HistoryResponse): ZilBitmapInterface {
        // peek into history to see if we need to create a new image, add it to the stack and return it or
        // if the operation has a trivial undo, just let it be
        // we are assured that history just pushed this operation since we require HistoryResponse
        // as a parameter
        if (history.getHistory().isNotEmpty() && !history.getHistory().last()
                .trivialUndo() && response != HistoryResponse.DummyOperation
        ) {
            // not simple to undo, so back up what we have
            val lastImage = images.last().clone()
            images.add(lastImage)
        }
//        if (history.getHistory().size > 1 && !history.getHistory().last().trivialUndo()) {
//            return images.last().clone()
//        }

        return images.last()
    }

    fun firstImage(): ZilBitmapInterface? {
        return images.firstOrNull()
    }

    fun imageToDisplay(): ZilBitmapInterface {
        return images.last()
    }

    fun resetStates(newImage: ZilBitmapInterface) {
        filterValues = FilterValues()
        history = HistoryOperations()
        images = mutableListOf(newImage)
        images[0].writeToCanvas(canvasBitmaps[ImageContextBitmaps.CurrentCanvasImage]!!)
        imageModified = !imageModified
        // imageIsLoaded = true
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
    var imageSpecificStates: LinkedHashMap<File, ImageContext> = LinkedHashMap()

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
     * Layout of the image space, can either be single paned i.e. one image
     * or two paned showing unedited image + edited image
     * */
    var imageSpaceLayout by mutableStateOf(ImageSpaceLayout.SingleLayout)


    /**
     *  Contains a shared buffer used by images when we want to write
     *
     *  The image layout is usually ZilBitmapInterface->Skia, but since I can't hand
     *  skia a native memory pointer, we need an intermediate buffer
     *  ZilBitmapInterface->ByteBuffer->Skia, skia will set of to write its own native
     *  memory which it will then use, ignoring our buffer,
     *  this means the buffer can be reused multiple times by separate image threads
     *
     *  So it's here to facilitate that.
     *
     *  When an image wants to display it's output it's going to lock this shared buffer, write to skia and finally
     *  unlock, skia will read its pointer, allocate what it needs and do it's internal shenanigans
     *
     * */
    var sharedBuffer: SharedBuffer = SharedBuffer()

    /**
     * Callers can subscribe to this to be informed of
     * a tab being closed and act appropriately
     * */
    var changeOnCloseTab by mutableStateOf(false)


    /**
     * Initialize image details
     *
     * If the image was already loaded, we preserve some details such as zoom state
     * */
    fun initializeImageSpecificStates(image: ZilBitmapInterface) {
        if (imageSpecificStates.containsKey(imFile)) {
            // preserve things like zoom even when reloading
            imageSpecificStates[imFile]?.resetStates(image);
        } else {
            imageSpecificStates[imFile] = ImageContext(image)
        }

        // move the tab index to the newly loaded tab
        imageSpecificStates.asSequence().forEachIndexed { idx, it ->
            if (it.key == imFile) {
                tabIndex = idx
            }
        }
        broadcastImageChange()
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
        return imageSpecificStates[imFile]!!.imageIsLoaded.asStateFlow().value
    }

    fun setImageIsLoaded(yes: Boolean) {
        imageSpecificStates[imFile]!!.imageIsLoaded.value = yes
    }

    fun appendToHistory(newValue: HistoryOperationsEnum, value: Any? = null): HistoryResponse {

        return imageSpecificStates[imFile]!!.history.addHistory(newValue, value)
    }

    fun resetHistory() {
        imageSpecificStates[imFile]?.history?.reset()
    }

    fun imageFilterValues(): FilterValues? {
        return imageSpecificStates[imFile]?.filterValues
    }

    fun imageStates(): LinkedHashMap<File, ImageContext> {
        return imageSpecificStates
    }

    /**
     * Return the context of the currently displayed image
     * */
    fun currentImageContext(): ImageContext? {
        return imageSpecificStates[imFile]
    }

    fun operationIsOngoing(): Boolean {
        return showStates.showTopLinearIndicator
    }

    fun removeFile(file: File) {
        imageStates().remove(file)

        val value = tabIndex - 1;
        tabIndex = value.coerceIn(
            minimumValue = 0,
            maximumValue = null
        )

        // set the new imFile
        imageStates().asSequence()
            .forEachIndexed { idx, it ->

                if (idx == tabIndex) {
                    imFile = it.key
                }
            }

        changeOnCloseTab = !changeOnCloseTab
        broadcastImageChange()

    }
//
//    fun getImage(): ZilBitmapInterface {
//
//        // if this is null it means the initializer didn't initialize the image
//        return imageSpecificStates[imFile]!!.currentImage()
//    }

}


class RecomposeWidgets {
    var rerunHistogram by mutableStateOf(false)
    var rerunImageSpecificStates by mutableStateOf(false)

}