import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import events.ExternalNavigationEventBus
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi

@OptIn(ExperimentalSplitPaneApi::class)
class AppContext {

    var paths by mutableStateOf("")

    var showStates by mutableStateOf(ShowModifiers())

    var currentFileLoaded by mutableStateOf("")

    var bottomStatus by mutableStateOf("")

    var isFirstDraw by mutableStateOf(false)

    var imageIsLoaded by mutableStateOf(false)

    var externalNavigationEventBus by mutableStateOf(ExternalNavigationEventBus())

    var image by mutableStateOf(ZilImageAndBitmapInterop());


}