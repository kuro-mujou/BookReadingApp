package com.example.epubtest2

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Composable
fun FilePickerScreen(
    navController: NavController,
    context: Context
) {
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var cacheFilePath by remember { mutableStateOf<String?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            selectedFileUri = uri
            uri?.let {
                val fileName = getFileName(context, it)
                val cacheFile = File(context.cacheDir, fileName)
                if (cacheFile.exists()) {
                    Toast.makeText(context, "File already imported", Toast.LENGTH_SHORT).show()
                    cacheFilePath = cacheFile.absolutePath
                } else {
                    cacheFilePath = saveFileToCache(context, it, fileName)
                }
            }
        }
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = {
            launcher.launch(arrayOf("application/epub+zip"))
        }) {
            Text(text = "Pick EPUB File")
        }

        selectedFileUri?.let {
            Text(text = "Selected file: $it")
        }

        cacheFilePath?.let {
            Text(text = "File saved to cache: $it")
            navController.navigate(Screens.BookListScreen.route + "/${Uri.encode(it)}")
        }
    }
}

private fun getFileName(context: Context, uri: Uri): String {
    var fileName = "unknown"
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                fileName = it.getString(nameIndex)
            }
        }
    }
    return fileName
}

private fun saveFileToCache(context: Context, uri: Uri, fileName: String): String? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val cacheFile = File(context.cacheDir, fileName)
        val outputStream = FileOutputStream(cacheFile)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        cacheFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}