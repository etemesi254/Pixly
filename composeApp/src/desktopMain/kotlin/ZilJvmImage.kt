import java.nio.ByteBuffer

class ZilJvmImage() : ZilImageInterface {

    private var jni = ZilImageJni()

    constructor(file: String) : this() {
        jni = ZilImageJni(file)
    }

    private constructor(jni: ZilImageJni) : this() {
        this.jni = jni
    }

    override fun clone(): ZilImageInterface {

        return ZilJvmImage(jni.clone());
    }

    override fun depth(): ZilDepth {
        return zilDepthFromNum(jni.depth.toUInt())
    }

    override fun width(): UInt {
        return jni.width().toUInt()
    }

    override fun height(): UInt {
        return jni.height().toUInt()
    }

    override fun colorspace(): ZilColorspace {
        return zilColorSpaceFromInt(jni.colorSpace.toUInt())
    }

    override fun convertColorspace(to: ZilColorspace) {
        jni.convertColorspace(to.toNum().toLong())
    }

    override fun convertDepth(to: ZilDepth) {
        jni.convertDepth(to.toNum().toLong())
    }

    override fun brightness(d: Float) {
        jni.brighten(d)
    }

    override fun bilateralFilter(d: Int, sigmaColor: Float, sigmaSpace: Float) {
        jni.bilateralFilter(d, sigmaColor, sigmaSpace)
    }

    override fun contrast(contrast: Float) {
        jni.contrast(contrast);
    }

    override fun crop(newWidth: UInt, newHeight: UInt, x: UInt, y: UInt) {
        jni.crop(newWidth.toLong(), newHeight.toLong(), x.toLong(), y.toLong())
    }

    override fun exposure(exposure: Float, blackPoint: Float) {
        jni.exposure(exposure, blackPoint)
    }

    override fun gamma(gamma: Float) {
        jni.gamma(gamma)
    }

    override fun save(file: String, format: ZilImageFormat) {
        jni.save(file, format.toNum().toLong())
    }

    override fun save(file: String) {
        jni.save(file)
    }


    override fun loadFile(file: String) {
        jni.loadNewFile(file)
    }

    override fun stretchContrast(lower: Float, higher: Float) {
        jni.stretchContrast(lower, higher)
    }

    override fun scharr() {
        jni.scharr()
    }

    override fun sobel() {
        jni.sobel()
    }

    override fun flip() {
        jni.flip()
    }

    override fun flop() {
        jni.flop()
    }

    override fun transpose() {
        kotlin.runCatching {
            jni.transpose()
        }
    }

    override fun verticalFlip() {
        jni.verticalFlip()
    }

    override fun histogram(): Map<String, LongArray> {
        return jni.histogram();
    }

    override fun exifMetadata(): Map<String, String> {
        return jni.exifMetadata()
    }

    override fun gaussianBlur(radius: Long) {
        jni.gaussianBlur(radius)
    }

    override fun boxBlur(radius: Long) {
        jni.boxBlur(radius)
    }


    override fun outputBufferSize(): Long {
        return jni.outBufferSize
    }

    override fun writeToBuffer(tempBuf: ByteBuffer, output: ByteArray) {
        jni.writeToBuffer(tempBuf, output)
    }

    override fun rotate90() {
        jni.rotate90()
    }

    override fun hslAdjust(hue: Float, saturation: Float, lightness: Float) {
        jni.hslAdjust(hue, saturation, lightness)
    }

    override fun medianBlur(radius: Long) {
        jni.medianBlur(radius)
    }

    override fun colorMatrix(floatArray: FloatArray) {
        jni.colorMatrix(array = floatArray)
    }
    override fun resize(newWidth: Long,newHeight: Long){
        jni.resize(newWidth, newHeight)

    }

}


fun ZilDepth.toNum(): UInt {
    return when (this) {
        ZilDepth.Unknown -> 0u
        ZilDepth.U8 -> 1u
        ZilDepth.U16 -> 2u
        ZilDepth.F32 -> 3u
    }
}

private fun zilDepthFromNum(value: UInt): ZilDepth {
    // nb should match rust definition, otherwise bad
    // things will happen
    return when (value) {
        0u -> ZilDepth.Unknown
        1u -> ZilDepth.U8
        2u -> ZilDepth.U16
        3u -> ZilDepth.F32
        else -> throw Exception("Unknown depth")

    }
}

fun ZilColorspace.toNum(): UInt {
    return when (this) {
        ZilColorspace.Unknown -> 0u
        ZilColorspace.RGB -> 1u
        ZilColorspace.RGBA -> 2u
        ZilColorspace.YCbCr -> 3u
        ZilColorspace.Luma -> 4u
        ZilColorspace.LumaA -> 5u
        ZilColorspace.YCCK -> 6u
        ZilColorspace.CMYK -> 7u
        ZilColorspace.BGR -> 8u
        ZilColorspace.BGRA -> 9u
        ZilColorspace.ARGB -> 10u
    }
}

fun zilColorSpaceFromInt(int: UInt): ZilColorspace {
    return when (int) {
        0u -> ZilColorspace.Unknown
        1u -> ZilColorspace.RGB
        2u -> ZilColorspace.RGBA
        3u -> ZilColorspace.YCbCr
        4u -> ZilColorspace.Luma
        5u -> ZilColorspace.LumaA
        6u -> ZilColorspace.YCCK
        7u -> ZilColorspace.CMYK
        8u -> ZilColorspace.BGR
        9u -> ZilColorspace.BGRA
        10u -> ZilColorspace.ARGB
        else -> ZilColorspace.Unknown

    }
}

fun ZilImageFormat.toNum(): UInt {
    return when (this) {
        ZilImageFormat.UnknownFormat -> 0u
        ZilImageFormat.JPEG -> 1u
        ZilImageFormat.PNG -> 2u
        ZilImageFormat.PPM -> 3u
        ZilImageFormat.PSD -> 4u
        ZilImageFormat.Farbfeld -> 5u
        ZilImageFormat.QOI -> 6u
        ZilImageFormat.JPEG_XL -> 7u
        ZilImageFormat.HDR -> 8u
        ZilImageFormat.BMP -> 9u

    }
}