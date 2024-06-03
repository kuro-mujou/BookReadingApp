package com.example.epubtest2

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation

fun NavGraphBuilder.mainGraph(navController: NavController){
    navigation(startDestination = Screens.MainPage.route,route = Screens.MainRoute.route)
    {
        composable(
            route = Screens.MainPage.route
        ) {
            MainScreen(navController)
        }
        composable(
            route = Screens.BookPage.route + "/{epubFilePath}",
            arguments = listOf(navArgument("epubFilePath") { type = NavType.StringType })
        ) { backStackEntry ->
            val epubFilePath = backStackEntry.arguments?.getString("epubFilePath")
            if (epubFilePath != null) {
                BookScreen(epubFilePath)
            }
        }
    }
}