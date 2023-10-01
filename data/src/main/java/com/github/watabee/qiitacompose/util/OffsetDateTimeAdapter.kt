package com.github.watabee.qiitacompose.util

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object OffsetDateTimeAdapter : JsonAdapter<OffsetDateTime>() {
    override fun fromJson(reader: JsonReader): OffsetDateTime? {
        return if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull()
        } else {
            OffsetDateTime.parse(reader.nextString())
        }
    }

    override fun toJson(writer: JsonWriter, value: OffsetDateTime?) {
        if (value == null) {
            writer.nullValue()
        } else {
            writer.value(
                value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            )
        }
    }
}
