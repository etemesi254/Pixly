import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.loadImageBitmap
import org.jetbrains.skia.*
import org.jetbrains.skia.Canvas

class ZilImageAndBitmapInterop(file: String) {
    public var inner: ZilImage = ZilImage(file);

    private var canvasBitmap = Bitmap();

    init {
        // first convert it to RGBA
        inner.convertColorspace(ZilColorspace.RGBA)
        // then convert it to ARGB
        inner.convertColorspace(ZilColorspace.ARGB)

        // set up canvas
        allocBuffer()
        installPixels()
    }

    private fun allocBuffer() {
        // TODO change color type to be premul once its exposed from native
        val info = ImageInfo.makeS32(inner.width().toInt(), inner.height().toInt(), ColorAlphaType.UNPREMUL);
        assert(canvasBitmap.allocPixels(info))
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun installPixels() {
        val buffer = inner.toBuffer();
        assert(canvasBitmap.installPixels(buffer.asByteArray()))
    }

    fun canvas(): ImageBitmap = canvasBitmap.asComposeImageBitmap()
}
