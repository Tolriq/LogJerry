@file:OptIn(ExperimentalComposeUiApi::class)

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import detection.JsonDetectionResult
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import log.refine.RefinedLog
import preferences.Preferences
import table.ColumnInfo
import table.ColumnType
import table.Header

val json = Json { prettyPrint = true }

@Composable
fun LogRow(
    refinedLog: RefinedLog,
    preferences: Preferences,
    header: Header,
    divider: @Composable RowScope.() -> Unit
) {
    Row(Modifier.height(IntrinsicSize.Min)) {
        Spacer(Modifier.width(8.dp))
        header.asColumnList.forEach { columnInfo ->
            if (columnInfo.visible) {
                CellByColumnType(preferences, columnInfo, refinedLog)
                if (columnInfo.columnType.showDivider) {
                    divider()
                }
            }
        }
        Spacer(Modifier.width(8.dp))
    }
}

@Composable
fun RowScope.CellByColumnType(preferences: Preferences, columnInfo: ColumnInfo, refinedLog: RefinedLog) {
    val log = refinedLog.log
    when (columnInfo.columnType) {
        ColumnType.Number -> NumberCell(preferences, columnInfo, log)
        ColumnType.Date -> DateCell(preferences, columnInfo, log)
        ColumnType.Time -> TimeCell(preferences, columnInfo, log)
        ColumnType.Pid -> PidCell(preferences, columnInfo, log)
        ColumnType.Tid -> TidCell(preferences, columnInfo, log)
        ColumnType.PackageName -> PackagerNameCell(preferences, columnInfo, log)
        ColumnType.Priority -> PriorityCell(preferences, columnInfo, log)
        ColumnType.Tag -> TagCell(preferences, columnInfo, log)
        ColumnType.Detection -> DetectionCell(columnInfo, refinedLog)
        ColumnType.Log -> LogCell(preferences, columnInfo, refinedLog)
    }
}

@Composable
private fun RowScope.NumberCell(preferences: Preferences, number: ColumnInfo, log: Log) {
    Text(
        text = log.number.toString(),
        style = MaterialTheme.typography.body2.copy(
            fontSize = preferences.fontSize,
            color = preferences.colorByPriority.getValue(log.priority)
        ),
        modifier = this.cellDefaultModifier(number.width)
    )
}

@Composable
private fun RowScope.DateCell(preferences: Preferences, date: ColumnInfo, log: Log) {
    Text(
        text = log.date,
        style = MaterialTheme.typography.body2.copy(
            fontSize = preferences.fontSize,
            color = preferences.colorByPriority.getValue(log.priority)
        ),
        modifier = this.cellDefaultModifier(date.width)
    )
}

@Composable
private fun RowScope.TimeCell(preferences: Preferences, time: ColumnInfo, log: Log) {
    Text(
        text = log.time,
        style = MaterialTheme.typography.body2.copy(
            fontSize = preferences.fontSize,
            color = preferences.colorByPriority.getValue(log.priority)
        ),
        modifier = this.cellDefaultModifier(time.width)
    )
}

@Composable
private fun RowScope.PidCell(preferences: Preferences, pid: ColumnInfo, log: Log) {
    Text(
        text = log.pid.toString(),
        style = MaterialTheme.typography.body2.copy(
            fontSize = preferences.fontSize,
            color = preferences.colorByPriority.getValue(log.priority)
        ),
        modifier = this.cellDefaultModifier(pid.width)
    )
}

@Composable
private fun RowScope.TidCell(preferences: Preferences, tid: ColumnInfo, log: Log) {
    Text(
        text = log.tid.toString(),
        style = MaterialTheme.typography.body2.copy(
            fontSize = preferences.fontSize,
            color = preferences.colorByPriority.getValue(log.priority)
        ),
        modifier = this.cellDefaultModifier(tid.width)
    )
}

@Composable
private fun RowScope.PackagerNameCell(preferences: Preferences, packageName: ColumnInfo, log: Log) {
    Text(
        text = log.packageName ?: "?",
        style = MaterialTheme.typography.body2.copy(
            fontSize = preferences.fontSize,
            color = preferences.colorByPriority.getValue(log.priority)
        ),
        modifier = this.cellDefaultModifier(packageName.width)
    )
}

@Composable
private fun RowScope.PriorityCell(preferences: Preferences, priority: ColumnInfo, log: Log) {
    Text(
        text = log.priority.text,
        style = MaterialTheme.typography.body2.copy(
            fontSize = preferences.fontSize,
            color = preferences.colorByPriority.getValue(log.priority)
        ),
        modifier = this.cellDefaultModifier(priority.width)
    )
}

@Composable
private fun RowScope.TagCell(preferences: Preferences, tag: ColumnInfo, log: Log) {
    Text(
        text = log.tag,
        style = MaterialTheme.typography.body2.copy(
            fontSize = preferences.fontSize,
            color = preferences.colorByPriority.getValue(log.priority)
        ),
        modifier = this.cellDefaultModifier(tag.width)
    )
}

@Composable
private fun RowScope.LogCell(preferences: Preferences, logHeader: ColumnInfo, refinedLog: RefinedLog) {
    Box(modifier = this.cellDefaultModifier(logHeader.width)) {
        SelectionContainer {
            Text(
                text = refinedLog.annotatedLog,
                style = MaterialTheme.typography.body2.copy(
                    fontSize = preferences.fontSize,
                    color = preferences.colorByPriority.getValue(refinedLog.log.priority)
                ),
                modifier = Modifier
            )
        }
    }
}

@Composable
private fun RowScope.DetectionCell(button: ColumnInfo, refinedLog: RefinedLog) {
    var showPrettyJsonDialog: Pair<Boolean, JsonObject>? by remember { mutableStateOf(null) }

    Column(modifier = this.cellDefaultModifier(button.width)) {
        refinedLog.detectionResults.values.flatten().forEach { result ->
            when (result) {
                is JsonDetectionResult -> {
                    result.jsonList.forEachIndexed { index, jsonObject ->
                        TextButton(onClick = { showPrettyJsonDialog = true to jsonObject }) {
                            Row {
                                Text("{ }")
                                Text("${index + 1}", fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    showPrettyJsonDialog?.let { (_, jsonObject) ->
        Dialog(
            onCloseRequest = { showPrettyJsonDialog = null },
            title = "Pretty Json",
            state = DialogState(width = 800.dp, height = 600.dp),
            onPreviewKeyEvent = { keyEvent ->
                if (keyEvent.isMetaPressed && keyEvent.key == Key.W && keyEvent.type == KeyEventType.KeyDown) {
                    showPrettyJsonDialog = null
                }
                false
            }
        ) {
            SelectionContainer {
                Text(json.encodeToString(JsonObject.serializer(), jsonObject), modifier = Modifier.padding(16.dp))
            }
        }
    }
}

/*
@Preview
@Composable
fun LogRowPreview() {
    MyTheme {
        LogRow(SampleData.log, Header.default) { Divider() }
    }
}
*/
fun RowScope.cellDefaultModifier(width: Int?, modifier: Modifier = Modifier): Modifier {
    return applyWidth(width, modifier)
        .padding(horizontal = 4.dp, vertical = 8.dp)
}
