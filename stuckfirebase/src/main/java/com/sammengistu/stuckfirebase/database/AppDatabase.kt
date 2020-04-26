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
import com.sammengistu.stuckfirebase.models.HiddenItemModel
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.models.UserModel

@Database(entities = [PostModel::class, HiddenItemModel::class, UserModel::class],
    version = 10, exportSchema = false)
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
            val MIGRATION_8_10 = object : Migration(8, 10) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("DROP TABLE posts")
                    database.execSQL("DROP TABLE hidden_items")
                    database.execSQL("DROP TABLE users")

                    database.execSQL("CREATE TABLE IF NOT EXISTS `posts` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT, `ownerRef` TEXT NOT NULL, `question` TEXT NOT NULL, `privacy` TEXT NOT NULL, `category` TEXT NOT NULL, `type` TEXT NOT NULL, `image1` TEXT NOT NULL, `image2` TEXT NOT NULL, `choice1` TEXT NOT NULL, `choice2` TEXT NOT NULL, `choice3` TEXT NOT NULL, `choice4` TEXT NOT NULL, `votes1` INTEGER NOT NULL, `votes2` INTEGER NOT NULL, `votes3` INTEGER NOT NULL, `votes4` INTEGER NOT NULL, `totalStars` INTEGER NOT NULL, `totalComments` INTEGER NOT NULL, `ref` TEXT NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `hidden_items` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `ownerRef` TEXT NOT NULL, `itemRef` TEXT NOT NULL, `type` TEXT NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `users` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` TEXT NOT NULL, `username` TEXT NOT NULL, `avatar` TEXT NOT NULL, `name` TEXT NOT NULL, `occupation` TEXT NOT NULL, `education` TEXT NOT NULL, `bio` TEXT NOT NULL, `ageGroup` INTEGER NOT NULL, `gender` INTEGER NOT NULL, `totalMadeVotes` INTEGER NOT NULL, `totalReceivedVotes` INTEGER NOT NULL, `totalReceivedStars` INTEGER NOT NULL, `ref` TEXT NOT NULL)")
                }
            }

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
                .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_10)
                .fallbackToDestructiveMigration().build()
        }
    }
}