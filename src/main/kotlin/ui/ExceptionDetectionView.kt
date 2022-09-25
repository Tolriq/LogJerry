package ui

import DetectionResultFocus
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import detection.ExceptionDetection

@Composable
fun ExceptionDetectionView(
    modifier: Modifier,
    detectionResultFocus: DetectionResultFocus?,
    moveToPreviousOccurrence: (DetectionResultFocus) -> Unit,
    moveToNextOccurrence: (DetectionResultFocus) -> Unit,
) {
    CompositionLocalProvider(
        LocalTextStyle provides LocalTextStyle.current.copy(fontSize = 12.sp),
    ) {
        Box(modifier = modifier) {
            Column(Modifier.height(IntrinsicSize.Min).padding(8.dp)) {
                Text(AnnotatedString("Exception", spanStyle = ExceptionDetection().detectedStyle))
                detectionResultFocus?.let {
                    Row {
/* TODO not sure it's helpful... remove it because it looks bad
                        Row(modifier = Modifier.weight(1f).fillMaxHeight()) {
                            // TODO cleanup ; don't cast
                            (it.focusing as? ExceptionDetectionResult)?.let {
                                Text(it.exception, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
*/
                        Row(modifier = Modifier.weight(1f).fillMaxHeight()) {
                            if (it.focusing == null) {
                                Text(
                                    "${it.results.size} results",
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            } else {
                                Text(
                                    "${it.currentIndexInView} / ${detectionResultFocus.totalCount}",
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                        }
                        IconButton(onClick = { moveToPreviousOccurrence(it) }) {
                            Icon(Icons.Default.KeyboardArrowUp, "Previous Occurrence")
                        }
                        IconButton(onClick = { moveToNextOccurrence(it) }) {
                            Icon(Icons.Default.KeyboardArrowDown, "Next Occurrence")
                        }
                    }
                }
            }
        }
    }
}
