package image.desktop

import java.lang.ref.Cleaner
import java.net.URL
import java.nio.ByteBuffer
import java.nio.file.Paths

internal class ZilImageJni : Cleaner.Cleanable {
    constructor(file: String) {
        imagePtr = createImagePtrNative()
        loadImageNative(imagePtr, file)
    }

    constructor() {
        imagePtr = createImagePtrNative()
    }

    private constructor(imagePtr: Long) {
        this.imagePtr = imagePtr
    }

    private var imagePtr: Long = 0


    private external fun createImagePtrNative(): Long

    private external fun destroyImagePtrNative(imagePtr: Long)

    private external fun loadImageNative(imagePtr: Long, fileName: String)

    private external fun cloneNative(imagePtr: Long): Long

    private external fun getImageWidthNative(imagePtr: Long): Long

    private external fun getImageHeightNative(imagePtr: Long): Long

    private external fun getDepthNative(imagePtr: Long): Long

    private external fun getColorSpaceNative(imagePtr: Long): Long

    private external fun saveNative(imagePtr: Long, filename: String)

    private external fun saveToNative(imagePtr: Long, filename: String, imageFormat: Long)

    private external fun contrastNative(imagePtr: Long, value: Float)

    private external fun cropNative(imagePtr: Long, newWidth: Long, newHeight: Long, x: Long, y: Long)

    private external fun exposureNative(imagePtr: Long, exposure: Float, blackPoint: Float)

    private external fun gammaNative(imageptr: Long, gamma: Float)

    private external fun bilateralFilterNative(imagePtr: Long, d: Int, sigmaSpace: Float, sigmaColor: Float)

    private external fun allocByteMemoryNative(nativeMemoryLength: Long): Long

    private external fun resizeByteMemoryNative(nativeMemoryPtr: Long, size: Long): Long

    private external fun freeByteMemoryNative(nativeMemoryPtr: Long)

    private external fun getImageOutBufferSizeNative(nativeMemoryPtr: Long): Long

    private external fun writeToBufferNative(imagePtr: Long, nativeMemoryPtr: Long, length: Long, array: ByteArray)

    private external fun convertColorSpaceNative(imagePtr: Long, toColorspace: Long)

    private external fun convertDepthNative(imagePtr: Long, toDepth: Long)

    private external fun brightenNative(imagePtr: Long, by: Float)

    private external fun sobelNative(imagePtr: Long)

    private external fun scharrNative(imagePtr: Long)

    private external fun stretchContrastNative(imagePtr: Long, lower: Float, higher: Float)

    private external fun flipNative(imagePtr: Long)

    private external fun verticalFlipNative(imagePtr: Long)

    private external fun flopNative(imagePtr: Long)

    private external fun transposeNative(imagePtr: Long)

    private external fun histogramNative(imagePtr: Long, arrays: Map<String, LongArray>)

    private external fun exifMetadataNative(imagePtr: Long, metadata: Map<String, String>)

    private external fun boxBlurNative(imagePtr: Long, radius: Long)

    private external fun gaussianBlurNative(imagePtr: Long, radius: Long)

    private external fun hslAdjustNative(imagePtr: Long, hue: Float, saturation: Float, lightness: Float)

    private external fun medianBlurNative(imagePtr: Long, radius: Long)

    //private native void rotate90Native(long imagePtr);
    /**
     * Write to native buffer allocated via bytebuffer direct
     */
    private external fun writeToNioBufferNative(imagePtr: Long, buffer: ByteBuffer)


    @Throws(Exception::class)
    fun width(): Long {
        if (imagePtr == 0L) {
            throw Exception("Image ptr is zero, have you loaded an image?")
        }
        return this.getImageWidthNative(imagePtr)
    }

    @Throws(Exception::class)
    fun height(): Long {
        if (imagePtr == 0L) {
            throw Exception("Image ptr is zero, have you loaded an image?")
        }
        return this.getImageHeightNative(imagePtr)
    }

    @Throws(Exception::class)
    fun save(filename: String) {
        if (imagePtr == 0L) {
            throw Exception("Image ptr is zero, have you loaded an image?")
        }
        this.saveNative(imagePtr, filename)
    }

    fun contrast(contrast: Float) {
        this.contrastNative(imagePtr, contrast)
    }

    fun exposure(exposure: Float, blackPoint: Float) {
        this.exposureNative(imagePtr, exposure, blackPoint)
    }

    fun crop(newWidth: Long, newHeight: Long, x: Long, y: Long) {
        this.cropNative(imagePtr, newWidth, newHeight, x, y)
    }

    fun bilateralFilter(d: Int, sigmaSpace: Float, sigmaColor: Float) {
        this.bilateralFilterNative(imagePtr, d, sigmaSpace, sigmaColor)
    }

    fun gamma(gamma: Float) {
        this.gammaNative(imagePtr, gamma)
    }

    override fun clean() {
        this.destroyImagePtrNative(imagePtr)
    }

    val outBufferSize: Long
        get() = this.getImageOutBufferSizeNative(imagePtr)


    val depth: Long
        get() = getDepthNative(imagePtr)

    val colorSpace: Long
        get() = getColorSpaceNative(imagePtr)

    fun save(filename: String, format: Long) {
        saveToNative(imagePtr, filename, format)
    }

    fun convertColorspace(to: Long) {
        convertColorSpaceNative(imagePtr, to)
    }

    fun convertDepth(to: Long) {
        convertDepthNative(imagePtr, to)
    }

    fun loadNewFile(file: String) {
        loadImageNative(imagePtr, file)
    }

    fun clone(): ZilImageJni {
        val newPtr = cloneNative(imagePtr)
        return ZilImageJni(newPtr)
    }

    fun brighten(by: Float) {
        brightenNative(imagePtr, by)
    }

    fun sobel() {
        sobelNative(imagePtr)
    }

    fun scharr() {
        scharrNative(imagePtr)
    }

    fun stretchContrast(lower: Float, higher: Float) {
        stretchContrastNative(imagePtr, lower, higher)
    }

    fun flip() {
        flipNative(imagePtr)
    }

    fun flop() {
        flopNative(imagePtr)
    }

    fun transpose() {
        transposeNative(imagePtr)
    }

    fun verticalFlip() {
        verticalFlipNative(imagePtr)
    }

    fun histogram(): Map<String, LongArray> {
        val map: Map<String, LongArray> = HashMap()
        histogramNative(imagePtr, map)
        return map
    }

    fun exifMetadata(): Map<String, String> {
        val map = HashMap<String, String>()
        exifMetadataNative(imagePtr, map)
        return map
    }

    fun gaussianBlur(radius: Long) {
        gaussianBlurNative(imagePtr, radius)
    }

    fun boxBlur(radius: Long) {
        boxBlurNative(imagePtr, radius)
    }

    @Throws(Exception::class)
    fun writeToBuffer(buf: ByteBuffer, output: ByteArray) {
        if (!buf.isDirect) {
            throw Exception("Native buffer should be direct")
        }
        if (buf.capacity() < outBufferSize) {
            throw Exception("The buffer capacity will not fit the array")
        }
        if (outBufferSize > output.size) {
            throw Exception("The output size will not fit the array")
        }

        writeToNioBufferNative(imagePtr, buf)
        // transfer that now to an output
        // the bytebuffer isn't backed by an array, so we can't peek into it
        // we just write the output to an array understood by java/jvm
        buf[0, output]
    }

    fun rotate90() {
        //rotate90Native(imagePtr);
    }

    fun hslAdjust(hue: Float, saturation: Float, lightness: Float) {
        hslAdjustNative(imagePtr, hue, saturation, lightness)
    }

    fun medianBlur(radius: Long) {
        medianBlurNative(imagePtr, radius)
    }


    companion object {
        init {
            val resource = Thread.currentThread().contextClassLoader!!;

            val path = resource.getResource("shared_libs/linux/libzune_jni_bindings.so")!!.path;

//            println(path)
//            val new_path = path.removeSuffix(".so");
            System.load(path)
        }
    }
}