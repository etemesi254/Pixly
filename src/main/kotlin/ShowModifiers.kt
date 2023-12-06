import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ShowModifiers {
    var showTopLinearIndicator by mutableStateOf(false)
    var showFilePicker by mutableStateOf( false)
    var showDirectoryPicker by mutableStateOf( false)
    var showPopups by mutableStateOf( false)
    var showInformation by mutableStateOf(false)
    var showLightTheme by mutableStateOf(false)
    var showLightFilters by mutableStateOf(false)


}
