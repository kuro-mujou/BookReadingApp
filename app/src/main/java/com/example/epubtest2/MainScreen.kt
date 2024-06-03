package com.example.epubtest2

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController


@Composable
fun MainScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Replace this button with your actual "Read" button in "Book A" UI
        Button(onClick = {
            val epubFilePath = "/storage/emulated/0/Download/test.epub" // Replace with your actual EPUB file path
            navController.navigate(Screens.BookPage.route + "/${Uri.encode(epubFilePath)}")
        }) {
            Text("Open EPUB File")
        }
    }
}