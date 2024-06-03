package com.example.epubtest2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChapterDao {
    @Insert
    suspend fun insertChapters(chapters: List<Chapter>)

    @Query("SELECT * FROM chapters WHERE chapterNumber = :chapterNumber")
    suspend fun getChapterByNumber(chapterNumber: Int): Chapter?

    @Query("SELECT * FROM chapters ORDER BY chapterNumber")
    suspend fun getAllChapters(): List<Chapter>

    @Query("SELECT COUNT(*) FROM chapters")
    suspend fun getChapterCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLastReadPage(lastReadPage: LastReadPage)

    @Query("SELECT lastPage FROM last_read_page WHERE epubFilePath = :epubFilePath")
    suspend fun getLastReadPage(epubFilePath: String): Int?
}