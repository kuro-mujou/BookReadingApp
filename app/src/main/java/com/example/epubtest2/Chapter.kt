package com.example.epubtest2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chapters")
data class Chapter(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val chapterNumber: Int
)

@Entity(tableName = "last_read_page")
data class LastReadPage(
    @PrimaryKey val epubFilePath: String,
    val lastPage: Int
)
