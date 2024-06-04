package com.example.epubtest2

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation

fun NavGraphBuilder.mainGraph(
    navController: NavController,
    context: Context
){
    navigation(startDestination = Screens.FilePickerScreen.route,route = Screens.MainRoute.route)
    {
        composable(
            route = Screens.MainScreen.route
        ) {
            MainScreen(navController)
        }
        composable(
            route = Screens.BookScreen.route + "/{epubFilePath}",
            arguments = listOf(navArgument("epubFilePath") { type = NavType.StringType })
        ) { backStackEntry ->
            val epubFilePath = backStackEntry.arguments?.getString("epubFilePath")
            if (epubFilePath != null) {
                BookScreen(epubFilePath)
            }
        }
        composable(Screens.FilePickerScreen.route) {
            FilePickerScreen(navController, context = context)
        }
        composable(
            route = Screens.BookListScreen.route + "/{filePath}",
            arguments = listOf(navArgument("filePath") { type = NavType.StringType })
        ) { backStackEntry ->
            val filePath = backStackEntry.arguments?.getString("filePath")
            filePath?.let { BookList(navController, it) }
        }
    }
}