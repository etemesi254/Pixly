interface ZilBitmapInterface {
    fun writeToCanvas(bitmap: ProtectedBitmapInterface)
    fun prepareNewFile(bitmap: ProtectedBitmapInterface)
    fun clone(): ZilBitmapInterface
    fun postProcessAlloc(bitmap: ProtectedBitmapInterface)


    fun hslAdjust(hue: Float, saturation: Float, lightness: Float, bitmap: ProtectedBitmapInterface){
        innerInterface().hslAdjust(hue,saturation,lightness)
        postProcessAlloc(bitmap)
    }

    fun contrast(delta: Float, bitmap: ProtectedBitmapInterface) {
        innerInterface().contrast(delta);
        postProcessAlloc(bitmap)
    }

    fun exposure(value: Float, blackPoint: Float, bitmap: ProtectedBitmapInterface) {
        innerInterface().exposure(value, blackPoint)
        postProcessAlloc(bitmap)
    }

    fun stretchContrast(value: ClosedFloatingPointRange<Float>, bitmap: ProtectedBitmapInterface) {
        innerInterface().stretchContrast(value.start, value.endInclusive)
        postProcessAlloc(bitmap)
    }

    fun gaussianBlur(radius: Long, bitmap: ProtectedBitmapInterface) {
        innerInterface().gaussianBlur(radius)
        postProcessAlloc(bitmap)
    }

    fun medianBlur(radius: Long, bitmap: ProtectedBitmapInterface) {
        innerInterface().medianBlur(radius)
        postProcessAlloc(bitmap)
    }

    fun bilateralBlur(radius: Long, bitmap: ProtectedBitmapInterface) {
        innerInterface().bilateralFilter(radius.toInt(), radius.toFloat(), radius.toFloat())
        postProcessAlloc(bitmap)
    }

    fun boxBlur(radius: Long, bitmap: ProtectedBitmapInterface) {
        innerInterface().boxBlur(radius)
        postProcessAlloc(bitmap)
    }

    fun flip(bitmap: ProtectedBitmapInterface) {
        innerInterface().flip()
        postProcessAlloc(bitmap)
    }

    fun verticalFlip(bitmap: ProtectedBitmapInterface) {
        innerInterface().verticalFlip()
        postProcessAlloc(bitmap)
    }

    fun horizontalFlip(bitmap: ProtectedBitmapInterface) {
        innerInterface().flop()
        postProcessAlloc(bitmap)
    }

    fun transpose(bitmap: ProtectedBitmapInterface) {
        innerInterface().transpose()
        postProcessAlloc(bitmap)
    }

    fun brighten(delta: Float, bitmap: ProtectedBitmapInterface) {
        innerInterface().brightness(delta)
        postProcessAlloc(bitmap)
    }

    fun colorMatrix(matrix: FloatArray, bitmap: ProtectedBitmapInterface) {
        innerInterface().colorMatrix(matrix)
        postProcessAlloc(bitmap)
    }

    fun width(): UInt {
        return innerInterface().width()
    }

    fun height(): UInt {
        return innerInterface().height()
    }

    fun innerInterface(): ZilImageInterface

    fun save(name: String, format: ZilImageFormat) {
        innerInterface().save(name, format)
    }

    fun rotate(angle: Float, bitmap: ProtectedBitmapInterface) {
        innerInterface().rotate(angle)
        postProcessAlloc(bitmap)
    }

    fun sobel(bitmap: ProtectedBitmapInterface) {
        innerInterface().sobel()
        postProcessAlloc(bitmap)
    }

}