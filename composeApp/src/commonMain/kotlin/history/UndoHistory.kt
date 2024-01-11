package history

import AppContext
import androidx.compose.runtime.mutableStateOf
import imageops.imageHorizontalFlip
import imageops.imageTranspose
import imageops.imageVerticalFlip

suspend fun AppContext.undoSingleHistory() {
    val ctx = currentImageContext();
    if (ctx != null) {

        val history = ctx.history.getHistory()
        if (history.isNotEmpty()) {
            // we confirmed we have something
            val lastOperation = history.last()

            if (lastOperation.trivialUndo()) {
                // simple to undo, so nothing was added on stack, we just need to repeat what it was
                // e.g. something like transposition is a bijection since repeating it undoes the
                // effect of the first
                undoSimpleBijectionOperation(lastOperation)
                // remove it from history
                ctx.history.pop()
            } else {
                // remove it from history
                ctx.images = ctx.images.dropLast(1).toMutableList()
                ctx.history.pop()
                if (lastOperation.requiresValue()) {
                    restorePreviousValue(lastOperation)
                }
                ctx.imageToDisplay().writeToCanvas(ctx.canvasBitmaps[ImageContextBitmaps.CurrentCanvasImage]!!)

                ctx.imageModified.value = !ctx.imageModified.value
                broadcastImageChange()

                // now write that to canvas


            }
            // remove it from
        }
    }
}

@Suppress("UNCHECKED_CAST")
private suspend fun AppContext.restorePreviousValue(historyOperationsEnum: HistoryOperationsEnum) {

    // find previous
    val ctx = currentImageContext()!!;
    val history = ctx.history.getHistory();
    val prev = history.indexOfLast { it == historyOperationsEnum }
    val syms = ctx.history.getValue();
    val convertToFloat = {
        val value: Float = if (prev == -1) {
            0F
        } else {
            (syms[prev] as? Float) ?: 0F
        }
        value
    }
    val convertToLong = {
        val value: Long = if (prev == -1) {
            0L
        } else {
            (syms[prev] as? Long) ?: 0L
        }
        value
    }
    val convertToClosedFloatingPointRange = {
        val value: ClosedFloatingPointRange<Float> = if (prev == -1) {
            0F..256F
        } else {
            (syms[prev] as? ClosedFloatingPointRange<Float>) ?: 0F..256F
        }
        value
    }


    when (historyOperationsEnum) {
        HistoryOperationsEnum.Brighten -> {

            ctx.filterValues.brightness = convertToFloat()
        }

        HistoryOperationsEnum.Contrast -> {

            ctx.filterValues.contrast = convertToFloat()
        }

        HistoryOperationsEnum.Exposure -> {

            ctx.filterValues.exposure = convertToFloat()
        }

        HistoryOperationsEnum.BoxBlur -> {
            ctx.filterValues.boxBlur = convertToLong()
        }

        HistoryOperationsEnum.GaussianBlur -> {

            ctx.filterValues.gaussianBlur = convertToLong()
        }

        HistoryOperationsEnum.BilateralBlur -> {

            ctx.filterValues.bilateralBlur = convertToLong()
        }

        HistoryOperationsEnum.MedianBlur -> {

            ctx.filterValues.medianBlur = convertToLong()
        }

        HistoryOperationsEnum.Levels -> {
            ctx.filterValues.stretchContrastRange = mutableStateOf(convertToClosedFloatingPointRange())
        }

        HistoryOperationsEnum.Hue -> {
            val value: List<Float> = if (prev == -1) {
                listOf(0f, 1f, 1f)
            } else {
                (syms[prev] as? List<Float>) ?: listOf(0f, 1f, 1f)
            }
            ctx.filterValues.hue = value.getOrNull(0) ?: 0F
            ctx.filterValues.saturation = value.getOrNull(1) ?: 1F
            ctx.filterValues.lightness = value.getOrNull(2) ?: 1F

        }

        else -> {/* TODO */
        }
    }
}

private suspend fun AppContext.undoSimpleBijectionOperation(historyOperationsEnum: HistoryOperationsEnum) {
    when (historyOperationsEnum) {
        HistoryOperationsEnum.VerticalFlip -> imageVerticalFlip(addToHistory = false)
        HistoryOperationsEnum.HorizontalFlip -> imageHorizontalFlip(false)
        HistoryOperationsEnum.Transposition -> imageTranspose(false)
        else -> throw Exception("History operation not a bijection cannot trivially undo it")
    }
}