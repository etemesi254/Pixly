interface ZilBitmapInterface {
    fun writeToCanvas(bitmap: ProtectedBitmapInterface)
    fun prepareNewFile(bitmap: ProtectedBitmapInterface)

    fun clone(): ZilBitmapInterface

    fun hslAdjust(hue: Float, saturation: Float, lightness: Float, bitmap: ProtectedBitmapInterface)

    fun contrast(delta: Float, bitmap: ProtectedBitmapInterface)

    fun exposure(value: Float, blackPoint: Float, bitmap: ProtectedBitmapInterface)

    fun stretchContrast(value: ClosedFloatingPointRange<Float>, bitmap: ProtectedBitmapInterface)
    fun gaussianBlur(radius: Long, bitmap: ProtectedBitmapInterface)

    fun medianBlur(radius: Long, bitmap: ProtectedBitmapInterface)

    fun bilateralBlur(radius: Long, bitmap: ProtectedBitmapInterface)

    fun boxBlur(radius: Long, bitmap: ProtectedBitmapInterface)

    fun flip(bitmap: ProtectedBitmapInterface)

    fun verticalFlip(bitmap: ProtectedBitmapInterface)

    fun horizontalFlip(bitmap: ProtectedBitmapInterface)

    fun transpose(bitmap: ProtectedBitmapInterface)

    fun brighten(delta: Float, bitmap: ProtectedBitmapInterface)

    fun colorMatrix(matrix: FloatArray, bitmap: ProtectedBitmapInterface)

    fun width(): UInt

    fun height(): UInt

    fun innerInterface(): ZilImageInterface

    fun save(name: String, format: ZilImageFormat)

    fun rotate(angle:Float,bitmap: ProtectedBitmapInterface)


}