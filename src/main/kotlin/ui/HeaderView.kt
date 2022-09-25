@file:OptIn(ExperimentalComposeUiApi::class)

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import table.ColumnInfo
import table.Header

// TODO Find cleaner way
fun RowScope.applyWidth(width: Int?, modifier: Modifier = Modifier): Modifier {
    return if (width == null) modifier.weight(1f) else modifier.width(width.dp)
}

@Composable
fun RowScope.HeaderView(columnInfo: ColumnInfo, modifier: Modifier = Modifier) {
    Row(applyWidth(columnInfo.width, modifier).padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = columnInfo.columnType.text,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun HeaderRow(header: Header, divider: @Composable RowScope.() -> Unit) {
    Row(Modifier.height(IntrinsicSize.Min)) {
        header.asColumnList.forEach {
            if (it.visible) {
                HeaderView(it)
                if (it.columnType.showDivider) {
                    divider()
                }
            }
        }
    }
}

@Preview
@Composable
fun HeaderPreview() {
    MyTheme {
        HeaderRow(Header.default) { Divider() }
    }
}
