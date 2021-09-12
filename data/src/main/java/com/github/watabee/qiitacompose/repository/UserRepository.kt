package com.github.watabee.qiitacompose.repository

import com.github.watabee.qiitacompose.api.response.User
import com.github.watabee.qiitacompose.db.dao.UserDao
import javax.inject.Inject

interface UserRepository {
    suspend fun findById(userId: String): User?

    suspend fun insertOrUpdate(user: User)
}

internal class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun findById(userId: String): User? {
        return userDao.findById(userId)
    }

    override suspend fun insertOrUpdate(user: User) {
        userDao.insertOrUpdate(user, MAX_STORE_USER)
    }

    companion object {
        private const val MAX_STORE_USER = 100
    }
}
