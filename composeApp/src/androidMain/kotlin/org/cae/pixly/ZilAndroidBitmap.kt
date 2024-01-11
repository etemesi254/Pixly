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

    override fun postProcessAlloc(bitmap: ProtectedBitmapInterface) {
        runBlocking {
            if (bitmap is AndroidProtectedBitmap) {
                bitmap.mutex().withLock {
                    allocBuffer(bitmap)
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

    override fun innerInterface(): ZilImageInterface {
        return image;
    }


}