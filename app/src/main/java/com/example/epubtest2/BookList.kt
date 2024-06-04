package com.example.epubtest2

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.io.File
import java.io.FileInputStream
import nl.siegmann.epublib.epub.EpubReader

@Composable
fun BookList(
    navController: NavController,
    filePath: String
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf<String?>(null) }
    var coverImage by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(filePath) {
        val epubFile = File(filePath)
        val epubReader = EpubReader()
        val book = epubReader.readEpub(FileInputStream(epubFile))

        title = book.title
        val coverImageResource = book.coverImage
        coverImage = coverImageResource?.let {
            BitmapFactory.decodeStream(coverImageResource.inputStream)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        title?.let {
            Text(text = it, style = MaterialTheme.typography.labelLarge)
        }

        coverImage?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f, matchHeightConstraintsFirst = false), // Maintain aspect ratio
                contentScale = ContentScale.Fit
            )
        }

        Button(
            onClick = {

            }
        ) {
            Text(
                text = "Open Content"
            )
        }
    }
}