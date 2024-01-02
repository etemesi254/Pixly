import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.sync.Mutex

interface ProtectedBitmapInterface {

    fun asImageBitmap(): ImageBitmap

    fun mutex(): Mutex

}