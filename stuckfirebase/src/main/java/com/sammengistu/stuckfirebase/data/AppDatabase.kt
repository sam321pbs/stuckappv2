package com.sammengistu.stuckapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sammengistu.stuckfirebase.constants.DATABASE_NAME

@Database(entities = [DraftPostModel::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postsDao(): PostDao

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {

            val MIGRATION_2_3 = object : Migration(2, 3) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE posts ADD COLUMN privacy TEXT DEFAULT 'public' NOT NULL")
                }
            }

            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .addMigrations(MIGRATION_2_3)
                .fallbackToDestructiveMigration().build()
        }
    }
}