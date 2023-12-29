package extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Simple function to help launch on IO thread
 *
 * This is launched in a separate threadpool that doesn't block
 * the caller thread or the UI thread
 * */
fun CoroutineScope.launchOnIoThread(
    block: suspend CoroutineScope.() -> Unit
) {
    // I got tired of repeating this every time
    this.launch(Dispatchers.IO) {
        block()
    }
}