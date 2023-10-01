package com.github.watabee.qiitacompose.ui.util

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap

private val formatters = ConcurrentHashMap<String, DateTimeFormatter>()

fun OffsetDateTime.format(pattern: String): String = format(formatters.getOrPut(pattern) { DateTimeFormatter.ofPattern(pattern) })
