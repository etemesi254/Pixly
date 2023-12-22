import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.colorspace.ColorSpace
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import components.ScalableImage
import history.HistoryOperationsEnum
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.skia.*
import java.nio.ByteBuffer
import java.nio.ByteOrder


class ZilBitmap() {
    var inner: ZilImage = ZilImage();

    // it may happen that this may be called from multiple coroutines
    // so we keep it single threaded since we do a lot of memory sharing
    // otherwise we will crash.
    //
    // NB: All calls that can be called from separate coroutines should use this
    private val mutex = Mutex();
    private var canvasBitmap = Bitmap();
    private var info: ImageInfo = ImageInfo.makeUnknown(0, 0);
    private var file: String = "";

    // a boolean to see if we have modified stuff
    private var isModified by mutableStateOf(true)


    constructor(file: String) : this() {

        this.file = file
        inner = ZilImage(file)
        prepareNewFile()
    }

    private fun prepareNewFile() {
        // convert depth to u8
        inner.convertDepth(ZilDepth.U8);

        // first convert it to RGBA
        inner.convertColorspace(ZilColorspace.RGBA)
        /*
        * BUGS BUGS BUGS
        *
        * Okay so you see how we have big endian and little endian systems
        *
        * Skia works with little endian,
        * it tells you to give it bytes, and then it renders them on screen
        * this kinda matters, because skia uses ARGB8888, which now  says
        * components are laid out in A, R, G finally B.
        *
        * BUT skia stores colors in 32-bit integer, which makes sense, so you'd assume that
        * A -> 24-32
        * R -> 24-16
        * G -> 16-08
        * B -> 08-00
        *
        * Since that's how you'd lay them in memory.
        *
        * But it's actually
        *
        * A -> 00-08
        * R -> 08-16
        * G -> 16-24
        * B -> 24-32
        *
        * Since the 32 bit int is laid out in little endian, LSB is first
        *
        * zune doesn't know this because it handles individual bytes, so we have
        * to handle it here.
        *
        * The solution is easy, once you see it, BGRA(Little Endian) == ARGB , because you literally
        * read the letters in the opposite way, so we have to do another conversion to BGRA so that
        * skia can see it in ARGB
        *
        * computers :)
        * */
        inner.convertColorspace(ZilColorspace.BGRA)
        // set up canvas
        allocBuffer()
        installPixels()

    }


    private fun allocBuffer() {
        // check if buffer would fit the new type
        // i.e do not pre-allocate
        val infoSize = info.height * info.width
        val imageSize = inner.height().toInt() * inner.width().toInt();
        info = ImageInfo.makeN32(inner.width().toInt(), inner.height().toInt(), ColorAlphaType.UNPREMUL);

        canvasBitmap.setImageInfo(info)

        if (infoSize != imageSize) {
            // TODO change color type to be pre-multiplied once its exposed from native
            assert(canvasBitmap.allocPixels(info))
        }

        // canvasBitmap.()
        // ensure we can store it
        assert(canvasBitmap.computeByteSize() == info.computeByteSize(info.minRowBytes))
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun installPixels() {
        val buffer = inner.toBuffer();
        assert(canvasBitmap.installPixels(buffer.asByteArray()))
    }

    @Composable
    fun image(appContext: AppContext) {
        /* Let's talk about computers and invalidations
        *
        * Say you have an image that takes 10 seconds to decode, so you alloc and decode
        *  remember that is happening in a different coroutine, so it may happen that
        * you click and open a new image which takes 2 seconds, so that coroutine finishes and renders
        * dope
        *
        * but what about this old one??? They share the same buffer and bitmap.
        *
        * So when the image renderer tries to render, nullptr :)
        *
        *
        * To solve this, we can just share locks between the renderer and the loader, so that
        * only one thing runs at a time
        */
        key(isModified) {
            if (mutex.tryLock()) {
                // unlock when composition is done
                SideEffect {
                    if (mutex.isLocked) {
                        mutex.unlock()
                    }
                }
                /*
                * Let's talk about getting compose to redraw.
                *
                * So we need it to redraw the image for real time effects viewing
                *
                * but we don't have a way to tell compose, hey, redraw me
                * So we make it think that the modifier has been modified, forcing a redraw
                *
                *
                * All image filters modify the isModified parameter, which acts as a signal
                * telling compose to redraw the image
                *
                * */
                ScalableImage(
                    appContext,
                    modifier = Modifier.fillMaxSize().modifyOnChange(isModified)
                )
            }
        }

    }

    fun canvas(): ImageBitmap {
        return canvasBitmap.asComposeImageBitmap()
    }


    suspend fun loadFile(file: String) {
        // ensure we are the only ones who can access this at a go,
        // because it happens that we may load too fast and we are called from coroutines
        // and invalidate our pointer causing skia to break
        mutex.withLock {
            inner.loadFile(file)
            this.file = file;
            prepareNewFile()
        }
    }


    fun contrast(appContext: AppContext, coroutineScope: CoroutineScope, value: Float) {
        appContext.initializeImageChange()
        // store history
        appContext.appendToHistory(HistoryOperationsEnum.Contrast, value)

        coroutineScope.launch(Dispatchers.IO) {
            mutex.withLock {
                inner.contrast(value)
                appContext.imageFilterValues().contrast = value
                postProcessPixelsManipulated(appContext)
            }
        }
    }

    fun gamma(appContext: AppContext, coroutineScope: CoroutineScope, value: Float) {
        appContext.appendToHistory(HistoryOperationsEnum.Gamma, value)

        coroutineScope.launch(Dispatchers.IO) {
            mutex.withLock {
                appContext.initializeImageChange()
                inner.gamma(value)
                appContext.imageFilterValues().gamma = value
                postProcessPixelsManipulated(appContext)
            }
        }

    }

    fun exposure(appContext: AppContext, coroutineScope: CoroutineScope, value: Float, blackPoint: Float = 0.0F) {
        appContext.appendToHistory(HistoryOperationsEnum.Exposure, value)

        coroutineScope.launch(Dispatchers.IO) {
            mutex.withLock {
                appContext.initializeImageChange()
                inner.exposure(value, blackPoint)
                appContext.imageFilterValues().exposure = value
                postProcessPixelsManipulated(appContext)
            }
        }
    }

    fun brighten(appContext: AppContext, coroutineScope: CoroutineScope, value: Float) {
        appContext.initializeImageChange()
        appContext.appendToHistory(HistoryOperationsEnum.Brighten, value)

        coroutineScope.launch(Dispatchers.IO) {
            mutex.withLock {
                inner.brightness(value)
                appContext.imageFilterValues().brightness = value
                postProcessPixelsManipulated(appContext)
            }
        }
    }

    fun stretchContrast(
        appContext: AppContext,
        coroutineScope: CoroutineScope,
        value: ClosedFloatingPointRange<Float>
    ) {

        appContext.initializeImageChange()
        appContext.appendToHistory(HistoryOperationsEnum.Levels, value)

        coroutineScope.launch(Dispatchers.IO) {
            mutex.withLock {
                inner.stretchContrast(value.start, value.endInclusive)
                appContext.imageFilterValues().stretchContrastRange.value = value
                postProcessPixelsManipulated(appContext)
            }
        }
    }

    fun gaussianBlur(appContext: AppContext, coroutineScope: CoroutineScope, radius: Long) {
        appContext.initializeImageChange()
        appContext.appendToHistory(HistoryOperationsEnum.GaussianBlur, radius)

        coroutineScope.launch(Dispatchers.IO) {
            mutex.withLock {
                inner.gaussianBlur(radius)
                appContext.imageFilterValues().gaussianBlur = radius.toUInt()
                postProcessPixelsManipulated(appContext)
            }
        }
    }

    fun boxBlur(appContext: AppContext, coroutineScope: CoroutineScope, radius: Long) {
        appContext.initializeImageChange()
        appContext.appendToHistory(HistoryOperationsEnum.BoxBlur, radius)

        coroutineScope.launch(Dispatchers.IO) {
            mutex.withLock {
                inner.boxBlur(radius)
                appContext.imageFilterValues().boxBlur = radius.toUInt()

                postProcessPixelsManipulated(appContext)
            }
        }
    }

    fun flip(appContext: AppContext, coroutineScope: CoroutineScope) {
        appContext.initializeImageChange()
        coroutineScope.launch(Dispatchers.IO) {
            mutex.withLock {
                inner.flip()
                postProcessAlloc(appContext)
            }
        }
    }

    fun verticalFlip(appContext: AppContext, coroutineScope: CoroutineScope) {
        appContext.initializeImageChange()
        appContext.appendToHistory(HistoryOperationsEnum.VerticalFlip)

        coroutineScope.launch(Dispatchers.IO) {
            mutex.withLock {
                inner.verticalFlip()
                postProcessAlloc(appContext)
            }
        }
    }

    fun flop(appContext: AppContext, coroutineScope: CoroutineScope) {
        appContext.initializeImageChange()
        appContext.appendToHistory(HistoryOperationsEnum.HorizontalFlip)

        coroutineScope.launch(Dispatchers.IO) {
            mutex.withLock {
                inner.flop()
                postProcessAlloc(appContext)
            }
        }
    }

    fun transpose(appContext: AppContext, coroutineScope: CoroutineScope) {
        appContext.initializeImageChange()
        appContext.appendToHistory(HistoryOperationsEnum.Transposition)

        coroutineScope.launch(Dispatchers.IO) {
            mutex.withLock {
                inner.transpose()
                postProcessAlloc(appContext)
            }
        }
    }

    fun save(file: String) {
        inner.save(file)
    }

    fun save(file: String, format: ZilImageFormat) {
        inner.save(file, format)
    }

    private fun postProcessAlloc(appContext: AppContext) {
        allocBuffer()
        installPixels()
        isModified = !isModified
        appContext.broadcastImageChange()
    }


    private fun postProcessPixelsManipulated(appContext: AppContext) {
        installPixels()
        isModified = !isModified;
        appContext.broadcastImageChange()
    }

}

const val BPP = 4


//class ZilBitmap(
//    val internalBuffer: ByteArray,
//    override val height: Int,
//    override val width: Int,
//    override val colorSpace: ColorSpace = ColorSpaces.Srgb,
//    override val config: ImageBitmapConfig = ImageBitmapConfig.Argb8888,
//    override val hasAlpha: Boolean = true,
//) : ImageBitmap {
//    override fun prepareToDraw() = Unit
//
//    override fun readPixels(
//        buffer: IntArray,
//        startX: Int,
//        startY: Int,
//        width: Int,
//        height: Int,
//        bufferOffset: Int,
//        stride: Int
//    ) {
//
//        // similar to https://cs.android.com/android/platform/superproject/+/42c50042d1f05d92ecc57baebe3326a57aeecf77:frameworks/base/graphics/java/android/graphics/Bitmap.java;l=2007
//        val lastScanline: Int = bufferOffset + (height - 1) * stride
//        require(startX >= 0 && startY >= 0)
//        require(width > 0 && startX + width <= this.width)
//        require(height > 0 && startY + height <= this.height)
//        require(bufferOffset >= 0 && bufferOffset + width <= buffer.size)
//        require(lastScanline >= 0 && lastScanline + width <= buffer.size)
//        require(stride == width) // TODO: Allow strides, will be fixed soon
//
//        // similar to https://cs.android.com/android/platform/superproject/+/9054ca2b342b2ea902839f629e820546d8a2458b:frameworks/base/libs/hwui/jni/Bitmap.cpp;l=898;bpv=1
//
//        val start = (startY * stride) + startX
//        val end = start + buffer.size
//        // wrap it in a bytebuffer
//        val slice = ByteBuffer.wrap(internalBuffer).slice(start, end).array()
//
//
//        // read pixels
//        println("${start} ${end} ${startX} ${startY}")
//        slice.putBytesIntoX(buffer, bufferOffset, slice.size / BPP)
//    }
//
//}
//
//internal fun ByteArray.putBytesIntoX(array: IntArray, offset: Int, length: Int) {
//    ByteBuffer.wrap(this)
//        .order(ByteOrder.LITTLE_ENDIAN) // to return ARGB
//        .asIntBuffer()
//        .get(array, offset, length)
//}
//
//@OptIn(ExperimentalUnsignedTypes::class)
//fun ZilImage.asZilBitmap(): ZilBitmap {
//    return ZilBitmap(this.toBuffer().asByteArray(), height = this.height().toInt(), width = this.width().toInt())
//}

/** A stub modifier that can be used to tell compose to
 * rebuild a widget
 * */
fun Modifier.modifyOnChange(modified: Boolean): Modifier {
    return this
}
