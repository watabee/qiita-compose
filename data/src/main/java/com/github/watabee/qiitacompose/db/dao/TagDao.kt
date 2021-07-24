package com.github.watabee.qiitacompose.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.watabee.qiitacompose.api.response.Tag
import com.github.watabee.qiitacompose.db.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TagDao {

    @Query("SELECT * FROM tags")
    abstract fun getAllTags(): Flow<List<Tag>>

    @Insert(entity = TagEntity::class, onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(tags: List<Tag>)

    @Query("DELETE FROM tags")
    abstract suspend fun deleteAll()
}
