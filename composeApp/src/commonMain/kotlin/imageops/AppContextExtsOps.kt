package imageops

import AppContext
import history.HistoryOperationsEnum
import history.HistoryResponse
import kotlinx.coroutines.sync.withLock
import java.text.DecimalFormat
import kotlin.math.absoluteValue

/**
 * Contains image operations implemented as extension functions for AppContext
 *
 * The reason they are extensions on AppContext is to allow things like history management
 * with an implicit this and things like broadcast stuff that are not specific to an image
 * but the whole app
 * */


const val EPSILON = 0.003F;

/**
 * Apply an image brighten operation with the specified
 * value
 *
 * @param value: A float value expected in the range -100 to 100
 * */
suspend fun AppContext.imageBrighten(value: Float) {
    val ctx = currentImageContext()
    ctx?.operationsMutex?.withLock {
        val prevValue = imageFilterValues()?.brightness!!;
        val delta = value - prevValue;

        if (delta.absoluteValue > EPSILON) {

            initializeImageChange()
            val resp = appendToHistory(HistoryOperationsEnum.Brighten, value.toInt())

            val image = ctx.currentImage(resp)

            // brightness expects a value between -1 and 1, so scale it here
            image.brighten(delta / 100, ctx.canvasBitmaps[ImageContextBitmaps.CurrentCanvasImage]!!)
            ctx.filterValues.brightness = value
            broadcastImageChange()
        }
    }
}

/**
 * Apply image contrast to an image
 *
 * @param value Value of contrast, integer between -100 and 100
 * */
suspend fun AppContext.imageContrast(value: Float) {
    val ctx = currentImageContext()
    ctx?.operationsMutex?.withLock {
        val prevValue = imageFilterValues()?.contrast!!;
        val delta = value - prevValue;


        if (delta.absoluteValue > EPSILON) {
            initializeImageChange()
            val resp = appendToHistory(HistoryOperationsEnum.Contrast, value.toLong().toFloat())
            val image = ctx.currentImage(resp)
            image.contrast(delta, ctx.canvasBitmaps[ImageContextBitmaps.CurrentCanvasImage]!!)
            ctx.filterValues.contrast = value
            broadcastImageChange()

        }
    }
}

/**
 * Change the image exposure
 *
 * @param value: New exposure value
 *
 * */
suspend fun AppContext.imageExposure(value: Float) {
    val ctx = currentImageContext()
    ctx?.operationsMutex?.withLock {
        val prevValue = imageFilterValues()?.contrast!!;
        val delta = value - prevValue;

        if (delta.absoluteValue > EPSILON) {
            initializeImageChange()
            val resp = appendToHistory(HistoryOperationsEnum.Exposure, value)
            val image = ctx.currentImage(resp)

            image.exposure(value + 1.0F, blackPoint = 0F, ctx.canvasBitmaps[ImageContextBitmaps.CurrentCanvasImage]!!)
            ctx.filterValues.exposure = value
            broadcastImageChange()

        }
    }
}

suspend fun AppContext.imageStretchContrast(value: ClosedFloatingPointRange<Float>) {
    val ctx = currentImageContext()
    ctx?.operationsMutex?.withLock {
        initializeImageChange()
        val resp = appendToHistory(HistoryOperationsEnum.Levels, value)
        val image = ctx.currentImage(resp)
        image.stretchContrast(value,ctx.canvasBitmaps[ImageContextBitmaps.CurrentCanvasImage]!!)
        ctx.filterValues.stretchContrastRange.value = value
        broadcastImageChange()
    }
}

suspend fun AppContext.imageGaussianBlur(radius: Long) {
    val ctx = currentImageContext()
    ctx?.operationsMutex?.withLock {
        initializeImageChange()
        val resp = appendToHistory(HistoryOperationsEnum.GaussianBlur, radius)
        val image = ctx.currentImage(resp)
        image.gaussianBlur(radius, ctx.canvasBitmaps[ImageContextBitmaps.CurrentCanvasImage]!!)

        ctx.filterValues.gaussianBlur = radius
        broadcastImageChange()
    }
}

suspend fun AppContext.imageMedianBlur(radius: Long) {
    val ctx = currentImageContext()
    ctx?.operationsMutex?.withLock {
        initializeImageChange()
        val resp = appendToHistory(HistoryOperationsEnum.MedianBlur, radius)
        val image = ctx.currentImage(resp)
        image.medianBlur(radius, ctx.canvasBitmaps[ImageContextBitmaps.CurrentCanvasImage]!!)
        ctx.filterValues.medianBlur = radius
        broadcastImageChange()
    }
}

suspend fun AppContext.imageBilateralBlur(radius: Long) {
    val ctx = currentImageContext()
    ctx?.operationsMutex?.withLock {
        initializeImageChange()
        val resp = appendToHistory(HistoryOperationsEnum.BilateralBlur, radius)
        val image = ctx.currentImage(resp)
        image.bilateralBlur(radius, ctx.canvasBitmaps[ImageContextBitmaps.CurrentCanvasImage]!!)
        ctx.filterValues.bilateralBlur = radius
        broadcastImageChange()
    }
}

suspend fun AppContext.imageBoxBlur(radius: Long) {
    val ctx = currentImageContext()
    ctx?.operationsMutex?.withLock {
        initializeImageChange()
        val resp = appendToHistory(HistoryOperationsEnum.BoxBlur, radius)
        val image = ctx.currentImage(resp)
        image.boxBlur(radius, ctx.canvasBitmaps[ImageContextBitmaps.CurrentCanvasImage]!!)
        ctx.filterValues.boxBlur = radius
        broadcastImageChange()
    }
}

suspend fun AppContext.imageRotate180(_radius: Long) {
    val ctx = currentImageContext()
    ctx?.operationsMutex?.withLock {
        initializeImageChange()
        val resp = appendToHistory(HistoryOperationsEnum.Rotate180)
        val image = ctx.currentImage(resp)
        image.flip(ctx.canvasBitmaps[ImageContextBitmaps.CurrentCanvasImage]!!)
        broadcastImageChange()
    }
}

suspend fun AppContext.imageVerticalFlip(addToHistory: Boolean = true) {
    val ctx = currentImageContext()
    ctx?.operationsMutex?.withLock {
        initializeImageChange()
        val resp = if (addToHistory) {
            appendToHistory(HistoryOperationsEnum.VerticalFlip)
        } else {
            HistoryResponse.DummyOperation
        }
        val image = ctx.currentImage(resp)
        image.verticalFlip(ctx.canvasBitmaps[ImageContextBitmaps.CurrentCanvasImage]!!)
        broadcastImageChange()
    }
}

suspend fun AppContext.imageHorizontalFlip(addToHistory: Boolean = true) {
    val ctx = currentImageContext()
    ctx?.operationsMutex?.withLock {
        initializeImageChange()
        val resp = if (addToHistory) {
            appendToHistory(HistoryOperationsEnum.HorizontalFlip)
        } else {
            HistoryResponse.DummyOperation
        }
        val image = ctx.currentImage(resp)
        image.horizontalFlip(ctx.canvasBitmaps[ImageContextBitmaps.CurrentCanvasImage]!!)
        broadcastImageChange()
    }
}

suspend fun AppContext.imageTranspose(addToHistory: Boolean = true) {
    val ctx = currentImageContext()
    ctx?.operationsMutex?.withLock {
        initializeImageChange()
        val resp = if (addToHistory) {
            appendToHistory(HistoryOperationsEnum.Transposition)
        } else {
            HistoryResponse.DummyOperation
        }
        val image = ctx.currentImage(resp)
        image.transpose(ctx.canvasBitmaps[ImageContextBitmaps.CurrentCanvasImage]!!)
        broadcastImageChange()
    }
}

suspend fun AppContext.imageHslAdjust(hue: Float? = null, saturation: Float? = null, lightness: Float? = null) {
    val ctx = currentImageContext()

    ctx?.operationsMutex?.withLock {
        initializeImageChange()
        val hueNonNull = hue ?: ctx.filterValues.hue
        val saturationNonNull = saturation ?: ctx.filterValues.saturation
        val lightnessNonNull = lightness ?: ctx.filterValues.lightness
        val h = hueNonNull - ctx.filterValues.hue;
        val s = (saturationNonNull - ctx.filterValues.saturation) + 1
        val l = (lightnessNonNull - ctx.filterValues.lightness) + 1
        val resp = appendToHistory(HistoryOperationsEnum.Hue, listOf(hueNonNull, saturationNonNull, lightnessNonNull))
        val image = ctx.currentImage(resp)
        image.hslAdjust(h, s, l,ctx.canvasBitmaps[ImageContextBitmaps.CurrentCanvasImage]!!)

        ctx.filterValues.hue = hueNonNull
        ctx.filterValues.saturation = saturationNonNull
        ctx.filterValues.lightness = lightnessNonNull

        broadcastImageChange()


    }
}