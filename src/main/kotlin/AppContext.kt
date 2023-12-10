import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import events.ExternalNavigationEventBus
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

@OptIn(ExperimentalSplitPaneApi::class)
class AppContext {

    var paths:MutableList<File> = mutableListOf()
    var position = 0;

    var showStates by mutableStateOf(ShowModifiers())

    var bottomStatus by mutableStateOf("")

    var isFirstDraw by mutableStateOf(false)

    var imageIsLoaded by mutableStateOf(false)
    var externalNavigationEventBus by mutableStateOf(ExternalNavigationEventBus())

    var image by mutableStateOf(ZilImageAndBitmapInterop());
    var imFile by  mutableStateOf(File(""))
    var rootDirectory by  mutableStateOf("/")


}