package history

import AppContext
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HistoryWidget(ctx: AppContext) {
    val state = rememberLazyListState()
    val regexSplit = remember { Regex("(?=\\p{Upper})") }
    if (ctx.currentImageContext()!=null) {
        key(ctx.currentImageContext()!!.imageModified) {
            Box() {
                LazyColumn(modifier = Modifier.fillMaxHeight().fillMaxWidth(), state) {
                    ctx.getHistory()?.getHistory()?.size?.let {
                        items(it) { it ->

                            val history = ctx.getHistory()!!.getHistory()[it]
                            val historyValue = ctx.getHistory()!!.getValue()[it];

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
                                if (history.requiresValue()) {
                                    Text(historyValue.toString())
                                }
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