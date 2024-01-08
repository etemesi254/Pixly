import image.toNum
import image.zilColorSpaceFromInt
import image.zilDepthFromNum
import java.lang.ref.Cleaner
import java.nio.ByteBuffer

internal class ZilImageJni : Cleaner.Cleanable, ZilImageInterface {
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


    private external fun getImageOutBufferSizeNative(nativeMemoryPtr: Long): Long

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

    private external fun colorMatrixNative(imagePtr: Long, matrix: FloatArray)

    private external fun resizeImageNative(imagePtr: Long, newWidth: Long, newHeight: Long)

    private external fun rotateNative(imagePtr: Long, angle: Float)

    /**
     * Write to native buffer allocated via bytebuffer direct
     */
    private external fun writeToNioBufferNative(imagePtr: Long, buffer: ByteBuffer)


    @Throws(Exception::class)
    override fun width(): UInt {
        if (imagePtr == 0L) {
            throw Exception("Image ptr is zero, have you loaded an image?")
        }
        return this.getImageWidthNative(imagePtr).toUInt()
    }

    @Throws(Exception::class)
    override fun height(): UInt {
        if (imagePtr == 0L) {
            throw Exception("Image ptr is zero, have you loaded an image?")
        }
        return this.getImageHeightNative(imagePtr).toUInt()
    }

    override fun colorspace(): ZilColorspace = colorSpace


    @Throws(Exception::class)
    override fun save(filename: String) {
        if (imagePtr == 0L) {
            throw Exception("Image ptr is zero, have you loaded an image?")
        }
        this.saveNative(imagePtr, filename)
    }


    override fun contrast(contrast: Float) {
        this.contrastNative(imagePtr, contrast)
    }

    override fun exposure(exposure: Float, blackPoint: Float) {
        this.exposureNative(imagePtr, exposure, blackPoint)
    }

    override fun crop(newWidth: UInt, newHeight: UInt, x: UInt, y: UInt) {
        this.cropNative(imagePtr, newWidth.toLong(), newHeight.toLong(), x.toLong(), y.toLong())
    }

    override fun bilateralFilter(d: Int, sigmaSpace: Float, sigmaColor: Float) {
        this.bilateralFilterNative(imagePtr, d, sigmaSpace, sigmaColor)
    }

    override fun gamma(gamma: Float) {
        this.gammaNative(imagePtr, gamma)
    }


    override fun clean() {
        this.destroyImagePtrNative(imagePtr)
    }

    private val outBufferSize: Long
        get() = this.getImageOutBufferSizeNative(imagePtr)


    private val depth: ZilDepth
        get() = zilDepthFromNum(getDepthNative(imagePtr).toUInt())

    private val colorSpace: ZilColorspace
        get() = zilColorSpaceFromInt(getColorSpaceNative(imagePtr).toUInt())


    override fun save(filename: String, format: ZilImageFormat) {
        saveToNative(imagePtr, filename, format.toNum().toLong())
    }

    override fun convertColorspace(to: ZilColorspace) {
        convertColorSpaceNative(imagePtr, to.toNum().toLong())
    }

    override fun convertDepth(to: ZilDepth) {
        convertDepthNative(imagePtr, to.toNum().toLong())
    }

    override fun loadFile(file: String) {
        loadImageNative(imagePtr, file)
    }

    override fun clone(): ZilImageJni {
        val newPtr = cloneNative(imagePtr)
        return ZilImageJni(newPtr)
    }

    override fun depth(): ZilDepth = depth

    override fun brightness(by: Float) {
        brightenNative(imagePtr, by)
    }

    override fun sobel() {
        sobelNative(imagePtr)
    }

    override fun scharr() {
        scharrNative(imagePtr)
    }

    override fun stretchContrast(lower: Float, higher: Float) {
        stretchContrastNative(imagePtr, lower, higher)
    }

    override fun flip() {
        flipNative(imagePtr)
    }

    override fun flop() {
        flopNative(imagePtr)
    }

    override fun transpose() {
        transposeNative(imagePtr)
    }

    override fun verticalFlip() {
        verticalFlipNative(imagePtr)
    }

    override fun histogram(): Map<String, LongArray> {
        val map: Map<String, LongArray> = HashMap()
        histogramNative(imagePtr, map)
        return map
    }

    override fun exifMetadata(): Map<String, String> {
        val map = HashMap<String, String>()
        exifMetadataNative(imagePtr, map)
        return map
    }

    override fun gaussianBlur(radius: Long) {
        gaussianBlurNative(imagePtr, radius)
    }

    override fun boxBlur(radius: Long) {
        boxBlurNative(imagePtr, radius)
    }

    override fun outputBufferSize(): Long = outBufferSize


    @Throws(Exception::class)
    override fun writeToBuffer(buf: ByteBuffer, output: ByteArray, writeToOutput: Boolean) {
        if (!buf.isDirect) {
            throw Exception("Native buffer should be direct")
        }
        if (buf.capacity() < outBufferSize) {
            throw Exception("The buffer capacity will not fit the array")
        }
        if (outBufferSize > output.size && writeToOutput) {
            throw Exception("The output size will not fit the array")
        }

        writeToNioBufferNative(imagePtr, buf)
        // transfer that now to an output
        // the bytebuffer isn't backed by an array, so we can't peek into it
        // we just write the output to an array understood by java/jvm
        if (writeToOutput) {
            buf.rewind()
            buf.get(output, 0, outBufferSize.toInt())
        }
        //buf[0, output]
    }

    override fun rotate(angle: Float) {
        rotateNative(imagePtr, angle);
    }

    override fun hslAdjust(hue: Float, saturation: Float, lightness: Float) {
        hslAdjustNative(imagePtr, hue, saturation, lightness)
    }

    override fun medianBlur(radius: Long) {
        medianBlurNative(imagePtr, radius)
    }

    override fun colorMatrix(array: FloatArray) {
        if (array.size != 20) {
            throw Exception("Array size should be 20")
        }
        colorMatrixNative(imagePtr, array)
    }

    override fun resize(newWidth: Long, newHeight: Long) {
        resizeImageNative(imagePtr, newWidth, newHeight)
    }

    companion object {
        init {
            System.loadLibrary("zune_jni_bindings")
        }
    }
}