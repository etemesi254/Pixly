import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import kotlinx.coroutines.sync.Mutex
import org.jetbrains.skia.Bitmap

class ProtectedBitmap : ProtectedBitmapInterface {
    var mutex = Mutex()
    var image = Bitmap()
    override fun asImageBitmap(): ImageBitmap {
        return image.asComposeImageBitmap()
    }

    override fun mutex(): Mutex {
        return mutex
    }

}

