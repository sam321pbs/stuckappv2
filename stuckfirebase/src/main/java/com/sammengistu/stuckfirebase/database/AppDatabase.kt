package com.sammengistu.stuckfirebase.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sammengistu.stuckfirebase.constants.DATABASE_NAME
import com.sammengistu.stuckfirebase.database.dao.HiddenItemsDao
import com.sammengistu.stuckfirebase.database.dao.PostDao
import com.sammengistu.stuckfirebase.database.dao.UsersDao
import com.sammengistu.stuckfirebase.models.DraftPostModel
import com.sammengistu.stuckfirebase.models.HiddenItemModel
import com.sammengistu.stuckfirebase.models.UserModel

@Database(entities = [DraftPostModel::class, HiddenItemModel::class, UserModel::class],
    version = 8, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postsDao(): PostDao
    abstract fun hiddenItemsDao(): HiddenItemsDao
    abstract fun usersDao(): UsersDao

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

            //
            val MIGRATION_7_8 = object : Migration(7, 8) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("DELETE FROM users")
                }
            }

            val MIGRATION_6_7 = object : Migration(6, 7) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("CREATE TABLE IF NOT EXISTS `users` " +
                            "(" +
                                " PRIMARY KEY(`id`)," +
                                " `userId` TEXT NOT NULL," +
                                " `username` TEXT NOT NULL," +
                                " `avatar` TEXT NOT NULL," +
                                " `name` TEXT NOT NULL" +
                                " `occupation` TEXT NOT NULL" +
                                " `education` TEXT NOT NULL" +
                                " `bio` TEXT NOT NULL" +
                                " `ageGroup` INTEGER NOT NULL" +
                                " `gender` INTEGER NOT NULL" +
                                " `totalMadeVotes` INTEGER NOT NULL" +
                                " `totalReceivedVotes` INTEGER NOT NULL" +
                                " `totalReceivedStars` INTEGER NOT NULL" +
                            ")"
                    )
                }
            }

            val MIGRATION_3_4 = object : Migration(3, 4) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE posts ADD COLUMN ownerId TEXT DEFAULT 'N/A' NOT NULL")
                    database.execSQL("ALTER TABLE posts ADD COLUMN ownerRef TEXT DEFAULT 'N/A' NOT NULL")
                }
            }

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
                .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_6_7, MIGRATION_7_8)
                .fallbackToDestructiveMigration().build()
        }
    }
}