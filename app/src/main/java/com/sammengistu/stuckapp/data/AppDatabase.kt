package com.sammengistu.stuckapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sammengistu.stuckapp.utils.DATABASE_NAME

@Database(entities = [Post::class], version = 2, exportSchema = false)
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

            //Todo: migration not working properly, figure this out
            val MIGRATION_1_2 = object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
//                    database.execSQL("ALTER TABLE ${Post.TABLE_NAME} ADD COLUMN image1Loc TEXT")
//                    database.execSQL("ALTER TABLE ${Post.TABLE_NAME} ADD COLUMN image2Loc TEXT")
//                    database.execSQL("ALTER TABLE ${Post.TABLE_NAME} ADD COLUMN type TEXT")
                }
            }

            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
//                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}