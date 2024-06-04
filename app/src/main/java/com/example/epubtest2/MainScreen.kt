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
        Button(onClick = {
            val epubFilePath = "/storage/emulated/0/Download/test.epub"
            navController.navigate(Screens.BookScreen.route + "/${Uri.encode(epubFilePath)}")
        }) {
            Text("Open EPUB File")
        }
    }
}