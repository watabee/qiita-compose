package com.github.watabee.qiitacompose.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.github.watabee.qiitacompose.api.response.User
import com.github.watabee.qiitacompose.db.entity.UserEntity

@Dao
internal abstract class UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    abstract suspend fun findById(userId: String): User?

    @Insert(entity = UserEntity::class, onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insert(user: User)

    @Query("DELETE FROM users WHERE id IN(:userIds)")
    protected abstract suspend fun deleteByIds(userIds: List<String>)

    @Query("SELECT COUNT(*) FROM users")
    protected abstract suspend fun countUsers(): Int

    @Query("SELECT id FROM users ORDER BY rowid LIMIT :limit")
    protected abstract suspend fun findOldUserIds(limit: Int): List<String>

    @Transaction
    open suspend fun insertOrUpdate(user: User, maxUserCount: Int) {
        insert(user)
        val userCount = countUsers()
        if (userCount <= maxUserCount) {
            return
        }
        val userIds = findOldUserIds(userCount - maxUserCount)
        deleteByIds(userIds)
    }
}
