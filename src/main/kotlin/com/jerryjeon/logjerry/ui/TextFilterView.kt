@file:OptIn(ExperimentalComposeUiApi::class)

package com.jerryjeon.logjerry.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jerryjeon.logjerry.filter.TextFilter
import com.jerryjeon.logjerry.filter.TextFilterType
import com.jerryjeon.logjerry.table.ColumnType

@Composable
fun TextFilterView(
    addFilter: (TextFilter) -> Unit,
    dismiss: () -> Unit
) {
    Column(
        Modifier.border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)).background(MaterialTheme.colors.background)
            .padding(16.dp)
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides LocalTextStyle.current.copy(fontSize = 12.sp)
        ) {
            AddTextFilterView(addFilter, dismiss)
        }
    }
}

@Composable
private fun AddTextFilterView(
    addFilter: (TextFilter) -> Unit,
    dismiss: () -> Unit,
) {
    var text by remember { mutableStateOf("") }
    val columnTypeState = remember { mutableStateOf(ColumnType.Log) }
    val radioOptions = TextFilterType.values().map { it.name }
    val selectedOption = remember { mutableStateOf(radioOptions[0]) }
    Column {
        Row(
            modifier = Modifier.onPreviewKeyEvent {
                when {
                    it.key == Key.Escape && it.type == KeyEventType.KeyDown -> {
                        text = ""
                        dismiss()
                        true
                    }
                    it.key == Key.Enter && it.type == KeyEventType.KeyDown -> {
                        if (text.isNotBlank()) {
                            addFilter(TextFilter(columnTypeState.value, TextFilterType.valueOf(selectedOption.value), text))
                            text = ""
                        }
                        true
                    }

                    else -> false
                }
            }
        ) {
            Column {
                radioOptions.forEach { text ->
                    Row(
                        Modifier
                            .padding(all = 8.dp)
                            .selectable(
                                selected = (text == selectedOption.value),
                                onClick = { selectedOption.value = text }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selectedOption.value),
                            onClick = null
                        )
                        Text(
                            text = text,
                            modifier = Modifier.padding(start = 8.dp),
                            color = if (text == "Include") MaterialTheme.colors.secondary else MaterialTheme.colors.error
                        )
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            Column {
                TextField(
                    modifier = Modifier.height(60.dp),
                    value = text,
                    onValueChange = {
                        text = it
                    },
                    singleLine = true,
                    leadingIcon = {
                        SelectColumnTypeView(columnTypeState)
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent
                    )
                )
            }
        }

        Row(modifier = Modifier.align(Alignment.End)) {
            TextButton(
                onClick = {
                    dismiss()
                    text = ""
                }
            ) {
                Text("Cancel")
            }
            TextButton(
                onClick = {
                    addFilter(TextFilter(columnTypeState.value, TextFilterType.valueOf(selectedOption.value), text))
                    dismiss()
                    text = ""
                }
            ) {
                Text("Ok")
            }
        }
    }
}

@Composable
private fun SelectColumnTypeView(
    columnTypeState: MutableState<ColumnType>
) {
    val allowedColumnTypes = listOf(ColumnType.Log, ColumnType.Tag, ColumnType.PackageName)
    var expanded by remember { mutableStateOf(false) }
    var columnType by columnTypeState
    Box(Modifier) {
        Row(
            modifier = Modifier.fillMaxHeight()
                .clickable(onClick = { expanded = true })
                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
        ) {
            Text(
                columnType.name,
                modifier = Modifier.wrapContentWidth().align(Alignment.CenterVertically),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Default.ArrowDropDown, "Column types", modifier = Modifier.align(Alignment.CenterVertically))
        }
        DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
            allowedColumnTypes.forEach {
                DropdownMenuItem(onClick = {
                    columnType = it
                    expanded = false
                }) {
                    Text(text = it.name)
                }
            }
        }
    }
}
