package com.jerryjeon.logjerry.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jerryjeon.logjerry.detector.DetectionStatus

@Composable
fun ExceptionDetectionView(
    modifier: Modifier,
    detectionStatus: DetectionStatus,
    moveToPreviousOccurrence: (DetectionStatus) -> Unit,
    moveToNextOccurrence: (DetectionStatus) -> Unit,
) {
    CompositionLocalProvider(
        LocalTextStyle provides LocalTextStyle.current.copy(fontSize = 12.sp),
    ) {
        Row(
            modifier
                .wrapContentWidth()
                .border(ButtonDefaults.outlinedBorder, MaterialTheme.shapes.small)
                .padding(start = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Exception")
            Spacer(modifier = Modifier.width(8.dp))
            ExceptionDetectionSelectionExist(detectionStatus, moveToPreviousOccurrence, moveToNextOccurrence)
        }
    }
}

@Composable
fun ExceptionDetectionSelectionExist(
    selection: DetectionStatus,
    moveToPreviousOccurrence: (DetectionStatus) -> Unit,
    moveToNextOccurrence: (DetectionStatus) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (selection.selected == null) {
            Text(
                "     ${selection.allDetections.size} results",
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        } else {
            Text(
                "${selection.currentIndexInView} / ${selection.totalCount}",
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
    IconButton(onClick = { moveToPreviousOccurrence(selection) }) {
        Icon(Icons.Default.KeyboardArrowUp, "Previous Occurrence")
    }
    IconButton(onClick = { moveToNextOccurrence(selection) }) {
        Icon(Icons.Default.KeyboardArrowDown, "Next Occurrence")
    }
}
