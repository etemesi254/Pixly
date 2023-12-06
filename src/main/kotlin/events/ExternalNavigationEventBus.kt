package events

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

enum class ExternalImageViewerEvent{
    Next,
    Previous,
}

class ExternalNavigationEventBus {
    private val _events = MutableSharedFlow<ExternalImageViewerEvent>(
        replay = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
        extraBufferCapacity = 1,
    )
    val events = _events.asSharedFlow()

    fun produceEvent(event: ExternalImageViewerEvent) {
        _events.tryEmit(event)
    }
}