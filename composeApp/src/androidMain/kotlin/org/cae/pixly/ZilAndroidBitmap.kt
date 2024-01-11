package org.cae.pixly

import ProtectedBitmapInterface
import SharedBuffer
import ZilBitmapInterface
import ZilColorspace
import ZilDepth
import ZilImageFormat
import ZilImageInterface
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.nio.ByteBuffer

class AndroidProtectedBitmap() : ProtectedBitmapInterface {
    var bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

    private val mutex = Mutex()


    override fun asImageBitmap(): ImageBitmap {
        return bitmap.asImageBitmap()
    }

    override fun mutex(): Mutex {
        return mutex
    }
}


class ZilAndroidBitmap(private val image: ZilImageInterface, private val androidSharedBuffer: SharedBuffer) :
    ZilBitmapInterface {


    override fun writeToCanvas(bitmap: ProtectedBitmapInterface) {
        image.convertColorspace(ZilColorspace.RGBA)
        postProcessAlloc(bitmap)
    }

    private fun postProcessAlloc(bitmap: ProtectedBitmapInterface) {
        runBlocking {
            if (bitmap is AndroidProtectedBitmap) {
                bitmap.mutex().withLock {
                    allocBuffer(bitmap)
                    installPixels(bitmap)
                }
            }
        }
    }


    private fun postProcessPixelsManipulated(bitmap: ProtectedBitmapInterface) {
        runBlocking {
            bitmap.mutex().withLock {
                if (bitmap is AndroidProtectedBitmap) {
                    installPixels(bitmap)
                }
            }
        }
    }

    private fun allocBuffer(bitmap: AndroidProtectedBitmap) {
        val imWidth = image.width().toInt();
        val imHeight = image.height().toInt();

//        assert(image.colorspace() == ZilColorspace.BGRA);
        assert(image.depth() == ZilDepth.U8);
        if ((imWidth != bitmap.bitmap.width) || (imHeight != bitmap.bitmap.height)) {
            bitmap.bitmap = Bitmap.createBitmap(imWidth, imHeight, Bitmap.Config.ARGB_8888)
        }
    }

    private fun installPixels(bitmap: AndroidProtectedBitmap) {
        runBlocking {
            val imWidth = image.width().toInt();
            val imHeight = image.height().toInt();

            androidSharedBuffer.mutex.withLock {
                if (androidSharedBuffer.nativeBuffer.capacity() < image.outputBufferSize()) {
                    androidSharedBuffer.nativeBuffer = ByteBuffer.allocateDirect(image.outputBufferSize().toInt())
                }
                image.writeToBuffer(androidSharedBuffer.nativeBuffer, androidSharedBuffer.sharedBuffer, false)
                // set it to zero
                androidSharedBuffer.nativeBuffer.rewind()
                // copy to bitmap
                bitmap.bitmap.copyPixelsFromBuffer(androidSharedBuffer.nativeBuffer)
                // reset position

            }

        }
    }

    override fun prepareNewFile(bitmap: ProtectedBitmapInterface) {
        image.convertDepth(ZilDepth.U8)
        // OO one more cool thing.
        // WHY??????
        // Bitmap advertises ARGB suuport,
        // but if you use ARGB+ bytebuffer you get a BGRA kinda format
        // it's RGBA that works. I don't know what endian shenanigans got us here
        // But if it works don't touch
        image.convertColorspace(ZilColorspace.RGBA)
        //image.convertColorspace(ZilColorspace.ARGB)

        runBlocking {
            if (bitmap is AndroidProtectedBitmap) {
                bitmap.mutex().withLock {
                    allocBuffer(bitmap)
                    installPixels(bitmap)
                }
            }
        }

    }

    override fun clone(): ZilBitmapInterface {
        return ZilAndroidBitmap(image.clone(), androidSharedBuffer)
    }

    override fun hslAdjust(hue: Float, saturation: Float, lightness: Float, bitmap: ProtectedBitmapInterface) {
        image.hslAdjust(hue, saturation, lightness)
        postProcessPixelsManipulated(bitmap)
    }

    override fun contrast(value: Float, bitmap: ProtectedBitmapInterface) {
        image.contrast(value)
        postProcessPixelsManipulated(bitmap)

    }

    override fun exposure(value: Float, blackPoint: Float, bitmap: ProtectedBitmapInterface) {
        image.exposure(value, blackPoint)
        postProcessPixelsManipulated(bitmap)
    }

    override fun brighten(value: Float, bitmap: ProtectedBitmapInterface) {
        // clamp here
        image.brightness(value.coerceIn(-1F..1F))
        postProcessPixelsManipulated(bitmap)
    }


    override fun stretchContrast(
        value: ClosedFloatingPointRange<Float>, bitmap: ProtectedBitmapInterface
    ) {
        image.stretchContrast(value.start, value.endInclusive)
        postProcessPixelsManipulated(bitmap)
    }

    override fun gaussianBlur(radius: Long, bitmap: ProtectedBitmapInterface) {
        image.gaussianBlur(radius)
        postProcessPixelsManipulated(bitmap)
    }

    override fun medianBlur(radius: Long, bitmap: ProtectedBitmapInterface) {
        image.medianBlur(radius)
        postProcessPixelsManipulated(bitmap)
    }

    override fun bilateralBlur(radius: Long, bitmap: ProtectedBitmapInterface) {
        image.bilateralFilter(radius.toInt(), radius.toFloat(), radius.toFloat())
        postProcessPixelsManipulated(bitmap)

    }

    override fun boxBlur(radius: Long, bitmap: ProtectedBitmapInterface) {
        image.boxBlur(radius)
        postProcessPixelsManipulated(bitmap)

    }

    override fun flip(bitmap: ProtectedBitmapInterface) {
        image.flip()
        postProcessAlloc(bitmap)
    }

    override fun verticalFlip(bitmap: ProtectedBitmapInterface) {
        image.verticalFlip()
        postProcessAlloc(bitmap)

    }

    override fun horizontalFlip(bitmap: ProtectedBitmapInterface) {
        image.flop()
        postProcessAlloc(bitmap)
    }

    override fun transpose(bitmap: ProtectedBitmapInterface) {
        image.transpose()
        postProcessAlloc(bitmap)
    }

    override fun colorMatrix(matrix: FloatArray, bitmap: ProtectedBitmapInterface) {
        image.colorMatrix(matrix)
        postProcessAlloc(bitmap)
    }

    override fun width(): UInt {
        return image.width()
    }

    override fun height(): UInt {
        return image.height()
    }

    override fun innerInterface(): ZilImageInterface {
        return image;
    }

    override fun save(name: String, format: ZilImageFormat) {
        image.save(name, format)
    }

    override fun rotate(angle: Float, bitmap: ProtectedBitmapInterface) {
        image.rotate(angle);
        postProcessAlloc(bitmap);
    }

    override fun sobel(bitmap: ProtectedBitmapInterface) {
        image.sobel()
        postProcessPixelsManipulated(bitmap)
    }

}