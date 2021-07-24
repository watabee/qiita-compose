package com.github.watabee.qiitacompose.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.watabee.qiitacompose.db.dao.TagDao
import com.github.watabee.qiitacompose.db.entity.TagEntity

@Database(
    entities = [TagEntity::class],
    version = 1,
    exportSchema = true
)
internal abstract class AppDatabase : RoomDatabase() {

    abstract fun tagDao(): TagDao

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
