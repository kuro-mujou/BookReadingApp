package com.example.epubtest2

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File

//"/storage/emulated/0/Download/test.epub"
@Composable
fun BookScreen(epubFilePath: String) {
    val context = LocalContext.current
    var chapters by remember { mutableStateOf(listOf<Chapter>()) }
    var currentPage by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(epubFilePath) {
        val outputDir = context.cacheDir.absolutePath
        coroutineScope.launch {
            val db = AppDatabase.getDatabase(context)
            val chapterCount = db.chapterDao().getChapterCount()
            if (chapterCount == 0) {
                unzipEpubFile(epubFilePath, outputDir)
                saveChaptersToDatabase(outputDir, context)
                deleteDirectory(File(outputDir))
            }
            chapters = db.chapterDao().getAllChapters()
            currentPage = db.chapterDao().getLastReadPage(epubFilePath) ?: 0
        }
    }

    if (chapters.isNotEmpty()) {
        ChapterPager(
            chapters = chapters,
            initialPage = currentPage
        ) { pageIndex ->
            coroutineScope.launch {
                val db = AppDatabase.getDatabase(context)
                db.chapterDao().insertLastReadPage(LastReadPage(epubFilePath, pageIndex))
            }
        }
    } else {
        LoadingScreen()
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ChapterPager(
    chapters: List<Chapter>,
    initialPage: Int,
    onPageChanged: (Int) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = initialPage)
    LaunchedEffect(pagerState.currentPage) {
        onPageChanged(pagerState.currentPage)
    }

    HorizontalPager(
        count = chapters.size,
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val chapter = chapters[page]
        ChapterView(chapter)
    }
}

@Composable
fun ChapterView(chapter: Chapter) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            text = chapter.title,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        )
        HtmlText(html = chapter.content)
    }
}

@Composable
fun HtmlText(html: String) {
    val spannableString = SpannableStringBuilder(html).toString()
    val spanned = HtmlCompat.fromHtml(spannableString, HtmlCompat.FROM_HTML_MODE_LEGACY)

    Text(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
        text = spanned.toAnnotatedString(),
        style = TextStyle(
            textIndent = TextIndent(firstLine = 16.sp),
            textAlign = TextAlign.Justify,
            fontSize = 18.sp
        )
    )
}
@Composable
fun ImageFromUrl(url: String) {
    val painter = rememberAsyncImagePainter(model = url)
    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f, matchHeightConstraintsFirst = false), // Maintain aspect ratio
        contentScale = ContentScale.Fit // Adjust the content scale to fit the width
    )
}
suspend fun unzipEpubFile(epubFilePath: String, outputDir: String) {
    withContext(Dispatchers.IO) {
        val zipFile = ZipFile(epubFilePath)
        val fileHeaders = zipFile.fileHeaders

        for (fileHeader in fileHeaders) {
            val extractedFile = File(outputDir, fileHeader.fileName)
            if (!extractedFile.exists()) {
                zipFile.extractFile(fileHeader, outputDir)
            }
        }
    }
}

fun deleteDirectory(directory: File): Boolean {
    if (directory.isDirectory) {
        val children = directory.listFiles()
        if (children != null) {
            for (child in children) {
                deleteDirectory(child)
            }
        }
    }
    return directory.delete()
}
fun readXhtmlFile(filePath: String): String {
    val file = File(filePath)
    val xhtmlContent = file.readText()
    val document: Document = Jsoup.parse(xhtmlContent, "", org.jsoup.parser.Parser.xmlParser())
    val imgSrc = document.select("img").attr("src")
    return imgSrc
}
suspend fun saveChaptersToDatabase(outputDir: String, context: Context) {
    withContext(Dispatchers.IO) {
        val chapters = mutableListOf<Chapter>()
        val dir = File(outputDir)
        val xhtmlFiles = dir.walk()
            .filter { it.extension == "xhtml" || it.extension == "html" }
            .sortedWith(compareBy { extractChapterNumber(it.name) }) // Sort files by chapter number
            .toList()
        for ((index, file) in xhtmlFiles.withIndex()) {
            val document: Document = Jsoup.parse(file, "UTF-8")
            val title = document.title()
            val bodyContent = document.body().html()
            chapters.add(Chapter(title = title, content = bodyContent, chapterNumber = index + 1))
        }
        val db = AppDatabase.getDatabase(context)
        db.chapterDao().insertChapters(chapters)
    }
}

fun extractChapterNumber(fileName: String): Int {
    val regex = "\\d+".toRegex()
    val matchResult = regex.find(fileName)
    return matchResult?.value?.toInt() ?: 0
}

fun Spanned.toAnnotatedString(): AnnotatedString = buildAnnotatedString {
    val spanned = this@toAnnotatedString
    append(spanned.toString())
    getSpans(0, spanned.length, Any::class.java).forEach { span ->
        val start = getSpanStart(span)
        val end = getSpanEnd(span)
        when (span) {
            is StyleSpan -> when (span.style) {
                Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                Typeface.BOLD_ITALIC -> addStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    ), start, end
                )
            }

            is UnderlineSpan -> addStyle(
                SpanStyle(textDecoration = TextDecoration.Underline),
                start,
                end
            )

            is ForegroundColorSpan -> addStyle(
                SpanStyle(color = Color(span.foregroundColor)),
                start,
                end
            )
        }
    }
}