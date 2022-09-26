package com.jerryjeon.logjerry.parse

import com.jerryjeon.logjerry.log.Log
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldBeSingleton
import org.junit.jupiter.api.Test

internal class ParserTest {

    @Test
    fun `Given raw custom log, parse well`() {
        val parseResult = CustomParser().parse(
            listOf("2022-09-20 17:50:28.689 Verbose/QueryBuilder: Query: COUNT on songs, count=564 |  - '' [0.74ms]")
        )
        parseResult.invalidSentences.shouldBeEmpty()
        parseResult.logs.shouldBeSingleton {
            Log(
                number = 1,
                date = "2022-09-20",
                time = "17:50:28.689",
                pid = 0,
                tid = 0,
                packageName = null,
                priorityText = "Verbose",
                tag = "QueryBuilder",
                log = "COUNT on songs, count=564 |  - '' [0.74ms]",
            )
        }

    }

    @Test
    fun `Given raw login, parse well`() {
        val parseResult = DefaultParser().parse(
            listOf("2021-12-19 23:05:36.664 165-165/? I/hwservicemanager: Since android.hardware.media.omx@1.0::IOmxStore/default is not registered, trying to start it as a lazy HAL.")
        )

        parseResult.logs.shouldBeSingleton {
            Log(
                number = 1,
                date = "2021-12-19",
                time = "23:05:36.664",
                pid = 165,
                tid = 165,
                packageName = null,
                priorityText = "I",
                tag = "hwservicemanager",
                log = "Since android.hardware.media.omx@1.0::IOmxStore/default is not registered, trying to start it as a lazy HAL.",
            )
        }
        parseResult.invalidSentences.shouldBeEmpty()
    }
}
