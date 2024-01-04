import org.cae.pixly.AndroidProtectedBitmap
import org.cae.pixly.ZilAndroidBitmap
import kotlin.system.measureTimeMillis

/**
 * Load the image specified by appCtx.imFile
 *
 * @param appCtx: Application context
 * @param forceReload: Reload the image even if it's currently loaded
 *
 * NB: DO NOT RUN THIS ON THE MAIN THREAD AS IT WILL BLOCK
 * OTHER I/0, RUN IT ON IO THREAD
 * */
actual fun loadImage(appCtx: AppContext, forceReload: Boolean) {

    val time = measureTimeMillis {
        val c = ZilJvmImage(appCtx.imFile.path);
        val image = ZilAndroidBitmap(c, appCtx.sharedBuffer);
        appCtx.initializeImageSpecificStates(image)
        // generate c
        val ctx = appCtx.currentImageContext();
        ctx?.initCurrentCanvas(AndroidProtectedBitmap())
        appCtx.setImageIsLoaded(true)

    }
}