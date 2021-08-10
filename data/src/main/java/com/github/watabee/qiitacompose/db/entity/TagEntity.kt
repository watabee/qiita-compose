package com.github.watabee.qiitacompose.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import com.github.watabee.qiitacompose.api.response.Tag

@Entity(tableName = "tags", primaryKeys = ["id"])
internal data class TagEntity(
    @Embedded val tag: Tag
)
