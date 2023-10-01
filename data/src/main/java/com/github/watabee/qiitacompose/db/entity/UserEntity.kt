package com.github.watabee.qiitacompose.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import com.github.watabee.qiitacompose.api.response.User

@Entity(tableName = "users", primaryKeys = ["id"])
internal data class UserEntity(
    @Embedded val user: User,
)
