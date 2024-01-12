package history

import AppContext
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun HistoryWidget(ctx: AppContext) {
    val state = rememberScrollState()
    val regexSplit = remember { Regex("(?=\\p{Upper})") }

    if (ctx.currentImageContext() != null) {

        var toModify by remember { mutableStateOf(false) }
        rememberCoroutineScope().launch {
            ctx.currentImageContext()!!.imageModified.collect {
                toModify = it
            }
        }

        key(toModify) {
            Box(modifier = Modifier.fillMaxSize().padding(end=10.dp)) {
                Divider(modifier = Modifier.fillMaxHeight().width(1.dp))

                Column(
                    modifier = Modifier.fillMaxSize().padding(start = 2.dp).verticalScroll(state = state ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text("History", style = MaterialTheme.typography.h5, modifier = Modifier.padding(vertical = 10.dp))
                    Divider(modifier = Modifier.padding(end=10.dp))

                    ctx.getHistory()?.getHistory()?.forEachIndexed { index, history ->

                        Column(modifier = Modifier.fillMaxWidth().padding(end=10.dp)) {

                            Row(
                                modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // separate the text based on Uppercase
                                // so that we can have things like
                                // VerticalFlip read Vertical Flip
                                val historyFormatted =
                                    history.toString().split(regexSplit).joinToString(" ") { string -> string }

                                Text(historyFormatted)

                            }

                            Divider()

                        }

                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(
                        scrollState = state
                    )
                )
            }
        }
    }

}