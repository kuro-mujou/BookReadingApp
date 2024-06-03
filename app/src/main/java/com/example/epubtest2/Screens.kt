package com.example.epubtest2

sealed class Screens (val route : String){
    data object MainRoute : Screens(route = "mainRoute")

    data object MainPage : Screens(route = "mainScreen")

    data object BookPage : Screens(route = "bookScreen")
}