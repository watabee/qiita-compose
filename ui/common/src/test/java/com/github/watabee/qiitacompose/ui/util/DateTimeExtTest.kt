package com.github.watabee.qiitacompose.ui.util

import com.google.common.truth.Truth
import org.junit.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

class DateTimeExtTest {
    @Test
    fun test_convertOffsetDateTimeToFormattedString() {
        val dateTime = OffsetDateTime.of(2022, 3, 6, 9, 28, 15, 0, ZoneOffset.ofHours(9))
        Truth.assertThat(dateTime.format("yyyy年MM月dd日hh時mm分ss秒"))
            .isEqualTo("2022年03月06日09時28分15秒")
    }
}
