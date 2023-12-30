import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import components.ScalableImage
import history.HistoryOperationsEnum
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.skia.*
import java.nio.ByteBuffer
import kotlin.math.absoluteValue

val EPSILON = 0.003F;

class ZilBitmap(private val tempSharedBuffer: SharedBuffer, image: ZilImageInterface) {
    var inner: ZilImageInterface = image;


    // USE CAREFULLY
    private var info: ImageInfo = ImageInfo.makeUnknown(0, 0)
    private var file: String = ""

    // a boolean to see if we have modified stuff
    var isModified by mutableStateOf(true)


    constructor(file: String, tempSharedBuffer: SharedBuffer, image: ZilImageInterface) : this(
        tempSharedBuffer,
        image
    ) {

        this.file = file
    }


    fun prepareNewFile(bitmap: ProtectedBitmap) {
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
        runBlocking {
            bitmap.mutex.withLock {
                allocBuffer(bitmap.bitmap)
                installPixels(bitmap.bitmap)
            }
        }

    }


    private fun allocBuffer(bitmap: Bitmap) {

        // check if buffer would fit the new type
        // i.e do not pre-allocate
        val infoSize = info.height * info.width
        val imageSize = inner.height().toInt() * inner.width().toInt();
        info =
            ImageInfo.makeN32(inner.width().toInt(), inner.height().toInt(), ColorAlphaType.UNPREMUL, ColorSpace.sRGB)

        bitmap.setImageInfo(info)

        if (infoSize != imageSize) {
            // TODO change color type to be pre-multiplied once its exposed from native
            assert(bitmap.allocPixels(info))
        }

        // ensure we can store it
        assert(bitmap.computeByteSize() == info.computeByteSize(info.minRowBytes))
    }

    private fun installPixels(bitmap: Bitmap) {
        runBlocking {
            tempSharedBuffer.mutex.withLock {
                // resize if small
                if (tempSharedBuffer.sharedBuffer.size < inner.outputBufferSize()) {
                    tempSharedBuffer.sharedBuffer = ByteArray(inner.outputBufferSize().toInt())
                }
                if (tempSharedBuffer.nativeBuffer.capacity() < inner.outputBufferSize()) {
                    tempSharedBuffer.nativeBuffer = ByteBuffer.allocateDirect(inner.outputBufferSize().toInt())
                }
                // wrap in a bytebuffer to ensure slice fits

                inner.writeToBuffer(tempSharedBuffer.nativeBuffer, tempSharedBuffer.sharedBuffer);
                val wrappedBuffer = ByteBuffer.wrap(tempSharedBuffer.sharedBuffer)

                val slice = wrappedBuffer.slice(0, inner.outputBufferSize().toInt())
                assert(bitmap.installPixels(slice.array()))

            }
        }
    }


//    fun canvas(): ImageBitmap {
//        return canvasBitmap.asComposeImageBitmap()
//    }

    fun contrast(value: Float, bitmap: ProtectedBitmap) {

        inner.contrast(value)
        postProcessPixelsManipulated(bitmap)

    }

    fun exposure(value: Float, blackPoint: Float = 0.0F, bitmap: ProtectedBitmap) {
        inner.exposure(value, blackPoint)
        postProcessPixelsManipulated(bitmap)
    }

    /**
     * Brighten an image
     *
     * @param value: New value for image, between -1 and 1
     * */
    fun brighten(value: Float, bitmap: ProtectedBitmap) {
        // clamp here
        inner.brightness(value.coerceIn(-1F..1F))
        postProcessPixelsManipulated(bitmap)
    }


    fun stretchContrast(
        value: ClosedFloatingPointRange<Float>, bitmap: ProtectedBitmap
    ) {
        inner.stretchContrast(value.start, value.endInclusive)
        postProcessPixelsManipulated(bitmap)
    }

    fun gaussianBlur(radius: Long, bitmap: ProtectedBitmap) {
        inner.gaussianBlur(radius)
        postProcessPixelsManipulated(bitmap)
    }

    fun medianBlur(radius: Long, bitmap: ProtectedBitmap) {
        // median filter is god slow
        inner.medianBlur(radius)
        postProcessPixelsManipulated(bitmap)
    }

    fun bilateralBlur(radius: Long, bitmap: ProtectedBitmap) {
        inner.bilateralFilter(radius.toInt(), radius.toFloat(), radius.toFloat())
        postProcessPixelsManipulated(bitmap)

    }

    fun boxBlur(radius: Long, bitmap: ProtectedBitmap) {
        inner.boxBlur(radius)
        postProcessPixelsManipulated(bitmap)

    }

    fun flip(bitmap:ProtectedBitmap) {
        inner.flip()
        postProcessAlloc(bitmap.bitmap, bitmap.mutex)
    }

    fun verticalFlip(bitmap: ProtectedBitmap) {
        inner.verticalFlip()
        postProcessAlloc(bitmap.bitmap, bitmap.mutex)

    }

    fun horizontalFlip(bitmap: ProtectedBitmap) {
        inner.flop()
        postProcessAlloc(bitmap.bitmap, bitmap.mutex)
    }

    fun transpose(bitmap: ProtectedBitmap) {
        inner.transpose()
        postProcessAlloc(bitmap.bitmap,bitmap.mutex)
    }
    fun colorMatrix(matrix: FloatArray,bitmap: ProtectedBitmap){
        inner.colorMatrix(matrix)
        postProcessAlloc(bitmap.bitmap,bitmap.mutex)
    }

//    fun save(file: String) {
//        inner.save(file)
//    }

    fun save(file: String, format: ZilImageFormat, appContext: AppContext, coroutineScope: CoroutineScope) {
        coroutineScope.launch(Dispatchers.IO) {
            appContext.initializeImageChange()
            inner.save(file, format)

            appContext.broadcastImageChange()
            appContext.bottomStatus = "Saved ${file.substringAfterLast("/")} to ${file.substringBeforeLast("/")}"
            appContext.showStates.showSaveDialog = false
        }
    }

    private fun postProcessAlloc(bitmap: Bitmap, skiaMutex: Mutex) {
        runBlocking {
            skiaMutex.withLock {
                allocBuffer(bitmap)
                installPixels(bitmap)
                isModified = !isModified
            }
        }
    }


    private fun postProcessPixelsManipulated(bitmap: ProtectedBitmap) {
        runBlocking {
            bitmap.mutex.withLock {
                installPixels(bitmap.bitmap)
                isModified = !isModified
            }
        }
    }

    fun hslAdjust(hue: Float, saturation: Float, lightness: Float, bitmap: ProtectedBitmap) {
        inner.hslAdjust(hue, saturation, lightness)
        postProcessPixelsManipulated(bitmap)

    }

    fun writeToCanvas(bitmap: ProtectedBitmap) {
        inner.convertColorspace(ZilColorspace.BGRA)
        postProcessAlloc(bitmap.bitmap, bitmap.mutex)
    }

    fun clone(): ZilBitmap {
        return ZilBitmap(this.tempSharedBuffer, this.inner.clone())
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
