package com.jerryjeon.logjerry.source

import com.jerryjeon.logjerry.filter.FilterManager
import com.jerryjeon.logjerry.log.LogManager
import com.jerryjeon.logjerry.parse.CustomParser
import com.jerryjeon.logjerry.parse.ParseStatus
import com.jerryjeon.logjerry.preferences.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import okio.Path.Companion.toPath
import okio.openZip

class SourceManager(private val preferences: Preferences) {
    private val sourceScope = CoroutineScope(Dispatchers.Default)
    private val parser = CustomParser()
    val sourceFlow: MutableStateFlow<Source> = MutableStateFlow(Source.None)
    val parseStatusFlow: StateFlow<ParseStatus> = sourceFlow.map {
        when (it) {
            is Source.ZipFile -> {
                val fileSystem =  FileSystem.SYSTEM
                val zipFileSystem = fileSystem.openZip(it.file.toOkioPath())
                val files = zipFileSystem.listOrNull("/".toPath()) ?: return@map ParseStatus.NotStarted
                val content = zipFileSystem.read(files.first()) { readUtf8() }.split("\n")
                val parseResult = parser.parse(content)
                ParseStatus.Completed(parseResult, LogManager(parseResult.logs, preferences))
            }

            is Source.File -> {
                val parseResult = parser.parse(it.file.readLines())
                ParseStatus.Completed(parseResult, LogManager(parseResult.logs, preferences))
            }

            is Source.Text -> {
                val parseResult = parser.parse(it.text.split("\n"))
                val filterManager = FilterManager()
                ParseStatus.Completed(parseResult, LogManager(parseResult.logs, preferences))
            }
            Source.None -> {
                ParseStatus.NotStarted
            }
        }
    }.stateIn(sourceScope, SharingStarted.Lazily, ParseStatus.NotStarted)

    fun changeSource(source: Source) {
        this.sourceFlow.value = source
    }

    fun turnOnKeywordDetection() {
        when (val value = parseStatusFlow.value) {
            is ParseStatus.Completed -> {
                value.logManager.detectorManager.setKeywordDetectionEnabled(true)
            }
            ParseStatus.NotStarted -> {}
            is ParseStatus.Proceeding -> {}
        }
    }
}
