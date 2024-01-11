import androidx.compose.runtime.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.withLock
import org.jetbrains.skia.*
import java.nio.ByteBuffer

val EPSILON = 0.003F;


class ZilBitmap(private val tempSharedBuffer: SharedBuffer, image: ZilImageInterface) :
    ZilBitmapInterface {
    var inner: ZilImageInterface = image;


    // USE CAREFULLY
    private var info: ImageInfo = ImageInfo.makeUnknown(0, 0)
    private var file: String = ""


    constructor(file: String, tempSharedBuffer: SharedBuffer, image: ZilImageInterface) : this(
        tempSharedBuffer,
        image
    ) {

        this.file = file
    }


    override fun prepareNewFile(bitmap: ProtectedBitmapInterface) {
        // convert depth to u8
        inner.convertDepth(ZilDepth.U8);

        // first convert it to RGBA
        inner.convertColorspace(ZilColorspace.RGBA)

        inner.convertColorspace(ZilColorspace.BGRA)
        // set up canvas
        runBlocking {
            if (bitmap is DesktopProtectedBitmap) {
                bitmap.mutex.withLock {
                    allocBuffer(bitmap.image)
                    installPixels(bitmap.image)
                }
            }
        }
    }


    private fun allocBuffer(bitmap: Bitmap) {

        // check if buffer would fit the new type
        // i.e do not pre-allocate
        val infoSize = info.height * info.width
        val imageSize = inner.height().toInt() * inner.width().toInt();

        info =
            ImageInfo.makeN32(
                inner.width().toInt(),
                inner.height().toInt(),
                ColorAlphaType.UNPREMUL,
                ColorSpace.sRGB
            )

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
                    tempSharedBuffer.nativeBuffer =
                        ByteBuffer.allocateDirect(inner.outputBufferSize().toInt())
                }
                // wrap in a bytebuffer to ensure slice fits

                inner.writeToBuffer(tempSharedBuffer.nativeBuffer, tempSharedBuffer.sharedBuffer, true);
                val wrappedBuffer = ByteBuffer.wrap(tempSharedBuffer.sharedBuffer)

                val slice = wrappedBuffer.slice(0, inner.outputBufferSize().toInt())
                assert(bitmap.installPixels(slice.array()))

            }
        }
    }

    override fun innerInterface(): ZilImageInterface {
        return inner;
    }

    override fun postProcessAlloc(bitmap: ProtectedBitmapInterface) {
        runBlocking {
            if (bitmap is DesktopProtectedBitmap) {
                bitmap.mutex.withLock {
                    allocBuffer(bitmap.image)
                    installPixels(bitmap.image)
                }
            }
        }
    }


    override fun writeToCanvas(bitmap: ProtectedBitmapInterface) {
        inner.convertColorspace(ZilColorspace.BGRA)
        postProcessAlloc(bitmap)
    }

    override fun clone(): ZilBitmap {
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


