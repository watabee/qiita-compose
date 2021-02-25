package com.github.watabee.qiitacompose.ui.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.concurrent.getOrSet

object AppDateFormatter {

    private val simpleDateFormats = ThreadLocal<SimpleDateFormat>()

    fun dateToString(format: String, date: Date): String {
        val simpleDateFormat = simpleDateFormats.getOrSet { SimpleDateFormat(format, Locale.US) }
        if (simpleDateFormat.toPattern() != format) {
            simpleDateFormat.applyPattern(format)
        }
        simpleDateFormat.timeZone = TimeZone.getDefault()
        return simpleDateFormat.format(date)
    }
}
