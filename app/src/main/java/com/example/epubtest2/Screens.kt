package com.example.epubtest2

sealed class Screens (val route : String){
    data object MainRoute : Screens(route = "mainRoute")

    data object MainScreen : Screens(route = "mainScreen")

    data object BookScreen : Screens(route = "bookScreen")
    data object FilePickerScreen : Screens(route = "filePicker")
    data object BookListScreen : Screens(route = "filePickerResult")

}