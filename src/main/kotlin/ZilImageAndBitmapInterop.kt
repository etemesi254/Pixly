import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.zIndex
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.skia.*

class ZilImageAndBitmapInterop() {
    var inner: ZilImage = ZilImage();

    // it may happen that this may be called from multiple coroutines
    // so we keep it single threaded since we do a lot of memory sharing
    private val mutex = Mutex();
    private var canvasBitmap = Bitmap();
    private var info: ImageInfo = ImageInfo.makeUnknown(0, 0);
    private var file: String = "";
    // a boolean to see if we have modified stuff
    private var isModified  by   mutableStateOf(true)


    constructor(file: String) : this() {

        this.file = file
        inner = ZilImage(file)
        prepareNewFile()
    }

    private fun prepareNewFile() {
        // convert depth to u8
        inner.convertDepth(ZilDepth.U8);

        // first convert it to RGBA
        inner.convertColorspace(ZilColorspace.RGBA)
        /*
        * BUGS BUGS BUGS
        *
        * Okay so you see how we have big endian and little endian systems
        *
        * Skia works with little endian,
        * it tells you to give it bytes, and then it renders them on screen
        * this kinda matters, because skia uses ARGB8888, which now  says
        * components are laid out in A, R, G finally B.
        *
        * BUT skia stores colors in 32-bit integer, which makes sense, so you'd assume that
        * A -> 24-32
        * R -> 24-16
        * G -> 16-08
        * B -> 08-00
        *
        * Since that's how you'd lay them in memory.
        *
        * But it's actually
        *
        * A -> 00-08
        * R -> 08-16
        * G -> 16-24
        * B -> 24-32
        *
        * Since the 32 bit int is laid out in little endian, LSB is first
        *
        * zune doesn't know this because it handles individual bytes, so we have
        * to handle it here.
        *
        * The solution is easy, once you see it, BGRA(Little Endian) == ARGB , because you literally
        * read the letters in the opposite way, so we have to do another conversion to BGRA so that
        * skia can see it in ARGB
        *
        * computers :)
        * */
        inner.convertColorspace(ZilColorspace.BGRA)
        // set up canvas
        allocBuffer()
        installPixels()

    }


    private fun allocBuffer() {
        // check if buffer would fit the new type
        // i.e do not pre-allocate
        val infoSize = info.height*info.width
        val imageSize = inner.height().toInt()*inner.width().toInt();
        info = ImageInfo.makeN32(inner.width().toInt(), inner.height().toInt(), ColorAlphaType.UNPREMUL);

        canvasBitmap.setImageInfo(info)

        if (infoSize!= imageSize) {
            // TODO change color type to be pre-multiplied once its exposed from native
            assert(canvasBitmap.allocPixels(info))
        }
        // ensure we can store it
        assert(canvasBitmap.computeByteSize() == info.computeByteSize(info.minRowBytes))
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun installPixels() {
        val buffer = inner.toBuffer();
        assert(canvasBitmap.installPixels(buffer.asByteArray()))
    }

    @Composable
    fun image() {
        /* Let's talk about computers and invalidations
        *
        * Say you have an image that takes 10 seconds to decode, so you alloc and decode
        *  remember that is happening in a different coroutine, so it may happen that
        * you click and open a new image which takes 2 seconds, so that coroutine finishes and renders
        * dope
        *
        * but what about this old one??? They share the same buffer and bitmap.
        *
        * So when the image renderer tries to render, nullptr :)
        *
        *
        * To solve this, we can just share locks between the renderer and the loader, so that
        * only one thing runs at a time
        */
        key(isModified) {
            if (mutex.tryLock()) {
                // unlock when composition is done
                SideEffect {
                    mutex.unlock()
                }
                /*
                * Let's talk about getting compose to redraw.
                *
                * So we need it to redraw the image for real time effects viewing
                *
                * but we don't have a way to tell compose, hey, redraw me
                * So we make it think that the modifier has been modified, forcing a redraw
                *
                *
                * All image filters modify the isModified parameter, which acts as a signal
                * telling compose to redraw the image
                *
                * */
                Image(bitmap = canvas(), contentDescription = null, modifier = Modifier.zIndex(if (isModified) {0F} else {0.0f}))
            }
        }

    }
    private fun canvas(): ImageBitmap {
        return canvasBitmap.asComposeImageBitmap()
    }


    suspend fun loadFile(file: String) {
        // ensure we are the only ones who can access this at a go,
        // because it happens that we may load too fast and we are called from coroutines
        // and invalidate our pointer causing skia to break
        mutex.withLock {
            inner.loadFile(file)
            this.file = file;
            prepareNewFile()
        }
    }


    fun contrast(value: Float) {
        inner.contrast(value)
        postProcessNoAlloc()
        isModified =isModified.xor(true)
    }
    fun gamma(value: Float) {
        inner.gamma(value)
        postProcessNoAlloc()
    }
    fun exposure(value: Float,blackPoint: Float = 0.0F){
        inner.exposure(value,blackPoint)
        postProcessNoAlloc()
    }
    fun brighten(value:Float){
        inner.brightness(value)
        postProcessNoAlloc()
    }

    fun stretchContrast(lower:Float,higher:Float){
        inner.stretchContrast(lower,higher)
        postProcessNoAlloc()
    }
    fun flip(){
        inner.flip()
        postProcessAlloc()
    }

    fun verticalFlip(){
        inner.verticalFlip()
        postProcessAlloc()
    }

    fun flop(){
        inner.flop()
        postProcessAlloc()
    }
    fun transpose(){
        inner.transpose()
        postProcessAlloc()
    }

    private fun postProcessAlloc(){
        allocBuffer()
        installPixels()
        isModified =isModified.xor(true)

    }




    private fun postProcessNoAlloc() {
        installPixels()
        isModified =isModified.xor(true)
    }

}
