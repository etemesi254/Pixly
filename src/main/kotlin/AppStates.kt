import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.rememberSplitPaneState

@OptIn(ExperimentalSplitPaneApi::class)
class AppStates {

    var showLightTheme by  mutableStateOf( false)
    var paths = mutableStateOf("")



}