package com.example.epubtest2

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun SetupNavGraph(
    startDestination: String,
    navController: NavHostController,
    context: Context
){
    NavHost(
        navController = navController,
        startDestination = startDestination
    ){
        mainGraph(navController = navController, context = context)
    }
}