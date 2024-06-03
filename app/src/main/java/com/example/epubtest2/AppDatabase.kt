package com.example.epubtest2

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [Chapter::class, LastReadPage::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chapterDao(): ChapterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "epub_reader_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}