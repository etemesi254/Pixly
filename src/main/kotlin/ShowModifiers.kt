import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ShowModifiers {
    var showTopLinearIndicator by mutableStateOf(false)
    var showFilePicker by mutableStateOf(false)
    var showDirectoryPicker by mutableStateOf(false)
    var showPopups by mutableStateOf(false)
    var showInformation by mutableStateOf(false)
    var showLightTheme by mutableStateOf(false)
    var showLightFilters by mutableStateOf(true)
    var showDirectoryViewer by mutableStateOf(true)
    var showBlurFilters by mutableStateOf(true)
    var showHslFilters by mutableStateOf(true)
    var showOrientationFilters by mutableStateOf(true)
    var showSaveDialog by mutableStateOf(false)
    var showHistogram by mutableStateOf(true)
    var showLevels by mutableStateOf(true)


}
