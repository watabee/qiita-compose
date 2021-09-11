package com.github.watabee.qiitacompose.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.watabee.qiitacompose.db.dao.TagDao
import com.github.watabee.qiitacompose.db.dao.UserDao
import com.github.watabee.qiitacompose.db.entity.TagEntity
import com.github.watabee.qiitacompose.db.entity.UserEntity

@Database(
    entities = [TagEntity::class, UserEntity::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
internal abstract class AppDatabase : RoomDatabase() {

    abstract fun tagDao(): TagDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "app.db").build()
                    .apply { instance = this }
            }
        }
    }
}
