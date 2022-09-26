package com.jerryjeon.logjerry.parse

import com.jerryjeon.logjerry.log.Log
import java.util.concurrent.atomic.AtomicInteger

class CustomParser : LogParser {

    private val number = AtomicInteger(1)
    override fun canParse(raw: String): Boolean {
        // TODO check
        return true
    }

    override fun parse(rawLines: List<String>): ParseResult {
        val logs = mutableListOf<Log>()
        val invalidSentences = mutableListOf<Pair<Int, String>>()
        var lastLog: Log? = null
        rawLines.forEachIndexed { index, s ->
            lastLog = try {
                val log = parseSingleLineLog(s)

                // Custom continuation
                if (log.log.startsWith("Cont(")) {
                    lastLog?.let {
                        it.copy(log = "${it.log}${log.log.substringAfter(") ")}")
                    } ?: log
                } else {
                    lastLog?.let { logs.add(it) }
                    log
                }
            } catch (e: Exception) {
                val continuedLog = if (lastLog == null) {
                    invalidSentences.add(index to s)
                    return@forEachIndexed
                } else {
                    lastLog!!
                }
                continuedLog.copy(log = "${continuedLog.log}\n$s")
            }
        }
        lastLog?.let { logs.add(it) }
        return ParseResult(logs, invalidSentences)
    }
    private fun parseSingleLineLog(raw: String): Log {
        val split = raw.split(" ")

        val date = split[0]
        val time = split[1]
        val pid = 0L
        val tid = 0L
        val packageName = null

        val thirdSegment = split[2].split("/")
        val priority = thirdSegment[0].replace("Verbose","V").replace("Error","E")
        val tag = thirdSegment[1].removeSuffix(":")

        val originalLog = split.subList(3, split.size).joinToString(separator = " ")

        return Log(number.getAndIncrement(), date, time, pid, tid, packageName, priority, tag, originalLog)
    }
}
