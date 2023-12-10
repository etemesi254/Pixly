package events

import AppContext
import isImage
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import loadImage

enum class ExternalImageViewerEvent {
    Next,
    Previous,
    ReloadImage
}

class ExternalNavigationEventBus {
    private val _events = MutableSharedFlow<ExternalImageViewerEvent>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
        extraBufferCapacity = 1,
    )
    val events = _events.asSharedFlow()

    fun produceEvent(event: ExternalImageViewerEvent) {
        _events.tryEmit(event)
    }
}

suspend fun handleKeyEvents(appCtx: AppContext) {
    appCtx.externalNavigationEventBus.events.collect() {
        if (it == ExternalImageViewerEvent.ReloadImage && appCtx.imageIsLoaded && appCtx.imFile.exists() && appCtx.imFile.isFile) {

            appCtx.showStates.showTopLinearIndicator = true;

            loadImage(appCtx)

        }
        if (it == ExternalImageViewerEvent.Next || it == ExternalImageViewerEvent.Previous) {
            if (appCtx.paths.isNotEmpty()) {
                var value = if (it == ExternalImageViewerEvent.Next)
                    appCtx.position++
                else {
                    appCtx.position--
                }

                if (value < 0) {
                    value = appCtx.paths.size - 1
                    appCtx.position = value

                } else if (value > appCtx.paths.size - 1) {
                    value = 0
                    appCtx.position = value

                }
                val fileValue = appCtx.paths.getOrNull(value)

                if (fileValue != null && isImage(fileValue)) {
                    appCtx.imFile = fileValue

                    appCtx.showStates.showTopLinearIndicator = true;

                    loadImage(appCtx)

                }
            }
        }
    }
}