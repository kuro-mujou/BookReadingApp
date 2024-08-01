package com.example.epubtest2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import nl.siegmann.epublib.epub.EpubReader
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.io.FileInputStream

@Composable
fun BookContent(
    navController: NavController,
    filePath: String
) {
    var content by remember { mutableStateOf<String?>("") }
    var paragraphs by remember {mutableStateOf<List<String>>(emptyList())}

    var otherTag by remember {mutableStateOf<String>("")}
    LaunchedEffect(filePath) {
        val epubFile = File(filePath)
        val epubReader = EpubReader()
        val book = epubReader.readEpub(FileInputStream(epubFile))
        content = book.spine.getResource(3)?.data?.let { String(it)}

        // Remove the entire <head> section
//        paragraphs = doc?.select("head")?.remove()?.

        // Extract all <p> tags
//         doc?.html()?.let { it)}.toString()
        paragraphs = extractParagraphs(content.toString())
        otherTag = selectExceptP(content.toString())
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        paragraphs?.let{Text(text = it.toString())}
        Row (
            modifier = Modifier.fillMaxWidth().height(30.dp).background(color = Color.Red)
        ){

        }
        otherTag?.let{Text(text = it.toString())}
    }
}

fun extractParagraphs(htmlContent: String): List<String> {
    val doc: Document = Jsoup.parse(htmlContent)

    // Remove the entire <head> section
    doc.select("head").clear()
    val paragraphs = doc.select("p")
    // Extract all <p> tags
//    val paragraphs: Elements = doc.select("p")
    return paragraphs.map { it.text() }
}
fun selectExceptP(htmlContent: String): String {
    val doc: Document = Jsoup.parse(htmlContent)

    // Select all p tags and remove them
    doc.select("p").clear()

    return doc.outerHtml()
}